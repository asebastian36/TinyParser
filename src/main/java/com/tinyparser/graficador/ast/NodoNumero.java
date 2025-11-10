package com.tinyparser.graficador.ast;

/**
 * Nodo "hoja" del AST que representa un valor numérico literal.
 */
public class NodoNumero implements Nodo {
    private double valor;

    public NodoNumero(double valor) {
        this.valor = valor;
    }

    @Override
    public double evaluar(double x) {
        return valor; // Simplemente devuelve su valor, 'x' es irrelevante
    }
}