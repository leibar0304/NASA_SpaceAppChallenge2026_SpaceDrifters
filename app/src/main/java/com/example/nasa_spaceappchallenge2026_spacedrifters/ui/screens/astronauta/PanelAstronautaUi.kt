package com.example.nasa_spaceappchallenge2026_spacedrifters.ui.screens.astronauta

import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.CargoTripulante
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.TipoPrueba
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.EstadoCognitivo

/**
 * Todo lo que pinta el panel del astronauta. Las pantallas solo conocen estos modelos, no las
 * tablas: si cambia la base de datos, solo cambia cómo los rellena el ViewModel.
 * Los null se muestran como "Sin datos", nunca como si estuviera bien.
 */
data class PanelAstronautaUi(
    val nombre: String? = null,
    val cargo: CargoTripulante? = null,
    /** Día de misión (1 = primer día), o null si aún no hay sesiones de misión. */
    val diaMision: Int? = null,
    val retrasoMin: Long? = null,
    val hechasHoy: Set<TipoPrueba> = emptySet(),
    /** Una entrada por prueba, con las sesiones de los últimos 30 días. */
    val rendimiento: List<RendimientoUi> = emptyList(),
)

data class RendimientoUi(
    val tipo: TipoPrueba,
    /** Con línea base, la puntuación de los puntos está en desviaciones y el rango habitual es −1..+1. */
    val tieneLineaBase: Boolean,
    /** Sesiones de misión, de la más antigua a la más reciente. */
    val puntos: List<PuntoRendimiento>,
)

data class PuntoRendimiento(
    val fechaEn: Long,
    /** Más alto = mejor, en todas las pruebas (ver domain/cognitivo/Rendimiento). */
    val puntuacion: Double,
    val estado: EstadoCognitivo,
)

/** Periodos que puede elegir el astronauta en "Mi rendimiento". */
enum class Periodo(val dias: Int) { SEMANA(7), MES(30) }
