package com.example.nasa_spaceappchallenge2026_spacedrifters

import android.app.Application
import com.example.nasa_spaceappchallenge2026_spacedrifters.di.AppContainer

/** Punto de entrada de la app. Crea el contenedor de dependencias una sola vez. */
class SpaceDriftersApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.iniciar() // carga los datos de ejemplo en segundo plano si la base está vacía
    }
}
