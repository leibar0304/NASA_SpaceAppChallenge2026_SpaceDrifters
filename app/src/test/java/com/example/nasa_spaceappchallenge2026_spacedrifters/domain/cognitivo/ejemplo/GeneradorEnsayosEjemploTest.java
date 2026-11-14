package com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.ejemplo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.DPrima;
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.nback.ConfigNBack;
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.nback.GeneradorNBack;
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.nback.MetricasNBack;
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.nback.ResultadoEnsayoNBack;
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.nback.SecuenciaNBack;
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.stroop.ConfigStroop;
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.stroop.GeneradorStroop;
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.stroop.MetricasStroop;
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.stroop.ResultadoEnsayoStroop;

import org.junit.Test;

import java.util.List;
import java.util.Random;

public class GeneradorEnsayosEjemploTest {

    @Test
    public void stroop_efectoYTiempoComoSePiden() {
        for (int semilla = 0; semilla < 100; semilla++) {
            Random r = new Random(semilla);
            List<ResultadoEnsayoStroop> rs = GeneradorEnsayosEjemplo.stroop(
                    GeneradorStroop.sesion(new ConfigStroop(), r), 600, 85, 2, 1, r);
            MetricasStroop m = MetricasStroop.calcular(rs);

            assertEquals(40, rs.size());
            assertEquals("semilla " + semilla, 85.0, m.getEfectoStroopMs(), 1.0);
            assertEquals(2, m.getErroresNoCoincidentes());
            assertEquals(1, rs.stream().filter(ResultadoEnsayoStroop::esOmision).count());
            assertEquals(100.0 * 37 / 40, m.getPorcentajeAciertos(), 1e-9);
        }
    }

    @Test
    public void nback_aciertosYFalsasAlarmasExactos() {
        Random r = new Random(5);
        SecuenciaNBack s = GeneradorNBack.sesion(new ConfigNBack(), r);
        List<ResultadoEnsayoNBack> rs = GeneradorEnsayosEjemplo.nback(s, 10, 2, 650, r);
        MetricasNBack m = MetricasNBack.calcular(rs, 2);

        assertEquals(42, rs.size());
        assertEquals(100.0 * 10 / 12, m.getAciertosCoincidencias(), 1e-9);
        assertEquals(2, m.getFalsasAlarmas());
        assertEquals(DPrima.calcular(10, 12, 2, 28), m.getDPrima(), 1e-9);
        // Los iniciales nunca se pulsan.
        assertTrue(!rs.get(0).esPulsado() && !rs.get(1).esPulsado());
    }

    @Test
    public void dPrimaObjetivo_elijeLaCombinacionMasCercana() {
        for (double objetivo = 0.5; objetivo <= 3.5; objetivo += 0.1) {
            int[] hf = GeneradorEnsayosEjemplo.aciertosYFalsasAlarmasPara(objetivo, 12, 28);
            double conseguida = DPrima.calcular(hf[0], 12, hf[1], 28);
            assertTrue("objetivo " + objetivo + " → " + conseguida, Math.abs(conseguida - objetivo) < 0.15);
        }
    }
}
