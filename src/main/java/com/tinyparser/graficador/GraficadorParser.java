package com.tinyparser.graficador;

import com.tinyparser.graficador.ast.*;

import java.util.List;

/**
 * Analizador Sintáctico (Parser) para las expresiones matemáticas.
 * Construye un Árbol de Sintaxis Abstracta (AST) para manejar
 * la precedencia de operadores.
 *
 * Implementa la siguiente gramática (para precedencia):
 * expresion -> termino ( (MAS | MENOS) termino )*
 * termino    -> factor ( (POR | ENTRE) factor )*
 * potencia   -> primario ( POTENCIA potencia )* (Asociatividad derecha)
 * factor     -> (MAS | MENOS) factor | primario
 * primario   -> NUMERO | ID | PAREN_IZQ expresion PAREN_DER
 */
public class GraficadorParser {
    private List<TokenGraficador> tokens;
    private int pos = 0;

    public Nodo parsear(List<TokenGraficador> tokens) {
        this.tokens = tokens;
        this.pos = 0;
        Nodo resultado = expresion();

        // Si no consumimos todos los tokens, algo está mal
        if (!esFin()) {
            throw new ExcepcionParseoGraficador("Token inesperado: '" + peek().lexema + "'");
        }
        return resultado;
    }

    // expresion -> termino ( (MAS | MENOS) termino )*
    private Nodo expresion() {
        Nodo nodo = termino();
        while (coincidir(TipoTokenGraficador.MAS, TipoTokenGraficador.MENOS)) {
            TipoTokenGraficador operador = previo().tipo;
            Nodo derecha = termino();
            nodo = new NodoBinario(nodo, operador, derecha);
        }
        return nodo;
    }

    // termino -> potencia ( (POR | ENTRE) potencia )*
    private Nodo termino() {
        Nodo nodo = potencia();
        while (coincidir(TipoTokenGraficador.POR, TipoTokenGraficador.ENTRE)) {
            TipoTokenGraficador operador = previo().tipo;
            Nodo derecha = potencia();
            nodo = new NodoBinario(nodo, operador, derecha);
        }
        return nodo;
    }

    // potencia -> factor ( POTENCIA potencia )*
    // La potencia es asociativa por la derecha, por eso la recursión es en el lado derecho.
    private Nodo potencia() {
        Nodo nodo = factor();
        if (coincidir(TipoTokenGraficador.POTENCIA)) {
            TipoTokenGraficador operador = previo().tipo;
            Nodo derecha = potencia(); // Recursión a la derecha
            nodo = new NodoBinario(nodo, operador, derecha);
        }
        return nodo;
    }

    // factor -> (MAS | MENOS) factor | primario
    private Nodo factor() {
        if (coincidir(TipoTokenGraficador.MENOS)) {
            TipoTokenGraficador operador = previo().tipo;
            Nodo operando = factor(); // Recursión para '-x' o '-(x+1)'
            return new NodoUnario(operador, operando);
        }
        if (coincidir(TipoTokenGraficador.MAS)) {
            // Un '+' unario no hace nada, solo consume el token
            return factor();
        }
        return primario();
    }

    // primario -> NUMERO | ID | PAREN_IZQ expresion PAREN_DER
    private Nodo primario() {
        if (coincidir(TipoTokenGraficador.NUMERO)) {
            return new NodoNumero(previo().valor);
        }
        if (coincidir(TipoTokenGraficador.ID)) {
            return new NodoVariable();
        }
        if (coincidir(TipoTokenGraficador.PAREN_IZQ)) {
            Nodo nodo = expresion(); // Parsea la expresión dentro del paréntesis
            consumir(TipoTokenGraficador.PAREN_DER, "Se esperaba ')' después de la expresión.");
            return nodo;
        }

        // Error
        if (esFin()) {
            throw new ExcepcionParseoGraficador("Se esperaba un número, 'x', o '('.");
        } else {
            throw new ExcepcionParseoGraficador("Se esperaba un número, 'x', o '(', pero se encontró '" + peek().lexema + "'.");
        }
    }

    // --- Métodos de Ayuda ---

    private boolean coincidir(TipoTokenGraficador... tipos) {
        for (TipoTokenGraficador tipo : tipos) {
            if (verificar(tipo)) {
                avanzar();
                return true;
            }
        }
        return false;
    }

    private TokenGraficador consumir(TipoTokenGraficador tipo, String mensajeError) {
        if (verificar(tipo)) return avanzar();
        throw new ExcepcionParseoGraficador(mensajeError);
    }

    private boolean verificar(TipoTokenGraficador tipo) {
        if (esFin()) return false;
        return peek().tipo == tipo;
    }

    private TokenGraficador avanzar() {
        if (!esFin()) pos++;
        return previo();
    }

    private boolean esFin() {
        return peek().tipo == TipoTokenGraficador.EOF;
    }

    private TokenGraficador peek() {
        return tokens.get(pos);
    }

    private TokenGraficador previo() {
        return tokens.get(pos - 1);
    }
}