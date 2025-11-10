package com.tinyparser.graficador;

/**
 * Excepción personalizada para errores de sintaxis en el graficador.
 */
public class ExcepcionParseoGraficador extends RuntimeException {
    public ExcepcionParseoGraficador(String mensaje) {
        super(mensaje);
    }
}