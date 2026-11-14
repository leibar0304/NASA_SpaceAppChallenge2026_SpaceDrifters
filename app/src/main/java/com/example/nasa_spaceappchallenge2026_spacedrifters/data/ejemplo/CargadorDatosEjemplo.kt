package com.example.nasa_spaceappchallenge2026_spacedrifters.data.ejemplo

import androidx.room.withTransaction
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.AppDatabase
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.Astronauta
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.CargoTripulante
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.RegistroDiario
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.Sintoma
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.repository.CognitivoRepository
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.repository.SesionNueva
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.ejemplo.GeneradorEnsayosEjemplo
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.nback.ConfigNBack
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.nback.GeneradorNBack
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.nback.SecuenciaNBack
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.stroop.ConfigStroop
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.stroop.GeneradorStroop
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Random
import kotlin.math.roundToInt

// ---- Formato de assets/datos_ejemplo.json ----

@Serializable
data class DatosEjemplo(
    val diasMision: Int,
    val tripulacion: List<TripulanteEjemplo>,
)

@Serializable
data class PerfilStroopEjemplo(val tiempoCoincidentesMs: Double, val efectoMs: Double, val desviacionMs: Double)

@Serializable
data class PerfilNBackEjemplo(val dPrima: Double, val desviacion: Double, val tiempoMs: Double)

@Serializable
data class TripulanteEjemplo(
    val id: Long,
    val nombre: String,
    val cargo: CargoTripulante,
    val horasSuenoHabituales: Double,
    /** Semilla del generador: los mismos datos en cada instalación. */
    val semilla: Long,
    /** Antigüedad de su última sesión de hoy (la de Maya, < 18 min, sigue de camino a la Tierra). */
    val minutosDesdeUltimaSesion: Long = 100,
    val stroop: PerfilStroopEjemplo,
    val nback: PerfilNBackEjemplo,
    /** Desviaciones respecto a su media por día de misión; positivo = peor. */
    val stroopZ: List<Double>,
    val nbackZ: List<Double>,
    val horasSueno: List<Double?>,
    val somnolencia: List<Int?>,
    /** Día de misión ("1".."14") → síntomas de ese día. */
    val sintomas: Map<String, List<Sintoma>> = emptyMap(),
)

/**
 * Carga los datos de ejemplo la primera vez que arranca la app (base de datos vacía), para que
 * haya contenido desde el principio. El día 14 de misión es hoy.
 *
 * Los ensayos se generan (GeneradorEnsayosEjemplo, Java) y se guardan con [CognitivoRepository]
 * igual que una prueba real, en orden: prácticas → línea base → misión. Así los resúmenes, las
 * líneas base y los estados salen de los ensayos.
 */
