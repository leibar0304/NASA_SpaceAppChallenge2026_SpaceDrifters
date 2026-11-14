package com.example.nasa_spaceappchallenge2026_spacedrifters.ui.screens.astronauta

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.nasa_spaceappchallenge2026_spacedrifters.ui.components.colorEstado
import kotlin.math.max
import kotlin.math.min

/**
 * Gráfica de rendimiento del astronauta: un punto por sesión (color = estado) entre [desde] y
 * [hasta], con "mejor" hacia arriba. Con línea base, la franja sombreada es su rango habitual
 * (puntuación −1..+1) y la línea discontinua, su media. Sin ejes numéricos: no se enseñan
 * números en bruto al astronauta. [descripcion] es el resumen para lectores de pantalla.
 */
@Composable
fun GraficaRendimiento(
    puntos: List<PuntoRendimiento>,
    tieneLineaBase: Boolean,
    desde: Long,
    hasta: Long,
    descripcion: String,
    modifier: Modifier = Modifier,
) {
    val colorBanda = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
    val colorMedia = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
    val colorLinea = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    val colorBorde = MaterialTheme.colorScheme.onBackground
    val colorFondo = MaterialTheme.colorScheme.surfaceContainer
    val colores = puntos.map { colorEstado(it.estado) }

    Canvas(modifier.semantics { contentDescription = descripcion }) {
        val margen = 10.dp.toPx()
        val ancho = size.width - 2 * margen
        val alto = size.height - 2 * margen
        val (minimo, maximo) = escala(puntos, tieneLineaBase)

        fun y(v: Double): Float = margen + alto * (1f - ((v.coerceIn(minimo, maximo) - minimo) / (maximo - minimo)).toFloat())
        fun x(t: Long): Float =
            if (hasta <= desde) margen + ancho / 2 else margen + ancho * ((t - desde).toFloat() / (hasta - desde)).coerceIn(0f, 1f)

        if (tieneLineaBase) {
            val arriba = y(1.0)
            drawRect(colorBanda, topLeft = Offset(0f, arriba), size = Size(size.width, y(-1.0) - arriba))
            drawLine(
                colorMedia,
                start = Offset(0f, y(0.0)),
                end = Offset(size.width, y(0.0)),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())),
            )
        }

        if (puntos.size > 1) {
            val linea = Path()
            puntos.forEachIndexed { i, p ->
                if (i == 0) linea.moveTo(x(p.fechaEn), y(p.puntuacion)) else linea.lineTo(x(p.fechaEn), y(p.puntuacion))
            }
            drawPath(linea, colorLinea, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        }

        puntos.forEachIndexed { i, p ->
            val centro = Offset(x(p.fechaEn), y(p.puntuacion))
            val ultimo = i == puntos.lastIndex
            if (ultimo) {
                // La sesión más reciente, destacada con un anillo.
                drawCircle(colorBorde, radius = 7.dp.toPx(), center = centro)
                drawCircle(colorFondo, radius = 5.5.dp.toPx(), center = centro)
                drawCircle(colores[i], radius = 4.5.dp.toPx(), center = centro)
            } else {
                drawCircle(colores[i], radius = 4.dp.toPx(), center = centro)
            }
        }
    }
}

/**
 * Rango vertical. Con línea base, al menos de −3 a +3 desviaciones (se ve la franja y lo que se
 * sale de ella) y como mucho ±6 (lo más extremo se pinta en el borde). Sin línea base, el de los datos.
 */
private fun escala(puntos: List<PuntoRendimiento>, tieneLineaBase: Boolean): Pair<Double, Double> {
    val valores = puntos.map { it.puntuacion }
    if (tieneLineaBase) {
        val bajo = min(-3.0, (valores.minOrNull() ?: 0.0) - 0.5).coerceAtLeast(-6.0)
        val alto = max(3.0, (valores.maxOrNull() ?: 0.0) + 0.5).coerceAtMost(6.0)
        return bajo to alto
    }
    val bajo = valores.minOrNull() ?: return -1.0 to 1.0
    val alto = valores.maxOrNull() ?: return -1.0 to 1.0
    if (alto - bajo < 1e-9) return bajo - 1.0 to alto + 1.0
    val holgura = (alto - bajo) * 0.15
    return bajo - holgura to alto + holgura
}
