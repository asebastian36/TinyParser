package com.tinyparser;

import com.tinyparser.graficador.ast.Nodo;

import javax.swing.*;
import java.awt.*;

/**
 * Panel de dibujo personalizado donde se renderiza el gráfico.
 * Sobrescribe paintComponent para dibujar los ejes y la función.
 */
public class PanelLienzo extends JPanel {

    private Nodo arbol;
    private final double RANGO_X_MIN = -10.0;
    private final double RANGO_X_MAX = 10.0;
    private final double RANGO_Y_MIN = -10.0;
    private final double RANGO_Y_MAX = 10.0;
    private final int MARGEN = 30;

    public void setArbol(Nodo arbol) {
        this.arbol = arbol;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();

        // Dibujar ejes
        dibujarEjes(g2d, ancho, alto);

        // Si no hay un árbol válido, no dibujar función
        if (arbol == null) {
            return;
        }

        // Dibujar función
        dibujarFuncion(g2d, ancho, alto);
    }

    private void dibujarEjes(Graphics2D g2d, int ancho, int alto) {
        // --- Transformar coordenadas ---
        // (0,0) del mundo real al centro del panel
        int xOrigen = ancho / 2;
        int yOrigen = alto / 2;

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setStroke(new BasicStroke(1));

        // Eje X
        g2d.drawLine(0, yOrigen, ancho, yOrigen);
        // Eje Y
        g2d.drawLine(xOrigen, 0, xOrigen, alto);

        // Etiquetas y marcas
        g2d.setColor(Color.BLACK);

        // Marcas en eje X
        double escalaX = (ancho - 2 * MARGEN) / (RANGO_X_MAX - RANGO_X_MIN);
        for (double x = RANGO_X_MIN; x <= RANGO_X_MAX; x++) {
            if (Math.abs(x) > 0.1) { // No dibujar en el origen
                int px = (int) (xOrigen + x * escalaX);
                g2d.drawLine(px, yOrigen - 5, px, yOrigen + 5);
                g2d.drawString(String.format("%.0f", x), px - 5, yOrigen + 20);
            }
        }

        // Marcas en eje Y
        double escalaY = (alto - 2 * MARGEN) / (RANGO_Y_MAX - RANGO_Y_MIN);
        for (double y = RANGO_Y_MIN; y <= RANGO_Y_MAX; y++) {
            if (Math.abs(y) > 0.1) {
                int py = (int) (yOrigen - y * escalaY); // Y invertido en Swing
                g2d.drawLine(xOrigen - 5, py, xOrigen + 5, py);
                g2d.drawString(String.format("%.0f", y), xOrigen + 10, py + 5);
            }
        }

        // Origen (0,0)
        g2d.drawString("0", xOrigen + 5, yOrigen + 20);
    }

    private void dibujarFuncion(Graphics2D g2d, int ancho, int alto) {
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(2));

        double escalaX = (ancho - 2 * MARGEN) / (RANGO_X_MAX - RANGO_X_MIN);
        double escalaY = (alto - 2 * MARGEN) / (RANGO_Y_MAX - RANGO_Y_MIN);
        int xOrigen = ancho / 2;
        int yOrigen = alto / 2;

        Polygon poligono = new Polygon();

        // Iterar sobre los píxeles del eje X para una curva suave
        for (int px_actual = MARGEN; px_actual <= ancho - MARGEN; px_actual++) {
            // Convertir píxel a coordenada x
            double x = (px_actual - xOrigen) / escalaX;

            try {
                // Evaluar y
                double y = arbol.evaluar(x);

                // Convertir coordenada y a píxel
                // Se invierte la Y: (yOrigen - y * escalaY)
                int py_actual = (int) (yOrigen - y * escalaY);

                // Añadir punto al polígono
                poligono.addPoint(px_actual, py_actual);

            } catch (Exception e) {
                // Salto en la función (ej. división por cero), no añadir punto
            }
        }

        // Dibujar la línea poligonal
        g2d.drawPolyline(poligono.xpoints, poligono.ypoints, poligono.npoints);
    }
}