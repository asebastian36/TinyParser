package com.tinyparser;

public enum TipoToken {
    IF, THEN, ELSE, END,
    REPEAT, UNTIL,
    READ, WRITE,

    ASIGNACION, // :=
    IGUAL,      // =
    MENORQUE,   // <
    MAYORQUE,   // >

    MAS,        // + (adop)
    MENOS,      // - (adop)
    POR,        // * (opmult)
    ENTRE,      // / (opmult)

    PAREN_IZQ,  // (
    PAREN_DER,  // )
    PUNTOYCOMA, // ;

    // Literales y otros
    ID,         // identificador
    NUMERO,     // número
    EOF         // Fin de Archivo
}