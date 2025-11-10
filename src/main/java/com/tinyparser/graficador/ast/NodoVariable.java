package com.tinyparser.graficador.ast;

/**
 * Nodo "hoja" del AST que representa la variable 'x'.
 */
public class NodoVariable implements Nodo {
    @Override
    public double evaluar(double x) {
        return x; // Devuelve el valor de 'x' que se le pasó
    }
}