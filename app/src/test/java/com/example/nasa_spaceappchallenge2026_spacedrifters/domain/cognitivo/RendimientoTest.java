package com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo;

import static com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.EvaluadorEstado.Direccion.MAYOR_ES_PEOR;
import static com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.EvaluadorEstado.Direccion.MENOR_ES_PEOR;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RendimientoTest {

    private static final double DELTA = 1e-9;

    @Test
    public void stroop_masEfectoEsPeor_puntuacionNegativa() {
        // Efecto Stroop: media 50 ± 10 ms.
        assertEquals(-2.0, Rendimiento.puntuacion(70, 50, 10, MAYOR_ES_PEOR), DELTA);
        assertEquals(1.0, Rendimiento.puntuacion(40, 50, 10, MAYOR_ES_PEOR), DELTA);
        assertEquals(0.0, Rendimiento.puntuacion(50, 50, 10, MAYOR_ES_PEOR), DELTA);
    }

    @Test
    public void dPrima_menosEsPeor_puntuacionNegativa() {
        // d′: media 2,0 ± 0,5.
        assertEquals(-2.0, Rendimiento.puntuacion(1.0, 2.0, 0.5, MENOR_ES_PEOR), DELTA);
        assertEquals(1.0, Rendimiento.puntuacion(2.5, 2.0, 0.5, MENOR_ES_PEOR), DELTA);
    }

    @Test
    public void desviacionCero() {
        assertEquals(0.0, Rendimiento.puntuacion(50, 50, 0, MAYOR_ES_PEOR), DELTA);
        assertEquals(-4.0, Rendimiento.puntuacion(51, 50, 0, MAYOR_ES_PEOR), DELTA);
        assertEquals(4.0, Rendimiento.puntuacion(2.1, 2.0, 0, MENOR_ES_PEOR), DELTA);
    }

    @Test
    public void orientado_sinLineaBase() {
        assertEquals(-80.0, Rendimiento.orientado(80, MAYOR_ES_PEOR), DELTA);
        assertEquals(2.5, Rendimiento.orientado(2.5, MENOR_ES_PEOR), DELTA);
    }
}
