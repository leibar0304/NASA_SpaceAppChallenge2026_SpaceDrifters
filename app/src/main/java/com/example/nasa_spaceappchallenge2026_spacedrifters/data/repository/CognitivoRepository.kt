package com.example.nasa_spaceappchallenge2026_spacedrifters.data.repository

import androidx.room.withTransaction
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.EnsayoNBack
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.EnsayoStroop
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.LineaBaseCognitiva
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.ResumenSesion
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.SesionPrueba
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.TipoPrueba
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.ConfigCognitiva
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.EstadoCognitivo
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.EvaluadorEstado
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.LineaBase
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.nback.MetricasNBack
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.nback.ResultadoEnsayoNBack
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.stroop.MetricasStroop
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.stroop.ResultadoEnsayoStroop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Datos de una sesión recién terminada (o interrumpida) que hay que guardar. */
data class SesionNueva(
    val astronautaId: Long,
    val inicioEn: Long,
    val finEn: Long,
    val esPractica: Boolean,
    val valida: Boolean,
    val esLineaBase: Boolean = false,
) {
    /** Solo las sesiones reales y válidas tienen resumen y cuentan para todo lo demás. */
    val cuenta: Boolean get() = !esPractica && valida
}

/**
 * Guardado y lectura de las pruebas cognitivas. Cada sesión se guarda en una transacción:
 * sesión + ensayos + (si cuenta) resumen con su estado frente a la línea base.
 * Las métricas y el estado se calculan en domain/cognitivo (Java).
 */
