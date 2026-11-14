package com.example.nasa_spaceappchallenge2026_spacedrifters.ui.screens.astronauta

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nasa_spaceappchallenge2026_spacedrifters.R
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.CargoTripulante
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.TipoPrueba
import com.example.nasa_spaceappchallenge2026_spacedrifters.di.AppViewModelProvider
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.EstadoCognitivo
import com.example.nasa_spaceappchallenge2026_spacedrifters.ui.components.MarcaEstado
import com.example.nasa_spaceappchallenge2026_spacedrifters.ui.components.PantallaBase
import com.example.nasa_spaceappchallenge2026_spacedrifters.ui.components.etiquetaEstado
import com.example.nasa_spaceappchallenge2026_spacedrifters.ui.theme.LocalEstadoColores
import com.example.nasa_spaceappchallenge2026_spacedrifters.ui.theme.SpaceDriftersTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/** Panel del astronauta: quién es, en qué día de misión está, sus pruebas de hoy y su rendimiento. */
@Composable
fun InicioAstronautaScreen(
    onAtras: () -> Unit,
    onEmpezarStroop: () -> Unit,
    onEmpezarNBack: () -> Unit,
    viewModel: InicioAstronautaViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val panel by viewModel.panel.collectAsStateWithLifecycle()
    PanelAstronautaContenido(
        panel = panel,
        onAtras = onAtras,
        onEmpezarStroop = onEmpezarStroop,
        onEmpezarNBack = onEmpezarNBack,
    )
}

@Composable
private fun PanelAstronautaContenido(
    panel: PanelAstronautaUi,
    onAtras: () -> Unit,
    onEmpezarStroop: () -> Unit,
    onEmpezarNBack: () -> Unit,
    ahora: Long = remember { System.currentTimeMillis() },
) {
    var periodo by rememberSaveable { mutableStateOf(Periodo.SEMANA) }
    PantallaBase(
        titulo = stringResource(R.string.astronauta_inicio_titulo),
        onAtras = onAtras,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CabeceraPanel(panel)
            TarjetaPruebasDeHoy(
                hechasHoy = panel.hechasHoy,
                onEmpezarStroop = onEmpezarStroop,
                onEmpezarNBack = onEmpezarNBack,
            )
            MiRendimiento(panel.rendimiento, periodo, onPeriodo = { periodo = it }, ahora = ahora)
        }
    }
}

// ---- Cabecera ----

@Composable
private fun CabeceraPanel(panel: PanelAstronautaUi) {
    val sinDatos = stringResource(R.string.estado_sin_datos)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Iniciales en un parche circular.
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = panel.nombre?.let(::iniciales) ?: "–",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = panel.nombre ?: sinDatos, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = panel.cargo?.let { stringResource(nombreCargo(it)) } ?: sinDatos,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = buildList {
                    add(panel.diaMision?.let { stringResource(R.string.panel_dia_mision, it) } ?: sinDatos)
                    panel.retrasoMin?.let { add(stringResource(R.string.panel_retraso, it)) }
                }.joinToString("  ·  "),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

private fun iniciales(nombre: String): String =
    nombre.split(' ').filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }

@StringRes
private fun nombreCargo(cargo: CargoTripulante): Int = when (cargo) {
    CargoTripulante.COMANDANTE -> R.string.cargo_comandante
    CargoTripulante.INGENIERIA_VUELO -> R.string.cargo_ingenieria_vuelo
    CargoTripulante.OFICIAL_MEDICO -> R.string.cargo_oficial_medico
    CargoTripulante.ESPECIALISTA_MISION -> R.string.cargo_especialista_mision
}

// ---- Pruebas de hoy ----

@Composable
private fun TarjetaPruebasDeHoy(hechasHoy: Set<TipoPrueba>, onEmpezarStroop: () -> Unit, onEmpezarNBack: () -> Unit) {
    Card(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = stringResource(R.string.pruebas_hoy_titulo), style = MaterialTheme.typography.titleLarge)
            FilaPrueba(
                nombre = stringResource(R.string.stroop_nombre),
                descripcion = stringResource(R.string.stroop_descripcion_corta),
                hecha = TipoPrueba.STROOP in hechasHoy,
                onEmpezar = onEmpezarStroop,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            FilaPrueba(
                nombre = stringResource(R.string.nback_nombre),
                descripcion = stringResource(R.string.nback_descripcion_corta),
                hecha = TipoPrueba.NBACK in hechasHoy,
                onEmpezar = onEmpezarNBack,
            )
        }
    }
}

@Composable
private fun FilaPrueba(nombre: String, descripcion: String, hecha: Boolean, onEmpezar: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = nombre, style = MaterialTheme.typography.titleMedium)
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (hecha) {
                Text(
                    text = stringResource(R.string.prueba_hecha_hoy),
                    style = MaterialTheme.typography.labelLarge,
                    color = LocalEstadoColores.current.normal,
                )
            }
        }
        if (hecha) {
            OutlinedButton(onClick = onEmpezar, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(stringResource(R.string.repetir))
            }
        } else {
            Button(onClick = onEmpezar, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(stringResource(R.string.empezar))
            }
        }
    }
}

