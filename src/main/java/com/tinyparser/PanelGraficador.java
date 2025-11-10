package com.tinyparser;

import com.tinyparser.graficador.*;
import com.tinyparser.graficador.ast.Nodo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

/**
 * Panel que contiene la lógica para el graficador en tiempo real.
 */
public class PanelGraficador extends JPanel {

    private VentanaPrincipal ventanaPrincipal;
    private JTextField campoExpresion;
    private JLabel etiquetaEstado;
    private PanelLienzo panelLienzo;
    private GraficadorLexer lexer;
    private GraficadorParser parser;

    public PanelGraficador(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        this.lexer = new GraficadorLexer();
        this.parser = new GraficadorParser();

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Panel Superior (Controles) ---
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));

        JButton btnVolverMenu = new JButton("Volver al Menú");
        panelSuperior.add(btnVolverMenu, BorderLayout.WEST);

        // --- Panel de Entrada (para f(x) y el campo de texto) ---
        // Se crea este panel intermedio para que el campo de texto
        // pueda expandirse correctamente.
        JPanel panelEntrada = new JPanel(new BorderLayout(5, 5));
        JLabel etiquetaFdeX = new JLabel("f(x) = ");
        etiquetaFdeX.setFont(new Font("Arial", Font.BOLD, 16));
        panelEntrada.add(etiquetaFdeX, BorderLayout.WEST); // Label al oeste

        campoExpresion = new JTextField("x^2");
        campoExpresion.setFont(new Font("Monospaced", Font.PLAIN, 16));
        panelEntrada.add(campoExpresion, BorderLayout.CENTER); // Campo de texto en el centro (expandible)

        // Añadir el panel de entrada al panel superior
        panelSuperior.add(panelEntrada, BorderLayout.CENTER);

        // --- Panel Central (Gráfico) ---
        panelLienzo = new PanelLienzo();
        panelLienzo.setBackground(Color.WHITE);

        // --- Panel Inferior (Estado) ---
        etiquetaEstado = new JLabel("OK");
        etiquetaEstado.setFont(new Font("Arial", Font.ITALIC, 14));
        etiquetaEstado.setForeground(new Color(0, 128, 0));
        etiquetaEstado.setHorizontalAlignment(SwingConstants.CENTER);

        // --- Añadir componentes al panel ---
        add(panelSuperior, BorderLayout.NORTH);
        add(panelLienzo, BorderLayout.CENTER);
        add(etiquetaEstado, BorderLayout.SOUTH);

        // --- Lógica de Eventos ---
        btnVolverMenu.addActionListener(e -> ventanaPrincipal.mostrarMenu());

        // El "oyente" para la actualización en tiempo real
        campoExpresion.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarGrafico();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarGrafico();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarGrafico();
            }
        });

        // Graficar la expresión inicial ("x^2")
        actualizarGrafico();
    }

    /**
     * Esta es la función clave que se llama con cada pulsación de tecla.
     */
    private void actualizarGrafico() {
        String expresion = campoExpresion.getText();
        if (expresion.trim().isEmpty()) {
            etiquetaEstado.setText("Ingrese una expresión (ej. x^2 + 1).");
            etiquetaEstado.setForeground(Color.GRAY);
            panelLienzo.setArbol(null);
            panelLienzo.repaint();
            return;
        }

        try {
            // 1. Lexer: Convertir texto en tokens
            List<TokenGraficador> tokens = lexer.escanear(expresion);

            // 2. Parser: Convertir tokens en Árbol de Sintaxis Abstracta (AST)
            Nodo arbol = parser.parsear(tokens);

            // 3. Éxito: Enviar el AST al lienzo para que lo dibuje
            etiquetaEstado.setText("OK");
            etiquetaEstado.setForeground(new Color(0, 128, 0));
            panelLienzo.setArbol(arbol);

        } catch (ExcepcionParseoGraficador e) {
            // 4. Error de sintaxis: Mostrar error y limpiar el lienzo
            etiquetaEstado.setText("Error: " + e.getMessage());
            etiquetaEstado.setForeground(Color.RED);
            panelLienzo.setArbol(null);
        } catch (Exception e) {
            // 5. Otro error (ej. léxico)
            etiquetaEstado.setText("Error: " + e.getMessage());
            etiquetaEstado.setForeground(Color.RED);
            panelLienzo.setArbol(null);
        }

        // 6. Repintar el lienzo
        panelLienzo.repaint();
    }
}