class CognitivoRepository(
    private val db: AppDatabase,
    private val config: ConfigCognitiva,
) {
    private val dao = db.cognitivoDao()

    /**
     * Si ya completó la práctica de esta prueba. El N-Back tiene dos prácticas (1-back y 2-back)
     * y cada una se guarda como una sesión, así que hacen falta dos válidas.
     */
    suspend fun haHechoPractica(astronautaId: Long, tipo: TipoPrueba): Boolean =
        dao.contarPracticasValidas(astronautaId, tipo) >= practicasNecesarias(tipo)

    /** Pruebas reales y válidas hechas desde [desde] (epoch ms). */
    fun tiposHechosDesde(astronautaId: Long, desde: Long): Flow<Set<TipoPrueba>> =
        dao.tiposHechosDesde(astronautaId, desde).map { it.toSet() }

    /** Guarda una sesión Stroop. Devuelve el estado frente a la línea base, o null si no cuenta. */
    suspend fun guardarStroop(sesion: SesionNueva, resultados: List<ResultadoEnsayoStroop>): EstadoCognitivo? =
        db.withTransaction {
            val sesionId = dao.insertarSesion(sesion.aEntidad(TipoPrueba.STROOP))
            dao.insertarEnsayosStroop(
                resultados.map { r ->
                    EnsayoStroop(
                        sesionId = sesionId,
                        numero = r.numero,
                        palabra = r.estimulo.palabra.name,
                        colorTinta = r.estimulo.tinta.name,
                        coincidente = r.estimulo.esCoincidente(),
                        respuesta = r.respuesta?.name,
                        correcto = r.esCorrecto(),
                        tiempoMs = r.tiempoMs,
                    )
                },
            )
            if (!sesion.cuenta) return@withTransaction null

            val m = MetricasStroop.calcular(resultados)
            val estado = estadoFrenteALineaBase(sesion.astronautaId, TipoPrueba.STROOP, m.efectoStroopMs)
            dao.guardarResumen(
                ResumenSesion(
                    sesionId = sesionId,
                    astronautaId = sesion.astronautaId,
                    tipo = TipoPrueba.STROOP,
                    fechaEn = sesion.inicioEn,
                    llegaTierraEn = llegaTierraEn(sesion),
                    tiempoMedioMs = m.tiempoMedioMs,
                    porcentajeAciertos = m.porcentajeAciertos,
                    efectoStroopMs = m.efectoStroopMs,
                    erroresNoCoincidentes = m.erroresNoCoincidentes,
                    estado = estado.name,
                ),
            )
            if (sesion.esLineaBase) recalcularLineaBaseSinTransaccion(sesion.astronautaId, TipoPrueba.STROOP)
            estado
        }

    /** Guarda una sesión N-Back de nivel [nivel]. Devuelve el estado, o null si no cuenta. */
    suspend fun guardarNBack(
        sesion: SesionNueva,
        resultados: List<ResultadoEnsayoNBack>,
        nivel: Int,
    ): EstadoCognitivo? = db.withTransaction {
        val sesionId = dao.insertarSesion(sesion.aEntidad(TipoPrueba.NBACK))
        dao.insertarEnsayosNBack(
            resultados.map { r ->
                EnsayoNBack(
                    sesionId = sesionId,
                    numero = r.numero,
                    estimulo = r.forma.name,
                    esCoincidencia = r.esCoincidencia(),
                    pulsado = r.esPulsado(),
                    tiempoMs = r.tiempoMs,
                )
            },
        )
        if (!sesion.cuenta) return@withTransaction null

        val m = MetricasNBack.calcular(resultados, nivel)
        val estado = estadoFrenteALineaBase(sesion.astronautaId, TipoPrueba.NBACK, m.dPrima)
        dao.guardarResumen(
            ResumenSesion(
                sesionId = sesionId,
                astronautaId = sesion.astronautaId,
                tipo = TipoPrueba.NBACK,
                fechaEn = sesion.inicioEn,
                llegaTierraEn = llegaTierraEn(sesion),
                aciertosCoincidencias = m.aciertosCoincidencias,
                falsasAlarmas = m.falsasAlarmas,
                dPrima = m.dPrima,
                tiempoAciertosMs = m.tiempoAciertosMs,
                estado = estado.name,
            ),
        )
        if (sesion.esLineaBase) recalcularLineaBaseSinTransaccion(sesion.astronautaId, TipoPrueba.NBACK)
        estado
    }

    /** Recalcula y guarda la línea base de todas las métricas de una prueba. */
    suspend fun recalcularLineaBase(astronautaId: Long, tipo: TipoPrueba) {
        db.withTransaction { recalcularLineaBaseSinTransaccion(astronautaId, tipo) }
    }

    private suspend fun recalcularLineaBaseSinTransaccion(astronautaId: Long, tipo: TipoPrueba) {
        val resumenes = dao.resumenesLineaBase(astronautaId, tipo)
        METRICAS.getValue(tipo).forEach { (metrica, extraer) ->
            val base = LineaBase.calcular(resumenes.map(extraer), config.minSesionesLineaBase) ?: return@forEach
            dao.guardarLineaBase(
                LineaBaseCognitiva(
                    astronautaId = astronautaId,
                    tipo = tipo,
                    metrica = metrica,
                    media = base.media,
                    desviacion = base.desviacion,
                    nSesiones = base.nSesiones,
                ),
            )
        }
    }

    private suspend fun estadoFrenteALineaBase(astronautaId: Long, tipo: TipoPrueba, valor: Double?): EstadoCognitivo {
        val guardada = dao.lineaBaseDe(astronautaId, tipo, metricaPrincipal(tipo))
        val base = guardada?.let { LineaBase(it.media, it.desviacion, it.nSesiones) }
        return EvaluadorEstado.evaluar(valor, base, direccion(tipo), config)
    }

    private fun llegaTierraEn(sesion: SesionNueva) = sesion.inicioEn + config.retrasoMisionMs

    private fun SesionNueva.aEntidad(tipo: TipoPrueba) = SesionPrueba(
        astronautaId = astronautaId,
        tipo = tipo,
        inicioEn = inicioEn,
        finEn = finEn,
        esPractica = esPractica,
        esLineaBase = esLineaBase,
        valida = valida,
        llegaTierraEn = llegaTierraEn(this),
    )

    companion object {
        const val METRICA_EFECTO_STROOP = "efectoStroopMs"
        const val METRICA_D_PRIMA = "dPrima"

        fun metricaPrincipal(tipo: TipoPrueba) = when (tipo) {
            TipoPrueba.STROOP -> METRICA_EFECTO_STROOP
            TipoPrueba.NBACK -> METRICA_D_PRIMA
        }

        /** Para el efecto Stroop más alto es peor; para d′, más bajo. */
        fun direccion(tipo: TipoPrueba) = when (tipo) {
            TipoPrueba.STROOP -> EvaluadorEstado.Direccion.MAYOR_ES_PEOR
            TipoPrueba.NBACK -> EvaluadorEstado.Direccion.MENOR_ES_PEOR
        }

        private fun practicasNecesarias(tipo: TipoPrueba) = when (tipo) {
            TipoPrueba.STROOP -> 1
            TipoPrueba.NBACK -> 2
        }

        /** Métricas con línea base de cada prueba (nombre → cómo sacarla del resumen). */
        private val METRICAS: Map<TipoPrueba, List<Pair<String, (ResumenSesion) -> Double?>>> = mapOf(
            TipoPrueba.STROOP to listOf(
                "tiempoMedioMs" to { r -> r.tiempoMedioMs },
                "porcentajeAciertos" to { r -> r.porcentajeAciertos },
                METRICA_EFECTO_STROOP to { r -> r.efectoStroopMs },
                "erroresNoCoincidentes" to { r -> r.erroresNoCoincidentes?.toDouble() },
            ),
            TipoPrueba.NBACK to listOf(
                "aciertosCoincidencias" to { r -> r.aciertosCoincidencias },
                "falsasAlarmas" to { r -> r.falsasAlarmas?.toDouble() },
                METRICA_D_PRIMA to { r -> r.dPrima },
                "tiempoAciertosMs" to { r -> r.tiempoAciertosMs },
            ),
        )
    }
}
