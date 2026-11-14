package com.example.nasa_spaceappchallenge2026_spacedrifters.domain.cognitivo;

/**
 * Conversión de la métrica principal de una sesión a una puntuación en la que "más alto es mejor"
 * en todas las pruebas, para las gráficas del astronauta (que no muestran números en bruto).
 */
public final class Rendimiento {

    private Rendimiento() {
    }

    /**
     * Desviaciones respecto a la media de su línea base, con signo de forma que positivo = mejor.
     * El rango habitual (media ± 1 desviación) queda siempre entre −1 y +1.
     * Con desviación 0 (línea base plana) devuelve 0 si es igual a la media y ±4 si no.
     */
    public static double puntuacion(double valor, double media, double desviacion, EvaluadorEstado.Direccion direccion) {
        double mejora = direccion == EvaluadorEstado.Direccion.MAYOR_ES_PEOR ? media - valor : valor - media;
        if (desviacion <= 0) {
            return mejora == 0 ? 0 : Math.signum(mejora) * 4;
        }
        return mejora / desviacion;
    }

    /** Sin línea base solo se puede orientar el valor (más alto = mejor), sin escala común. */
    public static double orientado(double valor, EvaluadorEstado.Direccion direccion) {
        return direccion == EvaluadorEstado.Direccion.MAYOR_ES_PEOR ? -valor : valor;
    }
}
