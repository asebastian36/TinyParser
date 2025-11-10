package com.tinyparser.graficador.ast;

import com.tinyparser.graficador.TipoTokenGraficador;

/**
 * Nodo del AST para operaciones unarias, como la negación '-'.
 * Ejemplo: -x o -(x+1)
 */
public class NodoUnario implements Nodo {
    private TipoTokenGraficador operador;
    private Nodo operando;

    public NodoUnario(TipoTokenGraficador operador, Nodo operando) {
        this.operador = operador;
        this.operando = operando;
    }

    @Override
    public double evaluar(double x) {
        double valOperando = operando.evaluar(x);

        if (operador == TipoTokenGraficador.MENOS) {
            return -valOperando;
        }
        throw new RuntimeException("Operador unario desconocido: " + operador);
    }
}