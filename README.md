# Proyecto: Analizador Tiny y Graficador de Funciones

## Descripción

Este proyecto es una aplicación de escritorio Java (construida con Swing) que combina dos herramientas principales en una sola interfaz:

1.  **Analizador Sintáctico para el Lenguaje "Tiny"**: Un analizador descendente recursivo que lee un archivo `ProgramaTiny.txt` y determina si su contenido se adhiere a la gramática formal del lenguaje.
2.  **Graficador de Funciones Polinomiales**: Una herramienta interactiva que analiza, valida y renderiza expresiones matemáticas en tiempo real, actualizando el gráfico con cada pulsación de tecla.

La aplicación presenta un menú principal para seleccionar qué módulo utilizar.

## Características

### Módulo 1: Analizador de Lenguaje Tiny

* **Análisis Léxico Completo:** El `Scanner` reconoce el léxico del lenguaje Tiny (`if`, `then`, `:=`, `read`, etc.).
* **Parser Descendente Recursivo:** La estructura del `Parser` mapea directamente las reglas de la gramática BNF del lenguaje.
* **Gramática Completa:** Implementa todas las sentencias definidas en la especificación (`sent-if`, `sent-repeat`, `sent-assign`, `sent-read`, `sent-write`).
* **Reporte de Errores:** La interfaz muestra "ACEPTA" si el código es válido, o "RECHAZA" con un mensaje de error claro si falla el análisis.

### Módulo 2: Graficador de Funciones

* **Interfaz Gráfica Interactiva:** Construido con componentes de Swing (`JFrame`, `JPanel`, `JTextField`).
* **Graficación en Tiempo Real:** El gráfico se actualiza instantáneamente con cada pulsación de tecla en el campo de expresión.
* **Validación de Sintaxis en Tiempo Real:** El sistema detecta errores (ej. paréntesis sin cerrar, operadores mal formados) mientras el usuario escribe y muestra un mensaje de estado.
* **Manejo de Precedencia de Operadores:** Utiliza un analizador con un Árbol de Sintaxis Abstracta (AST) para respetar el orden matemático de las operaciones (potencia, multiplicación/división, suma/resta).
* **Renderizado Personalizado:** Un `JPanel` personalizado (`PanelLienzo`) dibuja los ejes X/Y y la curva de la función.

## Operaciones Soportadas por el Graficador

El graficador en tiempo real soporta las siguientes operaciones:

| Operación | Caracter(es) | Ejemplo | Descripción |
| :--- | :--- | :--- | :--- |
| Suma | `+` | `x + 2` | Adición estándar. |
| Resta | `-` | `x - 1` | Sustracción estándar. |
| Multiplicación | `*` | `2 * x` | Multiplicación. |
| División | `/` | `x / 3` | División. |
| Potencia | `^` | `x^2` | Exponenciación (elevado a la potencia). |
| Negación Unaria | `-` | `-x` | Cambia el signo del número o variable. |
| Paréntesis | `( ... )` | `(x + 2) * 5` | Agrupa operaciones. Esencial para la precedencia. |
| Variable | `x` | `3 * x` | La única variable reconocida. |
| Números | `0-9` y `.` | `1.5` | Se aceptan números decimales. |

## Estructura del Proyecto

Los archivos del proyecto se organizan por función (Interfaz Gráfica vs. Lógica):

### Archivos de Interfaz Gráfica (GUI - Swing)

* `MainAnalizador.java`: Punto de entrada. Lanza la interfaz gráfica.
* `VentanaPrincipal.java`: El `JFrame` principal que contiene el `CardLayout` para cambiar de modo.
* `PanelAnalizador.java`: La interfaz de usuario para el módulo del analizador Tiny.
* `PanelGraficador.java`: La interfaz de usuario para el módulo del graficador.
* `PanelLienzo.java`: El componente `JPanel` personalizado que dibuja el gráfico.

### Archivos de Lógica (Analizador Tiny)

* `Parser.java`: Analizador sintáctico para el lenguaje Tiny.
* `Scanner.java`: Analizador léxico para el lenguaje Tiny.
* `Token.java`: Clase de datos para los tokens de Tiny.
* `TipoToken.java`: Enumeración de los tipos de token de Tiny.

