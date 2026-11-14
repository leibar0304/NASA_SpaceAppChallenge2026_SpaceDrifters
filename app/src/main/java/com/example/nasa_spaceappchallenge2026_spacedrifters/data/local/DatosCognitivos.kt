// Datos de las pruebas cognitivas (Stroop y N-Back) — Room
// Una sola base de datos local compartida por los dos roles (astronauta y médico).
// El retraso con la Tierra se simula con llegaTierraEn: el médico solo ve lo que ya "ha llegado".

package com.example.nasa_spaceappchallenge2026_spacedrifters.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

// Room guarda los enum como texto (su nombre) sin necesidad de TypeConverter.
enum class TipoPrueba { STROOP, NBACK }

// Una sesión = una vez que el astronauta hace una prueba
@Entity(
    tableName = "sesion_prueba",
    indices = [Index("astronautaId"), Index("tipo")]
)
data class SesionPrueba(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val astronautaId: Long,
    val tipo: TipoPrueba,
    val inicioEn: Long,            // epoch ms
    val finEn: Long?,
    val esPractica: Boolean = false,   // la de familiarización no cuenta
    val esLineaBase: Boolean = false,  // sesiones antes del lanzamiento
    val valida: Boolean = true,        // false si se interrumpe
    val llegaTierraEn: Long            // inicioEn + retraso de la misión
)

