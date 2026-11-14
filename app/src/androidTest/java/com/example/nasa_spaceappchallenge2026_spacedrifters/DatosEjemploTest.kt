package com.example.nasa_spaceappchallenge2026_spacedrifters

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.ejemplo.CargadorDatosEjemplo
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.AppDatabase
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.ResumenSesion
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.Sintoma
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.TipoPrueba
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.repository.CognitivoRepository
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.ConfigCognitiva
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.time.ZoneId

/** Los datos de ejemplo de assets/ cargan bien y producen los casos de la demo. */
@RunWith(AndroidJUnit4::class)
class DatosEjemploTest {

    private lateinit var db: AppDatabase
    private lateinit var repo: CognitivoRepository
    private val dao get() = db.cognitivoDao()

    private val zona = ZoneId.of("Europe/Madrid")
    // 14 de noviembre de 2026, 11:00 en Madrid.
    private val ahora = Instant.parse("2026-11-14T10:00:00Z").toEpochMilli()
    private val hoy = "2026-11-14"

    private val maya = 1L
    private val leo = 2L
    private val ane = 3L
    private val ilan = 4L

    @Before
    fun cargar() = runBlocking {
        val contexto = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(contexto, AppDatabase::class.java).build()
        repo = CognitivoRepository(db, ConfigCognitiva())
        val json = contexto.assets.open(CargadorDatosEjemplo.ARCHIVO).bufferedReader().use { it.readText() }
        assertTrue(CargadorDatosEjemplo(db, repo).cargarSiVacia(json, ahora, zona))
    }

    @After
    fun cerrar() = db.close()

    /** Los 14 resúmenes de misión (sin la línea base), del más antiguo al más reciente. */
    private suspend fun mision(id: Long, tipo: TipoPrueba): List<ResumenSesion> =
        dao.ultimosResumenes(id, tipo, 30).first().take(14).reversed()

    @Test
    fun cargaTripulacionRegistrosYSesiones() = runBlocking {
        assertEquals(4, db.tripulacionDao().astronautas().first().size)
        for (id in listOf(maya, leo, ane, ilan)) {
            assertEquals(14, db.tripulacionDao().registrosDesde(id, "2000-01-01").first().size)
            for (tipo in TipoPrueba.entries) {
                assertEquals("astronauta $id, $tipo", 17, dao.ultimosResumenes(id, tipo, 30).first().size)
                assertTrue(repo.haHechoPractica(id, tipo))
                val base = dao.lineaBaseDe(id, tipo, CognitivoRepository.metricaPrincipal(tipo))
                assertEquals(3, base!!.nSesiones)
            }
        }
        // Una segunda carga no duplica nada.
        assertFalse(CargadorDatosEjemplo(db, repo).cargarSiVacia("{}", ahora, zona))
    }

    @Test
    fun maya_nbackEmpeoraLosUltimosDiasYDuermePoco() = runBlocking {
        val nback = mision(maya, TipoPrueba.NBACK)
        assertTrue(nback.take(10).all { it.estado == "NORMAL" })
        nback.takeLast(3).forEach { assertTrue(it.estado, it.estado == "VIGILAR" || it.estado == "ALERTA") }
        assertTrue(mision(maya, TipoPrueba.STROOP).all { it.estado == "NORMAL" })

        val registros = db.tripulacionDao().registrosDesde(maya, "2000-01-01").first()
        assertTrue(registros.take(3).all { it.horasSueno!! < 7.5 })
    }

    @Test
    fun maya_suUltimaSesionSigueDeCamino() = runBlocking {
        val deCamino = dao.resumenesEnCamino(maya, ahora).first()
        assertEquals(1, deCamino.size)
        assertEquals(TipoPrueba.NBACK, deCamino.single().tipo)
        // El médico ve 16 de las 17 sesiones de N-Back de Maya.
        assertEquals(16, dao.resumenesRecibidos(maya, TipoPrueba.NBACK, ahora, 30).first().size)
    }

    @Test
    fun leo_stroopEnAlertaHoyConDolorDeCabeza() = runBlocking {
        val stroopHoy = mision(leo, TipoPrueba.STROOP).last()
        assertEquals("ALERTA", stroopHoy.estado)
        assertEquals(hoy, Instant.ofEpochMilli(stroopHoy.fechaEn).atZone(zona).toLocalDate().toString())
        assertTrue(stroopHoy.llegaTierraEn <= ahora)
        assertTrue(mision(leo, TipoPrueba.STROOP).dropLast(1).all { it.estado == "NORMAL" })
        assertTrue(Sintoma.DOLOR_CABEZA in db.tripulacionDao().registroDe(leo, hoy)!!.listaSintomas)
    }

    @Test
    fun aneEIlan_dentroDeSuRango() = runBlocking {
        for (id in listOf(ane, ilan)) {
            for (tipo in TipoPrueba.entries) {
                mision(id, tipo).forEach { assertEquals("astronauta $id, $tipo", "NORMAL", it.estado) }
            }
        }
    }
}
