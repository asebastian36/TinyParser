package com.tinyparser;

import javax.swing.*;
import java.awt.*;

/**
 * VentanaPrincipal (JFrame) que actúa como el contenedor principal de la aplicación.
 * Utiliza un CardLayout para cambiar entre los diferentes "paneles" o "modos".
 */
public class VentanaPrincipal extends JFrame {

    private CardLayout cardLayout;
    private JPanel panelContenedor;
    private JPanel panelMenu;
    private PanelAnalizador panelAnalizador;
    private PanelGraficador panelGraficador;

    public VentanaPrincipal() {
        setTitle("Proyecto Analizador y Graficador");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana

        // Inicializa el CardLayout y el panel que lo contendrá
        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);

        // --- 1. Panel del Menú Principal ---
        panelMenu = new JPanel(new GridBagLayout());
        panelMenu.setBackground(Color.DARK_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Seleccione un modo de operación");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelMenu.add(titulo, gbc);

        JButton btnIrAnalizador = new JButton("Analizador de Lenguaje Tiny");
        btnIrAnalizador.setFont(new Font("Arial", Font.PLAIN, 18));
        btnIrAnalizador.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy++;
        panelMenu.add(btnIrAnalizador, gbc);

        JButton btnIrGraficador = new JButton("Graficador de Funciones en Tiempo Real");
        btnIrGraficador.setFont(new Font("Arial", Font.PLAIN, 18));
        btnIrGraficador.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy++;
        panelMenu.add(btnIrGraficador, gbc);

        // --- 2. Panel del Analizador Tiny ---
        panelAnalizador = new PanelAnalizador(this); // 'this' para volver al menú

        // --- 3. Panel del Graficador ---
        panelGraficador = new PanelGraficador(this); // 'this' para volver al menú

        // --- Lógica de navegación (CardLayout) ---
        btnIrAnalizador.addActionListener(e -> cardLayout.show(panelContenedor, "ANALIZADOR"));
        btnIrGraficador.addActionListener(e -> cardLayout.show(panelContenedor, "GRAFICADOR"));

        // Añadir los paneles al contenedor con nombres
        panelContenedor.add(panelMenu, "MENU");
        panelContenedor.add(panelAnalizador, "ANALIZADOR");
        panelContenedor.add(panelGraficador, "GRAFICADOR");

        // Añadir el contenedor principal al JFrame
        add(panelContenedor);

        // Mostrar el menú al inicio
        cardLayout.show(panelContenedor, "MENU");
    }

    /**
     * Método público para permitir a los paneles hijos volver al menú principal.
     */
    public void mostrarMenu() {
        cardLayout.show(panelContenedor, "MENU");
    }
}