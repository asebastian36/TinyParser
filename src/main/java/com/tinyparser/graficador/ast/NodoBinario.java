package com.tinyparser.graficador.ast;

import com.tinyparser.graficador.TipoTokenGraficador;

/**
 * Nodo del AST para operaciones binarias (con dos operandos),
 * como '+', '-', '*', '/', '^'.
 */
public class NodoBinario implements Nodo {
    private Nodo izquierda;
    private Nodo derecha;
    private TipoTokenGraficador operador;

    public NodoBinario(Nodo izquierda, TipoTokenGraficador operador, Nodo derecha) {
        this.izquierda = izquierda;
        this.operador = operador;
        this.derecha = derecha;
    }

    @Override
    public double evaluar(double x) {
        double valIzq = izquierda.evaluar(x);
        double valDer = derecha.evaluar(x);

        switch (operador) {
            case MAS:
                return valIzq + valDer;
            case MENOS:
                return valIzq - valDer;
            case POR:
                return valIzq * valDer;
            case ENTRE:
                if (valDer == 0) {
                    throw new RuntimeException("División por cero.");
                }
                return valIzq / valDer;
            case POTENCIA:
                return Math.pow(valIzq, valDer);
            default:
                throw new RuntimeException("Operador binario desconocido: " + operador);
        }
    }
}