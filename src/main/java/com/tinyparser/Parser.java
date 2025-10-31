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
        secuenciaSentencias();
        if (!esFin()) {
            throw error(peek(), "Se esperaba fin de archivo pero se encontró más código.");
        }
    }

    private void secuenciaSentencias() {
        // Ejecuta la primera sentencia (obligatoria)
        sentencia();

        // Mientras haya un ';' separador, consume el ';' y espera OTRA sentencia
        while (coincidir(TipoToken.PUNTOYCOMA)) {
            sentencia();
        }
    }

    private void sentencia() {
        TipoToken tipo = peek().tipo;

        if (tipo == TipoToken.IF) {
            sentenciaIf();
        } else if (tipo == TipoToken.REPEAT) {
            sentenciaRepeat();
        } else if (tipo == TipoToken.ID) {
            sentenciaAsignacion();
        } else if (tipo == TipoToken.READ) {
            sentenciaRead();
        } else if (tipo == TipoToken.WRITE) {
            sentenciaWrite();
        } else {
            throw error(peek(), "Se esperaba el inicio de una sentencia (if, repeat, id, read, write).");
        }
    }

    private void sentenciaIf() {
        consumir(TipoToken.IF, "Se esperaba 'if'.");
        expresion();
        consumir(TipoToken.THEN, "Se esperaba 'then' después de la condición del if.");
        secuenciaSentencias();
        if (coincidir(TipoToken.ELSE)) {
            secuenciaSentencias();
        }
        consumir(TipoToken.END, "Se esperaba 'end' para cerrar el 'if'.");
    }

    private void sentenciaRepeat() {
        consumir(TipoToken.REPEAT, "Se esperaba 'repeat'.");
        secuenciaSentencias();
        consumir(TipoToken.UNTIL, "Se esperaba 'until' después del cuerpo del 'repeat'.");
        expresion();
    }

    private void sentenciaRead() {
        consumir(TipoToken.READ, "Se esperaba 'read'.");
        consumir(TipoToken.ID, "Se esperaba un identificador (variable) después de 'read'.");
    }

    // Regla: sent-write -> WRITE exp
    private void sentenciaWrite() {
        consumir(TipoToken.WRITE, "Se esperaba 'write'.");
        expresion();
    }

    // --- FIN DE NUEVAS REGLAS ---

    private void sentenciaAsignacion() {
        consumir(TipoToken.ID, "Se esperaba un identificador (variable).");
        consumir(TipoToken.ASIGNACION, "Se esperaba ':=' para asignación.");
        expresion();
    }

    private void expresion() {
        expSimple();
        if (coincidir(TipoToken.MENORQUE) || coincidir(TipoToken.IGUAL)) {
            expSimple();
        }
    }

    private void expSimple() {
        // 1. Parsea la parte no recursiva (beta)
        term();

        // 2. Implementa el (opsuma term)* con un bucle
        while (coincidir(TipoToken.MAS) || coincidir(TipoToken.MENOS)) {
            // (alpha)
            term();
        }
    }

    private void term() {
        do {
            factor();
        } while (coincidir(TipoToken.POR) || coincidir(TipoToken.ENTRE));
    }

    private void factor() {
        if (coincidir(TipoToken.NUMERO) || coincidir(TipoToken.ID)) {
            // Ya se consumió, no hacer nada.
        } else if (coincidir(TipoToken.PAREN_IZQ)) {
            // La gramática dice (exp), así que llamamos a expresion()
            expresion();
            consumir(TipoToken.PAREN_DER, "Se esperaba ')' para cerrar la expresión.");
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

    private void consumir(TipoToken tipo, String mensajeError) {
        if (verificar(tipo)) {
            avanzar();
            return;
        }
        throw error(peek(), mensajeError);
    }

    private boolean verificar(TipoToken tipo) {
        if (esFin()) return false;
        return peek().tipo == tipo;
    }

    private void avanzar() {
        if (!esFin()) actual++;
    }

    private boolean esFin() {
        return peek().tipo == TipoToken.EOF;
    }

    private Token peek() {
        return tokens.get(actual);
    }

    private ErrorParse error(Token token, String mensaje) {
        String ubicacion = (token.tipo == TipoToken.EOF)
                ? "en el fin del archivo"
                : "en '" + token.lexema + "'";
        return new ErrorParse("Error Sintáctico [linea " + token.linea + "] " + ubicacion + ": " + mensaje);
    }
}