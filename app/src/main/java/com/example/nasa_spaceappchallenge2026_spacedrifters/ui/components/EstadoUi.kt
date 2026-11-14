package com.example.nasa_spaceappchallenge2026_spacedrifters.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.nasa_spaceappchallenge2026_spacedrifters.R
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.EstadoCognitivo
import com.example.nasa_spaceappchallenge2026_spacedrifters.ui.theme.LocalEstadoColores

/** Texto de un estado. Siempre se muestra junto a su color, nunca el color solo. */
@StringRes
fun etiquetaEstado(estado: EstadoCognitivo): Int = when (estado) {
    EstadoCognitivo.NORMAL -> R.string.estado_normal
    EstadoCognitivo.VIGILAR -> R.string.estado_vigilar
    EstadoCognitivo.ALERTA -> R.string.estado_alerta
    EstadoCognitivo.SIN_DATOS -> R.string.estado_sin_datos
    EstadoCognitivo.SIN_LINEA_BASE -> R.string.estado_sin_linea_base
}

/** Color de un estado (sin datos y sin línea base comparten el gris: nunca parecen "bien"). */
@Composable
fun colorEstado(estado: EstadoCognitivo): Color {
    val c = LocalEstadoColores.current
    return when (estado) {
        EstadoCognitivo.NORMAL -> c.normal
        EstadoCognitivo.VIGILAR -> c.vigilar
        EstadoCognitivo.ALERTA -> c.alerta
        EstadoCognitivo.SIN_DATOS, EstadoCognitivo.SIN_LINEA_BASE -> c.sinDatos
    }
}

/** Estado como punto de color + texto. [texto] permite envolverlo ("Última: Vigilar"). */
@Composable
fun MarcaEstado(estado: EstadoCognitivo, modifier: Modifier = Modifier, texto: String = stringResource(etiquetaEstado(estado))) {
    val color = colorEstado(estado)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(10.dp)
                .background(color, CircleShape),
        )
        Text(text = texto, style = MaterialTheme.typography.labelLarge, color = color)
    }
}
