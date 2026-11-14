package com.example.nasa_spaceappchallenge2026_spacedrifters.data.repository

import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.AppDatabase
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.Astronauta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * Lectura de la tripulación. Mínimo por ahora: cuando llegue la base de datos nueva del equipo,
 * este es el sitio a adaptar (las pantallas no dependen de las tablas).
 */
class TripulacionRepository(
    private val db: AppDatabase,
    /** Espera a que termine la carga de los datos de ejemplo del primer arranque. */
    private val esperarDatos: suspend () -> Unit = {},
) {
    fun astronauta(id: Long): Flow<Astronauta?> = flow {
        esperarDatos()
        emitAll(db.tripulacionDao().astronauta(id))
    }
}
