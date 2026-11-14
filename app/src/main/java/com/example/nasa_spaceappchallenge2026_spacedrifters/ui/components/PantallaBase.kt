package com.example.nasa_spaceappchallenge2026_spacedrifters.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.nasa_spaceappchallenge2026_spacedrifters.R

/**
 * Estructura común de todas las pantallas: barra superior con título (y botón de volver si
 * [onAtras] no es null), el contenido, y el aviso de prototipo abajo.
 * Con [titulo] null no hay barra superior (para pantallas con su propia cabecera, como el
 * selector de rol); el aviso de prototipo sigue siempre.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBase(
    titulo: String?,
    onAtras: (() -> Unit)? = null,
    contenido: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            if (titulo != null) TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    if (onAtras != null) {
                        // IconButton ya garantiza una zona táctil de 48 dp.
                        IconButton(onClick = onAtras) {
                            Icon(
                                painter = painterResource(R.drawable.ic_atras),
                                contentDescription = stringResource(R.string.cd_atras),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        bottomBar = { AvisoPrototipo(Modifier.navigationBarsPadding()) },
        containerColor = MaterialTheme.colorScheme.background,
        content = contenido,
    )
}

/** Contenido provisional para pantallas que aún no están hechas. */
@Composable
fun EnConstruccion(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.pantalla_en_construccion),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
