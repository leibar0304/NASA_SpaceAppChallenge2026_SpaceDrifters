package com.example.nasa_spaceappchallenge2026_spacedrifters

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.local.TipoPrueba
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.repository.CognitivoRepository
import com.example.nasa_spaceappchallenge2026_spacedrifters.data.repository.SesionNueva
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.ConfigCognitiva
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.EstadoCognitivo
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.nback.FormaNBack
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.nback.ResultadoEnsayoNBack
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.stroop.ColorStroop
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.stroop.EstimuloStroop
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.stroop.ResultadoEnsayoStroop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Guardado de sesiones en Room (base de datos en memoria). */
@RunWith(AndroidJUnit4::class)
class CognitivoRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var repo: CognitivoRepository
    private val dao get() = db.cognitivoDao()

    private val astronauta = 7L
    private val minuto = 60_000L
    private val inicio = 1_000_000_000L

    @Before
    fun crear() {
        val contexto = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(contexto, AppDatabase::class.java).build()
        repo = CognitivoRepository(db, ConfigCognitiva()) // retraso de 18 min, umbrales 1 y 2
    }

    @After
    fun cerrar() = db.close()

    /** Sesión Stroop de 2 aciertos con un efecto Stroop exacto de [efectoMs]. */
    private fun stroop(efectoMs: Long) = listOf(
        ResultadoEnsayoStroop(1, EstimuloStroop(ColorStroop.VERDE, ColorStroop.VERDE), ColorStroop.VERDE, 500L),
        ResultadoEnsayoStroop(2, EstimuloStroop(ColorStroop.ROJO, ColorStroop.AZUL), ColorStroop.AZUL, 500L + efectoMs),
    )

    private fun sesion(inicioEn: Long = inicio, esPractica: Boolean = false, valida: Boolean = true, lineaBase: Boolean = false) =
        SesionNueva(astronauta, inicioEn, inicioEn + 2 * minuto, esPractica, valida, lineaBase)

    @Test
    fun sesionReal_guardaSesionEnsayosYResumenConRetraso() = runBlocking {
        val estado = repo.guardarStroop(sesion(), stroop(efectoMs = 120))
        assertEquals(EstadoCognitivo.SIN_LINEA_BASE, estado)

        val resumen = dao.ultimosResumenes(astronauta, TipoPrueba.STROOP).first().single()
        assertEquals(inicio + 18 * minuto, resumen.llegaTierraEn)
        assertEquals(120.0, resumen.efectoStroopMs!!, 1e-9)
        assertEquals(100.0, resumen.porcentajeAciertos!!, 1e-9)
        assertEquals("SIN_LINEA_BASE", resumen.estado)
        assertEquals(2, dao.ensayosStroop(resumen.sesionId).size)
    }

    @Test
    fun medico_soloVeLoQueYaHaLlegadoALaTierra() = runBlocking {
        repo.guardarStroop(sesion(), stroop(efectoMs = 100))
        val llega = inicio + 18 * minuto

        assertTrue(dao.resumenesRecibidos(astronauta, TipoPrueba.STROOP, ahora = llega - 1).first().isEmpty())
        assertEquals(1, dao.resumenesEnCamino(astronauta, ahora = llega - 1).first().size)

        assertEquals(1, dao.resumenesRecibidos(astronauta, TipoPrueba.STROOP, ahora = llega).first().size)
        assertTrue(dao.resumenesEnCamino(astronauta, ahora = llega).first().isEmpty())
    }

    @Test
    fun practica_seGuardaPeroNoTieneResumen() = runBlocking {
        assertFalse(repo.haHechoPractica(astronauta, TipoPrueba.STROOP))
        assertNull(repo.guardarStroop(sesion(esPractica = true), stroop(efectoMs = 100)))

        assertTrue(repo.haHechoPractica(astronauta, TipoPrueba.STROOP))
        assertTrue(dao.ultimosResumenes(astronauta, TipoPrueba.STROOP).first().isEmpty())
        assertTrue(repo.tiposHechosDesde(astronauta, 0).first().isEmpty())
    }

    @Test
    fun interrumpida_noCuentaNiComoPracticaNiComoHecha() = runBlocking {
        assertNull(repo.guardarStroop(sesion(valida = false), stroop(efectoMs = 100)))
        assertNull(repo.guardarStroop(sesion(esPractica = true, valida = false), stroop(efectoMs = 100)))

        assertFalse(repo.haHechoPractica(astronauta, TipoPrueba.STROOP))
        assertTrue(dao.ultimosResumenes(astronauta, TipoPrueba.STROOP).first().isEmpty())
        assertTrue(repo.tiposHechosDesde(astronauta, 0).first().isEmpty())
    }

    @Test
    fun conLineaBase_elEstadoSeCalculaFrenteAElla() = runBlocking {
        // Línea base: efecto 40, 50 y 60 ms → media 50, desviación 10.
        listOf(40L, 50L, 60L).forEachIndexed { i, efecto ->
            repo.guardarStroop(sesion(inicioEn = inicio + i * minuto, lineaBase = true), stroop(efecto))
        }
        val base = dao.lineaBaseDe(astronauta, TipoPrueba.STROOP, CognitivoRepository.METRICA_EFECTO_STROOP)!!
        assertEquals(50.0, base.media, 1e-9)
        assertEquals(10.0, base.desviacion, 1e-9)
        assertEquals(3, base.nSesiones)

        assertEquals(EstadoCognitivo.NORMAL, repo.guardarStroop(sesion(inicio + 10 * minuto), stroop(55)))
        assertEquals(EstadoCognitivo.VIGILAR, repo.guardarStroop(sesion(inicio + 11 * minuto), stroop(65)))
        assertEquals(EstadoCognitivo.ALERTA, repo.guardarStroop(sesion(inicio + 12 * minuto), stroop(80)))
        assertEquals(setOf(TipoPrueba.STROOP), repo.tiposHechosDesde(astronauta, 0).first())
    }

    @Test
    fun nback_necesitaLasDosPracticasYGuardaSusEnsayos() = runBlocking {
        val ensayos = listOf(
            ResultadoEnsayoNBack(1, FormaNBack.CRUZ, false, null),
            ResultadoEnsayoNBack(2, FormaNBack.ROMBO, false, null),
            ResultadoEnsayoNBack(3, FormaNBack.CRUZ, true, 600L),
            ResultadoEnsayoNBack(4, FormaNBack.ESTRELLA, false, null),
        )
        repo.guardarNBack(sesion(esPractica = true), ensayos, nivel = 1)
        assertFalse(repo.haHechoPractica(astronauta, TipoPrueba.NBACK))
        repo.guardarNBack(sesion(esPractica = true), ensayos, nivel = 2)
        assertTrue(repo.haHechoPractica(astronauta, TipoPrueba.NBACK))

        assertEquals(EstadoCognitivo.SIN_LINEA_BASE, repo.guardarNBack(sesion(), ensayos, nivel = 2))
        val resumen = dao.ultimosResumenes(astronauta, TipoPrueba.NBACK).first().single()
        assertEquals(100.0, resumen.aciertosCoincidencias!!, 1e-9)
        assertEquals(0, resumen.falsasAlarmas)
        assertEquals(600.0, resumen.tiempoAciertosMs!!, 1e-9)
        assertEquals(4, dao.ensayosNBack(resumen.sesionId).size)
    }
}
