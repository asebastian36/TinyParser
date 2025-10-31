package com.tinyparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String fuente;
    private final List<Token> tokens = new ArrayList<>();
    private int inicio = 0;
    private int actual = 0;
    private int linea = 1;

    private static final Map<String, TipoToken> palabrasClave;

    static {
        palabrasClave = new HashMap<>();
        palabrasClave.put("if", TipoToken.IF);
        palabrasClave.put("then", TipoToken.THEN);
        palabrasClave.put("else", TipoToken.ELSE);
        palabrasClave.put("end", TipoToken.END);
        palabrasClave.put("repeat", TipoToken.REPEAT);
        palabrasClave.put("until", TipoToken.UNTIL);
        palabrasClave.put("read", TipoToken.READ);
        palabrasClave.put("write", TipoToken.WRITE);
    }

    public Scanner(String fuente) {
        this.fuente = fuente;
    }

    public List<Token> escanearTokens() {
        while (!esFin()) {
            inicio = actual;
            escanearToken();
        }

        tokens.add(new Token(TipoToken.EOF, "", linea));
        return tokens;
    }

    private void escanearToken() {
        char c = avanzar();
        switch (c) {
            case '(': agregarToken(TipoToken.PAREN_IZQ); break;
            case ')': agregarToken(TipoToken.PAREN_DER); break;
            case '+': agregarToken(TipoToken.MAS); break;
            case '-': agregarToken(TipoToken.MENOS); break;
            case '*': agregarToken(TipoToken.POR); break;
            case '/': agregarToken(TipoToken.ENTRE); break;
            case ';': agregarToken(TipoToken.PUNTOYCOMA); break;
            case '=': agregarToken(TipoToken.IGUAL); break;
            case '<': agregarToken(TipoToken.MENORQUE); break; // <-- De la imagen

            case ':':
                if (coincidir()) {
                    agregarToken(TipoToken.ASIGNACION); // Maneja :=
                } else {
                    lanzarError("Caracter inesperado ':'. Se esperaba ':='.");
                }
                break;

            // Ignorar separadores
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                linea++;
                break;

            default:
                if (esDigito(c)) {
                    numero();
                } else if (esLetra(c)) {
                    identificador();
                } else {
                    lanzarError("Caracter inesperado: " + c);
                }
                break;
        }
    }

    private void identificador() {
        while (esLetra(peek())) avanzar();

        String texto = fuente.substring(inicio, actual);
        TipoToken tipo = palabrasClave.get(texto);
        if (tipo == null) tipo = TipoToken.ID;
        agregarToken(tipo);
    }

    private void numero() {
        while (esDigito(peek())) avanzar();

        agregarToken(TipoToken.NUMERO);
    }

    private boolean coincidir() {
        if (esFin()) return false;
        if (fuente.charAt(actual) != '=') return false;
        actual++;
        return true;
    }

    private char peek() {
        if (esFin()) return '\0';
        return fuente.charAt(actual);
    }

    private boolean esLetra(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean esDigito(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean esFin() {
        return actual >= fuente.length();
    }

    private char avanzar() {
        return fuente.charAt(actual++);
    }

    private void agregarToken(TipoToken tipo) {
        String texto = fuente.substring(inicio, actual);
        tokens.add(new Token(tipo, texto, linea));
    }

    private void lanzarError(String mensaje) {
        throw new RuntimeException("Error Léxico [linea " + linea + "]: " + mensaje);
    }
}