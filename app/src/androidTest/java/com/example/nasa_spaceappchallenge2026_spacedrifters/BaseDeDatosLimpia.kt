package com.example.nasa_spaceappchallenge2026_spacedrifters

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.rules.ExternalResource

/**
 * Vacía la base de datos de la app antes de cada test (también los datos de ejemplo), para que
 * no dependan unos de otros. Tiene que ir antes que la regla que abre la Activity:
 * `@get:Rule(order = 0)`.
 */
class BaseDeDatosLimpia : ExternalResource() {
    override fun before() {
        val container = ApplicationProvider.getApplicationContext<SpaceDriftersApp>().container
        runBlocking {
            // Primero se deja terminar la carga de los datos de ejemplo del arranque.
            container.datosEjemplo.await()
            withContext(Dispatchers.IO) { container.database.clearAllTables() }
        }
    }
}
