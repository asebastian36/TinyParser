package com.tinyparser;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class MainAnalizador {

    public static void main(String[] args) {
        try {
            // 1. Leer el archivo
            String rutaArchivo = "ProgramaTiny.txt";
            String codigoFuente = Files.readString(Paths.get(rutaArchivo));

            // 2. Escanear (Análisis Léxico)
            Scanner scanner = new Scanner(codigoFuente);
            List<Token> tokens = scanner.escanearTokens();

            // Opcional: Imprimir tokens para depuración
            // for (Token token : tokens) {
            //     System.out.println(token);
            // }

            // 3. Parsear (Análisis Sintáctico)
            Parser parser = new Parser(tokens);
            parser.parse();

            // 4. Si llega aquí, es válido
            System.out.println("ACEPTA"); // [cite: 14]

        } catch (Exception e) {
            // 5. Si algo falla (Error Léxico o Sintáctico)
            System.out.println("RECHAZA"); //
            System.err.println(e.getMessage()); // Imprime el error específico
        }
    }
}