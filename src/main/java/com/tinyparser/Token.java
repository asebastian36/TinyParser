package com.tinyparser;

public class Token {
    public final TipoToken tipo;
    public final String lexema;
    public final int linea;

    public Token(TipoToken tipo, String lexema, int linea) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.linea = linea;
    }

    @Override
    public String toString() {
        return "Token [tipo=" + tipo + ", lexema='" + lexema + "'] en linea " + linea;
    }
}