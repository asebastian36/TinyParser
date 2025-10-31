package com.tinyparser;

public enum TipoToken {
    // Palabras clave
    IF, THEN, ELSE, END,

    // Símbolos
    ASIGNACION, // :=
    IGUAL,      // =
    MAYORQUE,   // >
    MAS,        // +
    MENOS,      // -
    POR,        // *
    ENTRE,      // /
    PAREN_IZQ,  // (
    PAREN_DER,  // )
    PUNTOYCOMA, // ;

    // Literales y otros
    ID,         // identificador (letras)
    NUMERO,     // número (dígitos)
    EOF         // Fin de Archivo (End of File)
}