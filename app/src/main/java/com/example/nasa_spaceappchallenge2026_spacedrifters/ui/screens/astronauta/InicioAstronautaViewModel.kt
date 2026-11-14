package com.example.nasa_spaceappchallenge2026_spacedrifters.ui.screens.astronauta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.LineaBaseCognitiva
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.ResumenSesion
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.TipoPrueba
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.repository.CognitivoRepository
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.repository.TripulacionRepository
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.EstadoCognitivo
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.Rendimiento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/** Panel del astronauta: cabecera, pruebas de hoy y rendimiento de los últimos 30 días. */
class InicioAstronautaViewModel(
    cognitivo: CognitivoRepository,
    tripulacion: TripulacionRepository,
    astronautaId: Long,
    private val retrasoMin: Long,
    private val zona: ZoneId = ZoneId.systemDefault(),
) : ViewModel() {

    private val hoy: LocalDate = LocalDate.now(zona)
    private val inicioDeHoy: Long = hoy.atStartOfDay(zona).toInstant().toEpochMilli()
    private val inicioHace30Dias: Long = hoy.minusDays(Periodo.MES.dias - 1L).atStartOfDay(zona).toInstant().toEpochMilli()

    val panel: StateFlow<PanelAstronautaUi> = combine(
        tripulacion.astronauta(astronautaId),
        cognitivo.inicioMision(astronautaId),
        cognitivo.tiposHechosDesde(astronautaId, inicioDeHoy),
        rendimiento(cognitivo, astronautaId, TipoPrueba.STROOP),
        rendimiento(cognitivo, astronautaId, TipoPrueba.NBACK),
    ) { astronauta, inicioMision, hechas, stroop, nback ->
        PanelAstronautaUi(
            nombre = astronauta?.nombre,
            cargo = astronauta?.cargo,
            diaMision = inicioMision?.let { diaDeMision(it) },
            retrasoMin = retrasoMin,
            hechasHoy = hechas,
            rendimiento = listOf(stroop, nback),
        )
    }
        .catch { emit(PanelAstronautaUi(retrasoMin = retrasoMin)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PanelAstronautaUi(retrasoMin = retrasoMin))

    private fun rendimiento(cognitivo: CognitivoRepository, astronautaId: Long, tipo: TipoPrueba): Flow<RendimientoUi> =
        combine(
            cognitivo.resumenesMisionDesde(astronautaId, tipo, inicioHace30Dias),
            cognitivo.lineaBasePrincipal(astronautaId, tipo),
        ) { resumenes, base -> aRendimiento(tipo, resumenes, base) }

    private fun diaDeMision(inicio: Long): Int {
        val primerDia = Instant.ofEpochMilli(inicio).atZone(zona).toLocalDate()
        return ChronoUnit.DAYS.between(primerDia, hoy).toInt() + 1
    }

    companion object {
        /** Resúmenes → puntos de la gráfica ("más alto = mejor"). Las sesiones sin métrica no se pintan. */
        fun aRendimiento(tipo: TipoPrueba, resumenes: List<ResumenSesion>, base: LineaBaseCognitiva?): RendimientoUi {
            val direccion = CognitivoRepository.direccion(tipo)
            val puntos = resumenes.mapNotNull { r ->
                val valor = when (tipo) {
                    TipoPrueba.STROOP -> r.efectoStroopMs
                    TipoPrueba.NBACK -> r.dPrima
                } ?: return@mapNotNull null
                val puntuacion = if (base != null) {
                    Rendimiento.puntuacion(valor, base.media, base.desviacion, direccion)
                } else {
                    Rendimiento.orientado(valor, direccion)
                }
                val estado = EstadoCognitivo.entries.firstOrNull { it.name == r.estado } ?: EstadoCognitivo.SIN_DATOS
                PuntoRendimiento(r.fechaEn, puntuacion, estado)
            }
            return RendimientoUi(tipo, tieneLineaBase = base != null, puntos = puntos)
        }
    }
}