class CargadorDatosEjemplo(
    private val db: AppDatabase,
    private val repositorio: CognitivoRepository,
) {
    private val configStroop = ConfigStroop()
    private val configNBack = ConfigNBack()

    /** Devuelve true si ha cargado datos; false si ya había tripulación. */
    suspend fun cargarSiVacia(
        json: String,
        ahora: Long = System.currentTimeMillis(),
        zona: ZoneId = ZoneId.systemDefault(),
    ): Boolean {
        if (db.tripulacionDao().contarAstronautas() > 0) return false
        val datos = JSON.decodeFromString<DatosEjemplo>(json)
        db.withTransaction {
            datos.tripulacion.forEachIndexed { orden, t -> cargar(t, orden, datos.diasMision, ahora, zona) }
        }
        return true
    }

    private suspend fun cargar(t: TripulanteEjemplo, orden: Int, dias: Int, ahora: Long, zona: ZoneId) {
        listOf(t.stroopZ, t.nbackZ, t.horasSueno, t.somnolencia).forEach {
            require(it.size == dias) { "${t.nombre}: se esperaban $dias días y hay ${it.size}" }
        }
        db.tripulacionDao().insertarAstronautas(listOf(Astronauta(t.id, t.nombre, t.cargo, t.horasSuenoHabituales)))

        val random = Random(t.semilla)
        val hoy = Instant.ofEpochMilli(ahora).atZone(zona).toLocalDate()
        val lanzamiento = hoy.minusDays(dias - 1L)
        fun a(fecha: LocalDate, hora: Int, minuto: Int) = fecha.atTime(hora, minuto).atZone(zona).toInstant().toEpochMilli()

        // Familiarización, 4 semanas antes del lanzamiento: Stroop + las dos prácticas del N-Back.
        val diaPractica = lanzamiento.minusDays(28)
        stroop(t, a(diaPractica, 9, 0), esPractica = true, esLineaBase = false, z = 0.0, random = random)
        nbackPerfecto(t, a(diaPractica, 9, 10), GeneradorNBack.practica1Back(configNBack, random), random)
        nbackPerfecto(t, a(diaPractica, 9, 15), GeneradorNBack.practica2Back(configNBack, random), random)

        // Línea base antes del lanzamiento: media − 1, media y media + 1 desviación.
        listOf(21L, 14L, 7L).zip(listOf(-1.0, 0.0, 1.0)).forEach { (diasAntes, z) ->
            val fecha = lanzamiento.minusDays(diasAntes)
            stroop(t, a(fecha, 9, 0), esPractica = false, esLineaBase = true, z = z, random = random)
            nback(t, a(fecha, 9, 20), esLineaBase = true, z = z, random = random)
        }

        // Misión: una sesión diaria de cada prueba. Las de hoy, en el pasado reciente.
        val inicioHoy = hoy.atStartOfDay(zona).toInstant().toEpochMilli()
        for (dia in 1..dias) {
            val fecha = lanzamiento.plusDays(dia - 1L)
            val (inicioStroop, inicioNBack) = if (fecha == hoy) {
                val nback = (ahora - t.minutosDesdeUltimaSesion * MINUTO).coerceIn(inicioHoy, ahora - MINUTO)
                (nback - 35 * MINUTO).coerceAtLeast(inicioHoy) to nback
            } else {
                a(fecha, 8, 5 * orden) to a(fecha, 8, 20 + 5 * orden)
            }
            stroop(t, inicioStroop, esPractica = false, esLineaBase = false, z = t.stroopZ[dia - 1], random = random)
            nback(t, inicioNBack, esLineaBase = false, z = t.nbackZ[dia - 1], random = random)
        }

        db.tripulacionDao().insertarRegistros(
            (1..dias).map { dia ->
                RegistroDiario(
                    astronautaId = t.id,
                    fecha = lanzamiento.plusDays(dia - 1L).toString(),
                    horasSueno = t.horasSueno[dia - 1],
                    somnolencia = t.somnolencia[dia - 1],
                    sintomas = RegistroDiario.unirSintomas(t.sintomas[dia.toString()].orEmpty()),
                )
            },
        )
    }

    /** Stroop a [z] desviaciones de su media (positivo = más efecto, más lento y más errores). */
    private suspend fun stroop(t: TripulanteEjemplo, inicio: Long, esPractica: Boolean, esLineaBase: Boolean, z: Double, random: Random) {
        val secuencia = if (esPractica) GeneradorStroop.practica(configStroop, random) else GeneradorStroop.sesion(configStroop, random)
        val resultados = GeneradorEnsayosEjemplo.stroop(
            secuencia,
            t.stroop.tiempoCoincidentesMs + z * 15,
            t.stroop.efectoMs + z * t.stroop.desviacionMs,
            if (esPractica) 0 else (1 + z).roundToInt().coerceIn(0, 6),
            if (z > 2) 1 else 0,
            random,
        )
        repositorio.guardarStroop(sesion(t, inicio, esPractica, esLineaBase), resultados)
    }

    /** N-Back 2-back con la d′ más cercana a media − [z] desviaciones. */
    private suspend fun nback(t: TripulanteEjemplo, inicio: Long, esLineaBase: Boolean, z: Double, random: Random) {
        val secuencia = GeneradorNBack.sesion(configNBack, random)
        val coincidencias = secuencia.contarCoincidencias()
        val (aciertos, falsasAlarmas) = GeneradorEnsayosEjemplo.aciertosYFalsasAlarmasPara(
            t.nback.dPrima - z * t.nback.desviacion,
            coincidencias,
            configNBack.puntuables - coincidencias,
        ).let { it[0] to it[1] }
        val resultados = GeneradorEnsayosEjemplo.nback(secuencia, aciertos, falsasAlarmas, t.nback.tiempoMs + z * 30, random)
        repositorio.guardarNBack(sesion(t, inicio, esPractica = false, esLineaBase = esLineaBase), resultados, secuencia.nivel)
    }

    /** Práctica del N-Back sin fallos. */
    private suspend fun nbackPerfecto(t: TripulanteEjemplo, inicio: Long, secuencia: SecuenciaNBack, random: Random) {
        val resultados = GeneradorEnsayosEjemplo.nback(secuencia, secuencia.contarCoincidencias(), 0, t.nback.tiempoMs, random)
        repositorio.guardarNBack(sesion(t, inicio, esPractica = true, esLineaBase = false), resultados, secuencia.nivel)
    }

    private fun sesion(t: TripulanteEjemplo, inicio: Long, esPractica: Boolean, esLineaBase: Boolean) = SesionNueva(
        astronautaId = t.id,
        inicioEn = inicio,
        finEn = inicio + 2 * MINUTO,
        esPractica = esPractica,
        valida = true,
        esLineaBase = esLineaBase,
    )

    companion object {
        const val ARCHIVO = "datos_ejemplo.json"
        private const val MINUTO = 60_000L
        private val JSON = Json { ignoreUnknownKeys = true }
    }
}
