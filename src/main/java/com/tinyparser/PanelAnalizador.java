package com.tinyparser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * Panel que contiene la lógica para analizar el código Tiny.
 * Reutiliza el código original de MainAnalizador.java.
 */
public class PanelAnalizador extends JPanel {

    private VentanaPrincipal ventanaPrincipal;
    private JTextArea areaResultado;
    private JButton btnAnalizar;
    private JButton btnVolverMenu;
    private JLabel etiquetaArchivo;

    public PanelAnalizador(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Panel Superior (Botones) ---
        JPanel panelSuperior = new JPanel(new BorderLayout());
        etiquetaArchivo = new JLabel("Analizando: ProgramaTiny.txt");
        etiquetaArchivo.setFont(new Font("Arial", Font.BOLD, 14));

        btnAnalizar = new JButton("Analizar Archivo");
        btnVolverMenu = new JButton("Volver al Menú");

        panelSuperior.add(etiquetaArchivo, BorderLayout.WEST);
        panelSuperior.add(btnAnalizar, BorderLayout.CENTER);
        panelSuperior.add(btnVolverMenu, BorderLayout.EAST);

        // --- Área de Texto Central (Resultados) ---
        areaResultado = new JTextArea();
        areaResultado.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaResultado.setEditable(false);
        areaResultado.setText("Presione 'Analizar Archivo' para comenzar...");
        JScrollPane scrollPane = new JScrollPane(areaResultado);

        // --- Añadir componentes al panel ---
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Lógica de Eventos ---
        btnVolverMenu.addActionListener(e -> ventanaPrincipal.mostrarMenu());
        btnAnalizar.addActionListener(e -> analizarArchivo());
    }

    private void analizarArchivo() {
        try {
            String rutaArchivo = "ProgramaTiny.txt";
            File archivo = new File(rutaArchivo);

            if (!archivo.exists()) {
                areaResultado.setForeground(Color.RED);
                areaResultado.setText("ERROR: No se encontró el archivo 'ProgramaTiny.txt' en la raíz del proyecto.");
                return;
            }

            String codigoFuente = new String(Files.readAllBytes(archivo.toPath()), StandardCharsets.UTF_8);

            // 1. Escanear (Análisis Léxico)
            Scanner scanner = new Scanner(codigoFuente);
            List<Token> tokens = scanner.escanearTokens();

            // 2. Parsear (Análisis Sintáctico)
            Parser parser = new Parser(tokens);
            parser.parse();

            // 3. Si llega aquí, es válido
            areaResultado.setForeground(new Color(0, 128, 0)); // Verde oscuro
            areaResultado.setText("--- ANÁLISIS COMPLETADO ---\n\n");
            areaResultado.append("RESULTADO: ACEPTA\n\n");
            areaResultado.append("--- CÓDIGO ANALIZADO ---\n");
            areaResultado.append(codigoFuente);

        } catch (Exception e) {
            // 4. Si algo falla (Error Léxico o Sintáctico)
            areaResultado.setForeground(Color.RED);
            areaResultado.setText("--- ANÁLISIS FALLIDO ---\n\n");
            areaResultado.append("RESULTADO: RECHAZA\n\n");
            areaResultado.append("MOTIVO: " + e.getMessage());
        }
    }
}