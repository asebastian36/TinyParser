package com.tinyparser.graficador;

public enum TipoTokenGraficador {
    NUMERO,       // 123.45
    ID,           // x
    MAS,          // +
    MENOS,        // -
    POR,          // *
    ENTRE,        // /
    POTENCIA,     // ^
    PAREN_IZQ,    // (
    PAREN_DER,    // )
    EOF           // Fin de la expresión
}