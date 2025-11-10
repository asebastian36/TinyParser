package com.tinyparser.graficador;

public class TokenGraficador {
    public final TipoTokenGraficador tipo;
    public final String lexema;
    public final double valor; // Para números

    // Constructor para símbolos e ID
    public TokenGraficador(TipoTokenGraficador tipo, String lexema) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.valor = 0;
    }

    // Constructor para números
    public TokenGraficador(TipoTokenGraficador tipo, double valor) {
        this.tipo = tipo;
        this.lexema = String.valueOf(valor);
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Token [tipo=" + tipo + ", lexema='" + lexema + "']";
    }
}