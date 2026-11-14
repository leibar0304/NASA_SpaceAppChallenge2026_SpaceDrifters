package com.example.nasa_spaceappchallenge2026_spacedrifters.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.nasa_spaceappchallenge2026_spacedrifters.R

// Fuentes incluidas en la app (res/font), para que funcionen sin conexión. Licencia SIL OFL 1.1:
// los textos de licencia van en assets/licencias/.

/** Títulos: geométrica, con aire "espacial". */
val SpaceGrotesk = FontFamily(
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.space_grotesk_bold, FontWeight.Bold),
)

/** Texto: muy legible en tamaños pequeños y en pantallas de poca calidad. */
val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
)

/** Cifras (valores, cuentas atrás): monoespaciada, estilo consola de misión. */
val JetBrainsMono = FontFamily(
    Font(R.font.jetbrains_mono_medium, FontWeight.Medium),
)

private val base = Typography()

private fun TextStyle.con(familia: FontFamily, peso: FontWeight) = copy(fontFamily = familia, fontWeight = peso)

/**
 * Space Grotesk en títulos, Inter en el texto y JetBrains Mono en labelMedium (cifras).
 * Tamaños algo mayores que los de Material en los estilos más usados, para que se lea bien en la demo.
 */
val SpaceDriftersTypography = Typography(
    displayLarge = base.displayLarge.con(SpaceGrotesk, FontWeight.Bold),
    displayMedium = base.displayMedium.con(SpaceGrotesk, FontWeight.Bold),
    displaySmall = base.displaySmall.con(SpaceGrotesk, FontWeight.Bold),
    headlineLarge = base.headlineLarge.con(SpaceGrotesk, FontWeight.Bold),
    headlineMedium = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    headlineSmall = base.headlineSmall.con(SpaceGrotesk, FontWeight.Bold),
    titleLarge = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    titleSmall = base.titleSmall.con(Inter, FontWeight.SemiBold),
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodySmall = base.bodySmall.con(Inter, FontWeight.Normal),
    labelLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelSmall = base.labelSmall.con(Inter, FontWeight.Medium),
)