### Archivos de Lógica (Graficador de Funciones)

* `com/tinyparser/graficador/`:
    * `GraficadorLexer.java`: Analizador léxico para las expresiones matemáticas.
    * `GraficadorParser.java`: Analizador sintáctico que construye el AST para las matemáticas.
    * `ExcepcionParseoGraficador.java`: Excepción personalizada para errores de sintaxis.
    * `TipoTokenGraficador.java`: Enumeración de los tipos de token matemáticos.
    * `TokenGraficador.java`: Clase de datos para los tokens matemáticos.
* `com/tinyparser/graficador/ast/`:
    * `Nodo.java`: Interfaz para el Árbol de Sintaxis Abstracta (AST).
    * `NodoBinario.java`: Nodos del AST para operaciones binarias (`+`, `*`, `^`, etc.).
    * `NodoUnario.java`: Nodo del AST para negación (`-x`).
    * `NodoNumero.java`: Nodo del AST para valores numéricos.
    * `NodoVariable.java`: Nodo del AST para la variable `x`.

---

## Notas Técnicas del Analizador Tiny

Esta sección detalla conceptos clave en la construcción del *Módulo 1 (Analizador Tiny)*.

### Gramática BNF (Backus-Naur Form)

La gramática BNF proporcionada es la especificación formal de la sintaxis del lenguaje Tiny. Actúa como el "plano" para construir el `Parser`. En un analizador descendente recursivo, existe un mapeo directo:

* **Símbolos No Terminales** (ej. `sentencia`, `exp-simple`) se convierten en métodos (ej. `private void sentencia()`).
* **Símbolos Terminales** (ej. `IF`, `ID`) son consumidos por los métodos de ayuda `consumir()` y `coincidir()`.
* **La elección** (ej. `sentencia \to sent-if | sent-repeat`) se implementa con `if-else if` basado en el token actual.

### Manejo de la Recursión por la Izquierda

Un problema común en las gramáticas para analizadores descendentes es la **recursión por la izquierda**.

* **El Problema:** Ocurre cuando una regla se define a sí misma como su primer símbolo (ej. `exp-simple \to exp-simple opsuma term`). Si se implementa directamente, crea un bucle infinito (`StackOverflowError`).
* **La Solución:** Se reescribe la gramática. La regla `A \to A \alpha | \beta` se transforma en `A \to \beta (\alpha)*`.
* **Implementación en el Código:** La gramática `exp-simple \to term (opsuma term)*` se implementa en el `Parser` de Tiny con un bucle `while`:

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

---

## Cómo Compilar y Ejecutar

### Prerrequisitos
* Java Development Kit (JDK) 11 o superior.
* Un IDE como IntelliJ IDEA.

### Pasos
1.  **Configurar el Proyecto:**
    * Crea un nuevo proyecto Java.
    * Crea la estructura de paquetes: `com.tinyparser`, `com.tinyparser.graficador`, y `com.tinyparser.graficador.ast`.
    * Añade todos los archivos `.java` en sus respectivos paquetes.

2.  **Crear el Archivo de Entrada (para el Módulo Tiny):**
    * En la **carpeta raíz** de tu proyecto (al mismo nivel que la carpeta `src`), crea un archivo llamado `ProgramaTiny.txt`.
    * Escribe o pega el código Tiny que deseas analizar en este archivo.

3.  **Ejecutar:**
    * Abre el archivo `MainAnalizador.java`.
    * Ejecuta el método `main`.

## Salida Esperada

* El programa lanzará la ventana principal con dos opciones.
* **Si seleccionas "Analizar Lenguaje Tiny"**: La interfaz leerá `ProgramaTiny.txt` y mostrará "ACEPTA" o "RECHAZA" con el motivo del error.
* **Si seleccionas "Graficador de Funciones"**: La interfaz mostrará un campo de texto y un lienzo. Al escribir una expresión (ej. `x^2`), el gráfico aparecerá y se actualizará en tiempo real. Si la sintaxis es incorrecta (ej. `(x+2`), se mostrará un mensaje de error.