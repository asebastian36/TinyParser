package com.tinyparser;

import javax.swing.SwingUtilities;

/**
 * Clase principal modificada.
 * Su única responsabilidad ahora es iniciar la interfaz gráfica de usuario (GUI).
 * La lógica de análisis anterior se ha movido a PanelAnalizador.java.
 */
public class MainAnalizador {

    public static void main(String[] args) {
        // Asegura que la GUI se cree y se muestre en el hilo de despacho de eventos de Swing.
        SwingUtilities.invokeLater(() -> {
            // Crea una instancia de la ventana principal y la hace visible.
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}