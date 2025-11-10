package com.tinyparser.graficador.ast;

/**
 * Interfaz para todos los nodos del Árbol de Sintaxis Abstracta (AST).
 * El método clave es 'evaluar', que calcula el valor del subárbol.
 */
public interface Nodo {
    /**
     * Evalúa este nodo (y sus hijos) dado un valor para 'x'.
     * @param x El valor de la variable 'x'.
     * @return El resultado numérico de la evaluación.
     */
    double evaluar(double x);
}