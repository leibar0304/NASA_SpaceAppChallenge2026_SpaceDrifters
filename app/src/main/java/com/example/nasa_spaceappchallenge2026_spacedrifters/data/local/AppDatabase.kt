package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos local única de la app (astronauta y médico comparten los mismos datos).
 *
 * Prototipo: no hay migraciones. Si cambias una entidad, sube `version` y Room borra y
 * recrea la base de datos al arrancar (los datos de ejemplo se vuelven a cargar).
 */
@Database(
    entities = [
        SesionPrueba::class,
        EnsayoStroop::class,
        EnsayoNBack::class,
        ResumenSesion::class,
        LineaBaseCognitiva::class,
        Astronauta::class,
        RegistroDiario::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cognitivoDao(): CognitivoDao
    abstract fun tripulacionDao(): TripulacionDao

    companion object {
        fun crear(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "spacedrifters.db")
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}
