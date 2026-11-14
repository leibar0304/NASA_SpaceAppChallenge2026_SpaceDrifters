package com.example.nasa_spaceappchallenge2026_spacedrifters.ui.screens.rol

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nasa_spaceappchallenge2026_spacedrifters.R
import com.example.nasa_spaceappchallenge2026_spacedrifters.ui.components.PantallaBase
import com.example.nasa_spaceappchallenge2026_spacedrifters.ui.theme.SpaceDriftersTheme

/** Pantalla inicial: logo, nombre de la app y elección de rol. Sin login. */
@Composable
fun SelectorRolScreen(
    onAstronauta: () -> Unit,
    onMedico: () -> Unit,
) {
    // Sin barra superior: la cabecera con el logo hace de título.
    PantallaBase(titulo = null) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Cabecera()
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.selector_subtitulo),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TarjetaRol(
                icono = R.drawable.ic_astronauta,
                descripcionIcono = stringResource(R.string.cd_astronauta),
                titulo = stringResource(R.string.rol_astronauta),
                descripcion = stringResource(R.string.rol_astronauta_desc),
                onClick = onAstronauta,
            )
            TarjetaRol(
                icono = R.drawable.ic_medico,
                descripcionIcono = stringResource(R.string.cd_medico),
                titulo = stringResource(R.string.rol_medico),
                descripcion = stringResource(R.string.rol_medico_desc),
                onClick = onMedico,
            )
        }
    }
}

@Composable
private fun Cabecera() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Logo dentro de un "parche" circular, como los de las misiones.
        Box(
            modifier = Modifier
                .size(112.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = stringResource(R.string.cd_logo),
                modifier = Modifier.size(84.dp),
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.nombre_app),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(R.string.selector_titulo),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun TarjetaRol(
    @DrawableRes icono: Int,
    descripcionIcono: String,
    titulo: String,
    descripcion: String,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 96.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.background, CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(icono),
                    contentDescription = descripcionIcono,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(text = titulo, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_siguiente),
                contentDescription = stringResource(R.string.cd_entrar),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview
@Composable
private fun SelectorRolPreview() {
    SpaceDriftersTheme {
        SelectorRolScreen(onAstronauta = {}, onMedico = {})
    }
}
