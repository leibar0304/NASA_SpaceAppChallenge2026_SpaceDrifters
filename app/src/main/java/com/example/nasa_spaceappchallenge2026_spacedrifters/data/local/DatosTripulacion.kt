// Tripulación y registro diario — Room
// Mínimo necesario para el módulo cognitivo (perfil del médico y reglas). Ampliable por el equipo.

package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Cargo a bordo. El texto visible sale de strings.xml. */
enum class CargoTripulante { COMANDANTE, INGENIERIA_VUELO, OFICIAL_MEDICO, ESPECIALISTA_MISION }

@Entity(tableName = "astronauta")
data class Astronauta(
    @PrimaryKey val id: Long,
    val nombre: String,
    val cargo: CargoTripulante,
    /** Horas de sueño habituales (su base para las reglas de sueño). */
    val horasSuenoHabituales: Double,
)

/** Síntomas del registro diario. Se guardan por nombre, separados por comas. */
enum class Sintoma { DOLOR_CABEZA, DIFICULTAD_CONCENTRACION, FATIGA, NAUSEAS }

/** Lo que el astronauta anota cada día. Los null son "Sin datos", nunca "bien". */
@Entity(
    tableName = "registro_diario",
    primaryKeys = ["astronautaId", "fecha"],
    indices = [Index("astronautaId")],
)
data class RegistroDiario(
    val astronautaId: Long,
    /** Día en formato ISO, "2026-11-14". */
    val fecha: String,
    /** Horas dormidas la noche anterior. */
    val horasSueno: Double?,
    /** Escala de somnolencia de Karolinska (KSS): 1 = muy despierto … 9 = muy somnoliento. */
    val somnolencia: Int?,
    /** Nombres de [Sintoma] separados por comas; vacío si ninguno. */
    val sintomas: String = "",
) {
    val listaSintomas: List<Sintoma>
        get() = sintomas.split(',').filter { it.isNotBlank() }.mapNotNull { nombre ->
            Sintoma.entries.firstOrNull { it.name == nombre.trim() }
        }

    companion object {
        fun unirSintomas(sintomas: Collection<Sintoma>) = sintomas.joinToString(",") { it.name }
    }
}

@Dao
interface TripulacionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarAstronautas(a: List<Astronauta>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRegistros(r: List<RegistroDiario>)

    @Query("SELECT COUNT(*) FROM astronauta")
    suspend fun contarAstronautas(): Int

    @Query("SELECT * FROM astronauta ORDER BY id")
    fun astronautas(): Flow<List<Astronauta>>

    @Query("SELECT * FROM astronauta WHERE id = :id")
    fun astronauta(id: Long): Flow<Astronauta?>

    // Registro diario desde una fecha ISO (incluida), del más reciente al más antiguo
    @Query("SELECT * FROM registro_diario WHERE astronautaId = :id AND fecha >= :desde ORDER BY fecha DESC")
    fun registrosDesde(id: Long, desde: String): Flow<List<RegistroDiario>>

    @Query("SELECT * FROM registro_diario WHERE astronautaId = :id AND fecha = :fecha")
    suspend fun registroDe(id: Long, fecha: String): RegistroDiario?
}