// Cada ensayo del Stroop, uno a uno
@Entity(
    tableName = "ensayo_stroop",
    foreignKeys = [ForeignKey(entity = SesionPrueba::class, parentColumns = ["id"], childColumns = ["sesionId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("sesionId")]
)
data class EnsayoStroop(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sesionId: Long,
    val numero: Int,
    val palabra: String,        // "ROJO"
    val colorTinta: String,     // "AZUL"
    val coincidente: Boolean,
    val respuesta: String?,     // null = omisión (sin respuesta en 3 s)
    val correcto: Boolean,
    val tiempoMs: Long?
)

// Cada estímulo del N-Back, uno a uno
@Entity(
    tableName = "ensayo_nback",
    foreignKeys = [ForeignKey(entity = SesionPrueba::class, parentColumns = ["id"], childColumns = ["sesionId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("sesionId")]
)
data class EnsayoNBack(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sesionId: Long,
    val numero: Int,
    val estimulo: String,        // "TRIANGULO"
    val esCoincidencia: Boolean, // igual al de 2 posiciones atrás
    val pulsado: Boolean,
    val tiempoMs: Long?          // solo si ha pulsado
)

// Resumen calculado al terminar la sesión (lo que se pinta en gráficas y tablas)
@Entity(tableName = "resumen_sesion")
data class ResumenSesion(
    @PrimaryKey val sesionId: Long,
    val astronautaId: Long,
    val tipo: TipoPrueba,
    val fechaEn: Long,
    val llegaTierraEn: Long,
    // Stroop
    val tiempoMedioMs: Double? = null,
    val porcentajeAciertos: Double? = null,
    val efectoStroopMs: Double? = null,      // métrica principal Stroop
    val erroresNoCoincidentes: Int? = null,
    // N-Back
    val aciertosCoincidencias: Double? = null,
    val falsasAlarmas: Int? = null,
    val dPrima: Double? = null,              // métrica principal N-Back
    val tiempoAciertosMs: Double? = null,
    // Comparación con su línea base
    val estado: String                        // NORMAL | VIGILAR | ALERTA | SIN_LINEA_BASE
)

// Línea base por astronauta, prueba y métrica
@Entity(tableName = "linea_base_cognitiva", primaryKeys = ["astronautaId", "tipo", "metrica"])
data class LineaBaseCognitiva(
    val astronautaId: Long,
    val tipo: TipoPrueba,
    val metrica: String,        // "efectoStroopMs", "dPrima"...
    val media: Double,
    val desviacion: Double,     // rango habitual = media ± desviación
    val nSesiones: Int
)

@Dao
interface CognitivoDao {
    @Insert suspend fun insertarSesion(s: SesionPrueba): Long
    @Insert suspend fun insertarEnsayosStroop(e: List<EnsayoStroop>)
    @Insert suspend fun insertarEnsayosNBack(e: List<EnsayoNBack>)
    @Upsert suspend fun guardarResumen(r: ResumenSesion)
    @Upsert suspend fun guardarLineaBase(b: LineaBaseCognitiva)

    // Vista del astronauta: todo lo suyo, al instante
    @Query("SELECT * FROM resumen_sesion WHERE astronautaId = :id AND tipo = :tipo ORDER BY fechaEn DESC LIMIT :n")
    fun ultimosResumenes(id: Long, tipo: TipoPrueba, n: Int = 30): Flow<List<ResumenSesion>>

    // Vista del médico: solo lo que ya ha llegado a la Tierra
    @Query("SELECT * FROM resumen_sesion WHERE astronautaId = :id AND tipo = :tipo AND llegaTierraEn <= :ahora ORDER BY fechaEn DESC LIMIT :n")
    fun resumenesRecibidos(id: Long, tipo: TipoPrueba, ahora: Long, n: Int = 30): Flow<List<ResumenSesion>>

    // Lo que está "de camino" (para mostrar "1 sesión llegará en 12 min")
    @Query("SELECT * FROM resumen_sesion WHERE astronautaId = :id AND llegaTierraEn > :ahora ORDER BY llegaTierraEn")
    fun resumenesEnCamino(id: Long, ahora: Long): Flow<List<ResumenSesion>>

    @Query("SELECT * FROM linea_base_cognitiva WHERE astronautaId = :id")
    fun lineaBase(id: Long): Flow<List<LineaBaseCognitiva>>

    // Detalle de una sesión para el médico
    @Query("SELECT * FROM ensayo_stroop WHERE sesionId = :sesionId ORDER BY numero")
    suspend fun ensayosStroop(sesionId: Long): List<EnsayoStroop>

    @Query("SELECT * FROM ensayo_nback WHERE sesionId = :sesionId ORDER BY numero")
    suspend fun ensayosNBack(sesionId: Long): List<EnsayoNBack>

    // Prácticas completadas (para no repetir la práctica cada vez)
    @Query("SELECT COUNT(*) FROM sesion_prueba WHERE astronautaId = :id AND tipo = :tipo AND esPractica = 1 AND valida = 1")
    suspend fun contarPracticasValidas(id: Long, tipo: TipoPrueba): Int

    // Pruebas reales y válidas hechas desde un instante (para marcar "Pruebas de hoy")
    @Query("SELECT DISTINCT tipo FROM sesion_prueba WHERE astronautaId = :id AND esPractica = 0 AND valida = 1 AND inicioEn >= :desde")
    fun tiposHechosDesde(id: Long, desde: Long): Flow<List<TipoPrueba>>

    @Query("SELECT * FROM linea_base_cognitiva WHERE astronautaId = :id AND tipo = :tipo AND metrica = :metrica")
    suspend fun lineaBaseDe(id: Long, tipo: TipoPrueba, metrica: String): LineaBaseCognitiva?

    // Resúmenes de las sesiones de línea base (válidas) de una prueba, para calcularla
    @Query(
        "SELECT r.* FROM resumen_sesion r JOIN sesion_prueba s ON s.id = r.sesionId " +
            "WHERE s.astronautaId = :id AND s.tipo = :tipo AND s.esLineaBase = 1 AND s.valida = 1 AND s.esPractica = 0"
    )
    suspend fun resumenesLineaBase(id: Long, tipo: TipoPrueba): List<ResumenSesion>

    // Sesiones de misión (sin las de línea base) desde un instante, de la más antigua a la más reciente
    @Query(
        "SELECT r.* FROM resumen_sesion r JOIN sesion_prueba s ON s.id = r.sesionId " +
            "WHERE r.astronautaId = :id AND r.tipo = :tipo AND s.esLineaBase = 0 AND r.fechaEn >= :desde " +
            "ORDER BY r.fechaEn"
    )
    fun resumenesMisionDesde(id: Long, tipo: TipoPrueba, desde: Long): Flow<List<ResumenSesion>>

    // Primera sesión de misión (el día 1 de misión, mientras no haya una tabla de misión)
    @Query(
        "SELECT MIN(r.fechaEn) FROM resumen_sesion r JOIN sesion_prueba s ON s.id = r.sesionId " +
            "WHERE r.astronautaId = :id AND s.esLineaBase = 0"
    )
    fun inicioMision(id: Long): Flow<Long?>
}
