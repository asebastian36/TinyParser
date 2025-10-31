# Proyecto: Analizador Sintáctico para el Lenguaje Tiny

## Descripción

Este proyecto es una implementación de un **analizador sintáctico descendente recursivo** para el lenguaje de programación "Tiny". El programa está diseñado para leer un archivo de texto llamado `ProgramaTiny.txt` y procesar su contenido para determinar si se adhiere a la gramática especificada del lenguaje.

El analizador se compone de dos fases principales:
1.  **Análisis Léxico (Scanner):** Convierte el código fuente en una secuencia de *tokens*.
2.  **Análisis Sintáctico (Parser):** Verifica que la secuencia de tokens siga las reglas gramaticales del lenguaje.

Al finalizar, el programa imprime "ACEPTA" si el código es válido, o "RECHAZA" seguido de un mensaje de error específico si se encuentra una anomalía léxica o sintáctica.

## Características

* **Análisis Léxico Completo:** El `Scanner` reconoce el léxico del lenguaje, incluyendo:
    * **Palabras Clave:** `if`, `then`, `else`, `end`, `repeat`, `until`, `read`, `write`.
    * **Símbolos:** `:=`, `=`, `<`, `+`, `-`, `*`, `/`, `(`, `)`, `;`.
    * **Literales:** Identificadores (letras) y números (dígitos).
    * **Manejo de Separadores:** Ignora espacios, tabuladores y saltos de línea.
* **Parser Descendente Recursivo:** La estructura del `Parser` mapea directamente las reglas de la gramática BNF a métodos en Java.
* **Gramática Completa:** Implementa todas las sentencias definidas en la gramática formal (`sent-if`, `sent-repeat`, `sent-assign`, `sent-read`, `sent-write`).
* **Precedencia de Operadores:** Maneja correctamente expresiones aritméticas con múltiples niveles de precedencia (`*` y `/` antes que `+` y `-`) y el uso de paréntesis.
* **Reporte de Errores:** Si el código no es válido, el programa reporta un mensaje claro indicando la naturaleza y ubicación del error.

## Estructura del Proyecto

El proyecto está organizado en las siguientes clases Java:

* `MainAnalizador.java`: Punto de entrada del programa. Se encarga de leer el archivo `ProgramaTiny.txt` y orquestar el proceso de análisis.
* `Parser.java`: Contiene la lógica del análisis sintáctico. Consume los tokens generados por el Scanner y valida la estructura del programa.
* `Scanner.java`: Implementa el analizador léxico. Lee el código fuente como una cadena de texto y la descompone en una lista de `Token`.
* `Token.java`: Una clase de datos para representar una unidad léxica (token), conteniendo su tipo, el texto original (lexema) y la línea donde se encontró.
* `TipoToken.java`: Una enumeración (`enum`) que define todos los posibles tipos de tokens en el lenguaje Tiny.

---

## Notas Técnicas de Implementación

Esta sección detalla dos conceptos clave en la construcción del analizador.

### Gramática BNF (Backus-Naur Form)

La gramática BNF proporcionada (en la imagen) es la especificación formal de la sintaxis del lenguaje Tiny. Actúa como el "plano" para construir el `Parser`.

En un analizador descendente recursivo, existe un mapeo directo entre la gramática y el código:

* **Símbolos No Terminales:** Cada regla de la gramática (como `programa`, `sentencia`, `exp-simple`, `factor`) se convierte en un método en la clase `Parser` (ej. `private void sentencia()`, `private void expSimple()`, etc.).
* **Símbolos Terminales:** Los tokens (como `IF`, `ID`, `NUMERO`, `PAREN_IZQ`) son consumidos por los métodos de ayuda `consumir()` y `coincidir()`.
* **Secuenciación y Elección:**
    * La **secuenciación** (ej. `sent-read \to READ identificador`) se implementa llamando a `consumir(TipoToken.READ)` seguido de `consumir(TipoToken.ID)`.
    * La **elección** (ej. `sentencia \to sent-if | sent-repeat | ...`) se implementa usando una estructura `if-else if` basada en el token actual (`peek()`).

### Manejo de la Recursión por la Izquierda

Un problema común en las gramáticas para analizadores descendentes es la **recursión por la izquierda**.

* **El Problema:** Ocurre cuando una regla se define a sí misma como su primer símbolo. En la gramática de Tiny, esto ocurre en las reglas de expresiones:
    * `exp-simple \to exp-simple opsuma term | term`
    * `term \to term opmult factor | factor`
* **Por qué es un Problema:** Si se implementara directamente, el método `expSimple()` se llamaría a sí mismo *inmediatamente*, creando un bucle infinito y un desbordamiento de pila (`StackOverflowError`).
* **La Solución:** El problema se soluciona reescribiendo la gramática para eliminar esta recursión.
    * La regla: `A \to A \alpha | \beta`
    * Se transforma en: `A \to \beta (\alpha)*`

**Implementación en este proyecto:**

1.  **Gramática Original (Recursiva):**
    `exp-simple \to exp-simple opsuma term | term`

2.  **Gramática Modificada (Equivalente):**
    `exp-simple \to term (opsuma term)*`
    (Un `term` seguido de "cero o más" (`*`) secuencias de `opsuma term`).

3.  **Implementación en el Código:**
    Esta transformación se implementa en el método `expSimple()` (y de forma análoga en `term()`) usando un bucle:

    ```java
    private void expSimple() {
        // 1. Parsea la parte no recursiva (beta)
        term(); 
        
        // 2. Implementa el (opsuma term)* con un bucle
        while (coincidir(TipoToken.MAS) || coincidir(TipoToken.MENOS)) {
            // (alpha)
            term(); 
        }
    }
    ```
    Este enfoque consume un `term` inicial y luego, mientras siga encontrando operadores de suma/resta, consume el operador y el siguiente `term`, logrando la misma precedencia y asociatividad sin recursión infinita.

---

## Cómo Compilar y Ejecutar

### Prerrequisitos
* Java Development Kit (JDK) 11 o superior.
* Un IDE como IntelliJ IDEA.

### Pasos
1.  **Configurar el Proyecto:**
    * Crea un nuevo proyecto Java en tu IDE.
    * Crea un paquete (ej. `com.tinyparser`).
    * Añade los 5 archivos `.java` (`MainAnalizador.java`, `Parser.java`, `Scanner.java`, `Token.java`, `TipoToken.java`) a tu paquete.

2.  **Crear el Archivo de Entrada:**
    * En la **carpeta raíz** de tu proyecto (al mismo nivel que la carpeta `src`), crea un archivo llamado `ProgramaTiny.txt`.
    * Escribe o pega el código Tiny que deseas analizar en este archivo.

3.  **Ejecutar:**
    * Abre el archivo `MainAnalizador.java`.
    * Ejecuta el método `main`.

## Salida Esperada

* Si el código en `ProgramaTiny.txt` es sintácticamente correcto, la consola imprimirá:
    ```
    ACEPTA
    ```

* Si el código es incorrecto, la consola imprimirá "RECHAZA" seguido de un mensaje de error detallado:
    ```
    RECHAZA
    Error Sintáctico [linea 3] en 'else': Se esperaba 'end' para cerrar el 'if'.
    ```