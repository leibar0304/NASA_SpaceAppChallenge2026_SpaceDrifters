package com.example.nasa_spaceappchallenge2026_spacedrifters.di

import android.content.Context
import android.util.Log
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.config.ConfigMision
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.ejemplo.CargadorDatosEjemplo
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.AppDatabase
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.repository.CognitivoRepository
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.repository.TripulacionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async

/**
 * Inyección de dependencias manual (sin Hilt).
 *
 * Aquí se crean, una sola vez, la base de datos, los repositorios y cualquier otro
 * objeto compartido. Los ViewModels los obtienen a través de [AppViewModelProvider].
 *
 * Ejemplo de cómo añadir algo nuevo:
 * ```
 * val tripulacionRepository by lazy { TripulacionRepository(database.astronautaDao()) }
 * ```
 */
class AppContainer(context: Context) {
    private val appContext: Context = context.applicationContext

    /**
     * Ámbito de toda la app, para trabajos que tienen que terminar aunque se cierre la pantalla
     * (p. ej., guardar una prueba interrumpida al salir de la app).
     */
    val ambitoAplicacion: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database: AppDatabase by lazy { AppDatabase.crear(appContext) }

    /** assets/config_mision.json: retraso con la Tierra, umbrales de estado... */
    val configMision: ConfigMision by lazy { ConfigMision.leer(appContext) }

    val cognitivoRepository: CognitivoRepository by lazy {
        CognitivoRepository(database, configMision.aConfigCognitiva(), esperarDatos = { datosEjemplo.await() })
    }

    val tripulacionRepository: TripulacionRepository by lazy {
        TripulacionRepository(database, esperarDatos = { datosEjemplo.await() })
    }

    /**
     * Carga de assets/datos_ejemplo.json si la base de datos está vacía. Se lanza al arrancar
     * ([iniciar]); nunca falla hacia fuera (si algo va mal, se registra y la app sigue sin datos).
     */
    val datosEjemplo: Deferred<Unit> = ambitoAplicacion.async(start = CoroutineStart.LAZY) {
        runCatching {
            val json = appContext.assets.open(CargadorDatosEjemplo.ARCHIVO).bufferedReader().use { it.readText() }
            CargadorDatosEjemplo(database, cognitivoRepository).cargarSiVacia(json)
        }.onFailure { Log.e("DatosEjemplo", "No se han podido cargar los datos de ejemplo", it) }
        Unit
    }

    /**
     * Astronauta que usa la app en el rol de astronauta (Maya Okafor en los datos de ejemplo).
     * Provisional hasta que haya un selector de tripulante.
     */
    val astronautaActualId: Long = 1L

    /** Arranca lo que tiene que estar listo cuanto antes. Lo llama la Application. */
    fun iniciar() {
        datosEjemplo.start()
    }
}
