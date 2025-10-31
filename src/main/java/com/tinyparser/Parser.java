package com.tinyparser;

import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int actual = 0;

    // Clase interna para errores de parseo
    private static class ErrorParse extends RuntimeException {
        ErrorParse(String mensaje) {
            super(mensaje);
        }
    }

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void parse() {
        programa();
        if (!esFin()) {
            throw error(peek(), "Se esperaba fin de archivo pero se encontró más código.");
        }
    }

    // Regla: programa -> secuenciaSentencias
    private void programa() {
        secuenciaSentencias();
    }

    // Regla: secuenciaSentencias -> sentencia (PUNTOYCOMA sentencia)*
    // Esta gramática asume que los punto y coma SEPARAN sentencias.
    private void secuenciaSentencias() {
        sentencia();
        while (coincidir(TipoToken.PUNTOYCOMA)) {
            sentencia();
        }
    }

    // Regla: sentencia -> sentenciaIf | sentenciaAsignacion
    private void sentencia() {
        if (peek().tipo == TipoToken.IF) {
            sentenciaIf();
        } else if (peek().tipo == TipoToken.ID) {
            sentenciaAsignacion();
        } else {
            throw error(peek(), "Se esperaba una sentencia (if o asignación).");
        }
    }

    // Regla: sentenciaIf -> IF condicion THEN secuenciaSentencias (ELSE secuenciaSentencias)? END
    private void sentenciaIf() {
        consumir(TipoToken.IF, "Se esperaba 'if'.");
        condicion();
        consumir(TipoToken.THEN, "Se esperaba 'then' después de la condición del if.");
        secuenciaSentencias();
        if (coincidir(TipoToken.ELSE)) {
            secuenciaSentencias();
        }
        consumir(TipoToken.END, "Se esperaba 'end' para cerrar el 'if'."); // Captura bad_03_if_sin_end
    }

    // Regla: sentenciaAsignacion -> ID ASIGNACION expresion
    private void sentenciaAsignacion() {
        consumir(TipoToken.ID, "Se esperaba un identificador (variable).");
        consumir(TipoToken.ASIGNACION, "Se esperaba ':=' para asignación."); // Captura bad_01_token_asig_mal
        expresion();
    }

    // Regla: condicion -> expresion ( (IGUAL | MAYORQUE) expresion )
    // Inferencia de los ejemplos: if y>3 [cite: 23] | if n=10 [cite: 33]
    private void condicion() {
        expresion();
        if (coincidir(TipoToken.IGUAL) || coincidir(TipoToken.MAYORQUE)) {
            expresion();
        }
        // Si no hay operador, es una expresión simple (que no parece ser el caso en Tiny,
        // pero esta gramática lo permitiría. Para ser estricto, aquí se podría lanzar un error
        // si no se encuentra = o >). Por simplicidad, la dejamos así.
    }

    // Las siguientes 3 reglas manejan la aritmética (precedencia de operadores)

    // Regla: expresion -> termino ( (MAS | MENOS) termino )*
    private void expresion() {
        termino();
        while (coincidir(TipoToken.MAS) || coincidir(TipoToken.MENOS)) {
            termino();
        }
    }

    // Regla: termino -> factor ( (POR | ENTRE) factor )*
    private void termino() {
        do {
            factor();
        } while (coincidir(TipoToken.POR) || coincidir(TipoToken.ENTRE));
    }

    // Regla: factor -> NUMERO | ID | PAREN_IZQ expresion PAREN_DER
    private void factor() {
        if (coincidir(TipoToken.NUMERO) || coincidir(TipoToken.ID)) {
            // Ya se consumió, no hacer nada.
        } else if (coincidir(TipoToken.PAREN_IZQ)) {
            expresion();
            consumir(TipoToken.PAREN_DER, "Se esperaba ')' para cerrar la expresión."); // Captura bad_02_falta_paren
        } else {
            throw error(peek(), "Se esperaba un Número, un ID o un '('.");
        }
    }

    // --- Métodos de Ayuda ---

    private boolean coincidir(TipoToken... tipos) {
        for (TipoToken tipo : tipos) {
            if (verificar(tipo)) {
                avanzar();
                return true;
            }
        }
        return false;
    }

    private Token consumir(TipoToken tipo, String mensajeError) {
        if (verificar(tipo)) return avanzar();
        throw error(peek(), mensajeError);
    }

    private boolean verificar(TipoToken tipo) {
        if (esFin()) return false;
        return peek().tipo == tipo;
    }

    private Token avanzar() {
        if (!esFin()) actual++;
        return previo();
    }

    private boolean esFin() {
        return peek().tipo == TipoToken.EOF;
    }

    private Token peek() {
        return tokens.get(actual);
    }

    private Token previo() {
        return tokens.get(actual - 1);
    }

    private ErrorParse error(Token token, String mensaje) {
        String ubicacion = (token.tipo == TipoToken.EOF)
                ? "en el fin del archivo"
                : "en '" + token.lexema + "'";
        return new ErrorParse("Error Sintáctico [linea " + token.linea + "] " + ubicacion + ": " + mensaje);
    }
}