// ---- Mi rendimiento ----

@Composable
private fun MiRendimiento(
    rendimiento: List<RendimientoUi>,
    periodo: Periodo,
    onPeriodo: (Periodo) -> Unit,
    ahora: Long,
) {
    val zona = remember { ZoneId.systemDefault() }
    val desde = remember(periodo, ahora) {
        Instant.ofEpochMilli(ahora).atZone(zona).toLocalDate()
            .minusDays(periodo.dias - 1L).atStartOfDay(zona).toInstant().toEpochMilli()
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.mi_rendimiento_titulo),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
            )
            Periodo.entries.forEach { p ->
                FilterChip(
                    selected = p == periodo,
                    onClick = { onPeriodo(p) },
                    // FilterChip ya reserva una zona táctil de 48 dp aunque se vea más bajo.
                    label = { Text(pluralStringResource(R.plurals.periodo_dias, p.dias, p.dias)) },
                )
            }
        }
        rendimiento.forEach { r ->
            TarjetaRendimiento(r, puntos = r.puntos.filter { it.fechaEn >= desde }, periodo = periodo, desde = desde, hasta = ahora)
        }
    }
}

@Composable
private fun TarjetaRendimiento(r: RendimientoUi, puntos: List<PuntoRendimiento>, periodo: Periodo, desde: Long, hasta: Long) {
    val nombre = stringResource(if (r.tipo == TipoPrueba.STROOP) R.string.stroop_nombre else R.string.nback_nombre)
    val resumen = resumenRendimiento(r.tieneLineaBase, puntos)
    Card(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = nombre, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                puntos.lastOrNull()?.let { ultimo ->
                    MarcaEstado(
                        estado = ultimo.estado,
                        texto = stringResource(R.string.ultima_sesion, stringResource(etiquetaEstado(ultimo.estado))),
                    )
                }
            }
            Text(
                text = stringResource(R.string.grafica_mejor),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            GraficaRendimiento(
                puntos = puntos,
                tieneLineaBase = r.tieneLineaBase,
                desde = desde,
                hasta = hasta,
                descripcion = pluralStringResource(R.plurals.cd_grafica_rendimiento, periodo.dias, nombre, periodo.dias, resumen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = fechaCorta(desde),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(R.string.grafica_hoy),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (r.tieneLineaBase) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(width = 18.dp, height = 10.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f), RoundedCornerShape(2.dp)),
                    )
                    Text(
                        text = stringResource(R.string.rango_habitual),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Text(text = resumen, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/** Frase para el astronauta sobre el periodo, sin números en bruto. */
@Composable
private fun resumenRendimiento(tieneLineaBase: Boolean, puntos: List<PuntoRendimiento>): String {
    val peor = puntos.count { it.estado == EstadoCognitivo.VIGILAR || it.estado == EstadoCognitivo.ALERTA }
    return when {
        puntos.isEmpty() -> stringResource(R.string.rendimiento_sin_sesiones)
        !tieneLineaBase -> stringResource(R.string.resultado_sin_linea_base)
        peor == 0 -> stringResource(R.string.rendimiento_dentro)
        else -> pluralStringResource(R.plurals.rendimiento_fuera, puntos.size, peor, puntos.size)
    }
}

private fun fechaCorta(instante: Long): String =
    Instant.ofEpochMilli(instante).atZone(ZoneId.systemDefault()).toLocalDate()
        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))

// ---- Vista previa con datos inventados (no necesita base de datos) ----

@Preview(heightDp = 1400)
@Composable
private fun PanelAstronautaPreview() {
    val dia = 86_400_000L
    val ahora = LocalDate.of(2026, 11, 14).atTime(12, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    fun puntos(vararg z: Double) = z.mapIndexed { i, v ->
        val estado = when {
            v >= -1 -> EstadoCognitivo.NORMAL
            v >= -2 -> EstadoCognitivo.VIGILAR
            else -> EstadoCognitivo.ALERTA
        }
        PuntoRendimiento(ahora - (z.size - 1 - i) * dia, v, estado)
    }
    SpaceDriftersTheme {
        PanelAstronautaContenido(
            panel = PanelAstronautaUi(
                nombre = "Maya Okafor",
                cargo = CargoTripulante.COMANDANTE,
                diaMision = 14,
                retrasoMin = 18,
                hechasHoy = setOf(TipoPrueba.STROOP),
                rendimiento = listOf(
                    RendimientoUi(TipoPrueba.STROOP, true, puntos(0.3, -0.4, 0.1, -0.5, 0.2, 0.0, -0.4, 0.5, -0.2, 0.0, -0.3, -0.5, -0.7, -0.6)),
                    RendimientoUi(TipoPrueba.NBACK, true, puntos(0.2, -0.3, 0.4, 0.0, -0.5, 0.3, 0.1, -0.2, 0.4, 0.0, -1.5, -2.0, -2.3, -2.6)),
                ),
            ),
            onAtras = {},
            onEmpezarStroop = {},
            onEmpezarNBack = {},
            ahora = ahora,
        )
    }
}
