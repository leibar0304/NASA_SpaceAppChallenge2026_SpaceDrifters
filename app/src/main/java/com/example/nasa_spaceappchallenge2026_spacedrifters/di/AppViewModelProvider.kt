package com.example.nasa_spaceappchallenge2026_spacedrifters.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nasa_spaceappchallenge2026_spacedrifters.SpaceDriftersApp
import com.example.nasa_spaceappchallenge2026_spacedrifters.ui.screens.astronauta.InicioAstronautaViewModel
import com.example.nasa_spaceappchallenge2026_spacedrifters.ui.screens.astronauta.nback.NBackViewModel
import com.example.nasa_spaceappchallenge2026_spacedrifters.ui.screens.astronauta.stroop.StroopViewModel

/**
 * Fábrica única de ViewModels. Cada ViewModel nuevo se registra con un `initializer`:
 * ```
 * initializer { TripulacionViewModel(app().container.tripulacionRepository) }
 * ```
 * y en la pantalla: `viewModel<TripulacionViewModel>(factory = AppViewModelProvider.Factory)`.
 * Si el ViewModel necesita los argumentos de la ruta: `createSavedStateHandle().toRoute<MiRuta>()`.
 */
object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val c = app().container
            InicioAstronautaViewModel(
                cognitivo = c.cognitivoRepository,
                tripulacion = c.tripulacionRepository,
                astronautaId = c.astronautaActualId,
                retrasoMin = c.configMision.retrasoMisionMin,
            )
        }
        initializer {
            val c = app().container
            StroopViewModel(c.cognitivoRepository, c.astronautaActualId, c.ambitoAplicacion)
        }
        initializer {
            val c = app().container
            NBackViewModel(c.cognitivoRepository, c.astronautaActualId, c.ambitoAplicacion)
        }
    }
}

/** Acceso a la Application (y a su [AppContainer]) desde un `initializer`. */
fun CreationExtras.app(): SpaceDriftersApp =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SpaceDriftersApp
