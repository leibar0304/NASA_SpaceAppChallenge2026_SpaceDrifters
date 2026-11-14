package com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.ejemplo;

import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.DPrima;
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.nback.ResultadoEnsayoNBack;
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.nback.SecuenciaNBack;
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.stroop.EstimuloStroop;
import com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo.stroop.ResultadoEnsayoStroop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Genera ensayos simulados para los datos de ejemplo, con resultados buscados de antemano.
 * Los resúmenes se calculan después a partir de estos ensayos (con MetricasStroop/MetricasNBack),
 * así que son coherentes con ellos por construcción.
 */
public final class GeneradorEnsayosEjemplo {

    private static final long MIN_TIEMPO_MS = 300;
    private static final long MAX_TIEMPO_MS = 2_900;

    private GeneradorEnsayosEjemplo() {
    }

    /**
     * Respuestas a una secuencia Stroop. Los aciertos coincidentes tienen de media
     * [tiempoCoincidentesMs] y los no coincidentes [tiempoCoincidentesMs] + [efectoMs] (±1 ms).
     * [errores] respuestas equivocadas (se toca el color de la palabra) en ensayos no coincidentes
     * y [omisiones] ensayos sin respuesta.
     */
    public static List<ResultadoEnsayoStroop> stroop(List<EstimuloStroop> secuencia, double tiempoCoincidentesMs,
                                                     double efectoMs, int errores, int omisiones, Random random) {
        int n = secuencia.size();
        List<Integer> noCoincidentes = new ArrayList<>();
        for (int i = 0; i < n; i++) if (!secuencia.get(i).esCoincidente()) noCoincidentes.add(i);
        Collections.shuffle(noCoincidentes, random);
        Set<Integer> conError = new HashSet<>(noCoincidentes.subList(0, Math.min(errores, noCoincidentes.size())));

        List<Integer> resto = new ArrayList<>();
        for (int i = 0; i < n; i++) if (!conError.contains(i)) resto.add(i);
        Collections.shuffle(resto, random);
        Set<Integer> omitidos = new HashSet<>(resto.subList(0, Math.min(omisiones, resto.size())));

        // Tiempos brutos de los aciertos con algo de variabilidad.
        long[] tiempos = new long[n];
        for (int i = 0; i < n; i++) {
            double media = tiempoCoincidentesMs + (secuencia.get(i).esCoincidente() ? 0 : efectoMs);
            tiempos[i] = acotar(Math.round(media + random.nextGaussian() * 70));
        }
        // Se desplaza cada grupo para que su media sea exactamente la buscada.
        ajustarMedia(secuencia, tiempos, conError, omitidos, true, tiempoCoincidentesMs);
        ajustarMedia(secuencia, tiempos, conError, omitidos, false, tiempoCoincidentesMs + efectoMs);

        List<ResultadoEnsayoStroop> resultados = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            EstimuloStroop e = secuencia.get(i);
            if (omitidos.contains(i)) {
                resultados.add(new ResultadoEnsayoStroop(i + 1, e, null, null));
            } else if (conError.contains(i)) {
                resultados.add(new ResultadoEnsayoStroop(i + 1, e, e.getPalabra(), tiempos[i]));
            } else {
                resultados.add(new ResultadoEnsayoStroop(i + 1, e, e.getTinta(), tiempos[i]));
            }
        }
        return resultados;
    }

    /**
     * Respuestas a una secuencia N-Back con exactamente [aciertos] coincidencias pulsadas y
     * [falsasAlarmas] pulsaciones en no coincidencias puntuables. Tiempos alrededor de [tiempoMs].
     */
    public static List<ResultadoEnsayoNBack> nback(SecuenciaNBack secuencia, int aciertos, int falsasAlarmas,
                                                   double tiempoMs, Random random) {
        List<Integer> coincidencias = new ArrayList<>();
        List<Integer> noCoincidencias = new ArrayList<>();
        for (int i = secuencia.getNivel(); i < secuencia.size(); i++) {
            (secuencia.esCoincidencia(i) ? coincidencias : noCoincidencias).add(i);
        }
        if (aciertos > coincidencias.size() || falsasAlarmas > noCoincidencias.size()) {
            throw new IllegalArgumentException("Más pulsaciones que estímulos posibles");
        }
        Collections.shuffle(coincidencias, random);
        Collections.shuffle(noCoincidencias, random);
        Set<Integer> pulsados = new HashSet<>(coincidencias.subList(0, aciertos));
        pulsados.addAll(noCoincidencias.subList(0, falsasAlarmas));

        List<ResultadoEnsayoNBack> resultados = new ArrayList<>(secuencia.size());
        for (int i = 0; i < secuencia.size(); i++) {
            Long tiempo = pulsados.contains(i) ? acotar(Math.round(tiempoMs + random.nextGaussian() * 90)) : null;
            resultados.add(new ResultadoEnsayoNBack(i + 1, secuencia.get(i), secuencia.esCoincidencia(i), tiempo));
        }
        return resultados;
    }

    /**
     * Aciertos y falsas alarmas cuya d′ se acerca más a [dPrimaObjetivo]. En empate, menos
     * falsas alarmas. Devuelve {aciertos, falsasAlarmas}.
     */
    public static int[] aciertosYFalsasAlarmasPara(double dPrimaObjetivo, int coincidencias, int noCoincidencias) {
        int[] mejor = {coincidencias, 0};
        double menorDiferencia = Double.MAX_VALUE;
        for (int fa = 0; fa <= noCoincidencias; fa++) {
            for (int h = 0; h <= coincidencias; h++) {
                double diferencia = Math.abs(DPrima.calcular(h, coincidencias, fa, noCoincidencias) - dPrimaObjetivo);
                if (diferencia < menorDiferencia - 1e-12) {
                    menorDiferencia = diferencia;
                    mejor = new int[]{h, fa};
                }
            }
        }
        return mejor;
    }

    private static void ajustarMedia(List<EstimuloStroop> secuencia, long[] tiempos, Set<Integer> conError,
                                     Set<Integer> omitidos, boolean coincidentes, double objetivo) {
        double suma = 0;
        int cuenta = 0;
        for (int i = 0; i < tiempos.length; i++) {
            if (secuencia.get(i).esCoincidente() == coincidentes && !conError.contains(i) && !omitidos.contains(i)) {
                suma += tiempos[i];
                cuenta++;
            }
        }
        if (cuenta == 0) return;
        long desplazamiento = Math.round(objetivo - suma / cuenta);
        for (int i = 0; i < tiempos.length; i++) {
            if (secuencia.get(i).esCoincidente() == coincidentes && !conError.contains(i) && !omitidos.contains(i)) {
                tiempos[i] += desplazamiento;
            }
        }
    }

    private static long acotar(long ms) {
        return Math.max(MIN_TIEMPO_MS, Math.min(MAX_TIEMPO_MS, ms));
    }
}
