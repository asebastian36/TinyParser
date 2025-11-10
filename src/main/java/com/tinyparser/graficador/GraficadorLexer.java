package com.tinyparser.graficador;

import java.util.ArrayList;
import java.util.List;

/**
 * Analizador Léxico (Lexer) para las expresiones matemáticas.
 */
public class GraficadorLexer {

    private String texto;
    private int pos;

    public List<TokenGraficador> escanear(String texto) {
        this.texto = texto;
        this.pos = 0;
        List<TokenGraficador> tokens = new ArrayList<>();

        while (pos < texto.length()) {
            char actual = texto.charAt(pos);

            switch (actual) {
                case ' ': // Ignorar espacios
                case '\t':
                    pos++;
                    break;
                case '+':
                    tokens.add(new TokenGraficador(TipoTokenGraficador.MAS, "+"));
                    pos++;
                    break;
                case '-':
                    tokens.add(new TokenGraficador(TipoTokenGraficador.MENOS, "-"));
                    pos++;
                    break;
                case '*':
                    tokens.add(new TokenGraficador(TipoTokenGraficador.POR, "*"));
                    pos++;
                    break;
                case '/':
                    tokens.add(new TokenGraficador(TipoTokenGraficador.ENTRE, "/"));
                    pos++;
                    break;
                case '^':
                    tokens.add(new TokenGraficador(TipoTokenGraficador.POTENCIA, "^"));
                    pos++;
                    break;
                case '(':
                    tokens.add(new TokenGraficador(TipoTokenGraficador.PAREN_IZQ, "("));
                    pos++;
                    break;
                case ')':
                    tokens.add(new TokenGraficador(TipoTokenGraficador.PAREN_DER, ")"));
                    pos++;
                    break;
                case 'x': // Asumimos que la única variable es 'x'
                    tokens.add(new TokenGraficador(TipoTokenGraficador.ID, "x"));
                    pos++;
                    break;
                default:
                    if (Character.isDigit(actual) || actual == '.') {
                        tokens.add(scanNumero());
                    } else {
                        throw new ExcepcionParseoGraficador("Caracter inesperado: '" + actual + "'");
                    }
                    break;
            }
        }
        tokens.add(new TokenGraficador(TipoTokenGraficador.EOF, ""));
        return tokens;
    }

    private TokenGraficador scanNumero() {
        int inicio = pos;
        while (pos < texto.length() && (Character.isDigit(texto.charAt(pos)) || texto.charAt(pos) == '.')) {
            pos++;
        }
        String numeroStr = texto.substring(inicio, pos);
        try {
            return new TokenGraficador(TipoTokenGraficador.NUMERO, Double.parseDouble(numeroStr));
        } catch (NumberFormatException e) {
            throw new ExcepcionParseoGraficador("Número mal formado: '" + numeroStr + "'");
        }
    }
}