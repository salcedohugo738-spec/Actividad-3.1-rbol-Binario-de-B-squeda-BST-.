/*
Salcedo Alvarez Hugo Emmanuel
Morales Escobar Juan Adrian
 */
package front_end;

import Nodos.Empleado;
import Nodos.NodoBST;
import back_end.ArbolBST;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

/**
 * Panel que dibuja el árbol con nodos y conexiones. Cada nodo muestra su id;
 * al pasar el mouse se ve el detalle del empleado. Se usa como "Arbol Visual"
 * dentro de GUI_ArbolBinarioBusqueda.
 *
 * @author ASUS
 */
public class PanelArbol extends JPanel {

    private static final int DIAMETRO = 46;
    private static final int SEP_X = 64;
    private static final int SEP_Y = 76;
    private static final int MARGEN = 36;

    /** Posición calculada de un nodo para dibujarlo y mostrar su tooltip. */
    private static class Caja {
        NodoBST nodo;
        int x;
        int y;
        Caja izq;
        Caja der;
    }

    private ArbolBST arbol;
    private Integer idResaltado;
    private Caja raizCaja;
    private final List<Caja> cajas = new ArrayList<>();
    private int contadorX;
    private int niveles;

    public PanelArbol() {
        setBackground(Color.WHITE);
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    /** Asigna el árbol a dibujar (puede ser null si aún no se ha creado). */
    public void setArbol(ArbolBST arbol) {
        this.arbol = arbol;
        this.idResaltado = null;
        recalcular();
    }

    /** Resalta (en color) el nodo con ese id; null quita el resaltado. */
    public void setIdResaltado(Integer id) {
        this.idResaltado = id;
        repaint();
    }

    /** Vuelve a calcular posiciones y repinta; llamar después de modificar el árbol. */
    public void actualizar() {
        recalcular();
    }

    private void recalcular() {
        cajas.clear();
        raizCaja = null;
        contadorX = 0;
        niveles = 0;
        if (arbol != null && arbol.getRaiz() != null) {
            raizCaja = construir(arbol.getRaiz(), 0);
        }
        revalidate();
        repaint();
    }

    /** La coordenada x sale del orden inorden, así los nodos nunca se encimen. */
    private Caja construir(NodoBST nodo, int nivel) {
        Caja c = new Caja();
        c.nodo = nodo;
        niveles = Math.max(niveles, nivel + 1);
        if (nodo.getIzquierdo() != null) {
            c.izq = construir(nodo.getIzquierdo(), nivel + 1);
        }
        c.x = MARGEN + contadorX * SEP_X + DIAMETRO / 2;
        contadorX++;
        c.y = MARGEN + nivel * SEP_Y + DIAMETRO / 2;
        if (nodo.getDerecho() != null) {
            c.der = construir(nodo.getDerecho(), nivel + 1);
        }
        cajas.add(c);
        return c;
    }

    @Override
    public Dimension getPreferredSize() {
        if (raizCaja == null) {
            return new Dimension(400, 200);
        }
        int ancho = MARGEN * 2 + contadorX * SEP_X;
        int alto = MARGEN * 2 + niveles * SEP_Y;
        return new Dimension(ancho, alto);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (arbol == null) {
            mensajeCentrado(g2, "Presione \"Crear Arbol\" para comenzar.");
        } else if (raizCaja == null) {
            mensajeCentrado(g2, "El árbol está vacío.");
        } else {
            g2.setStroke(new BasicStroke(1.8f));
            g2.setColor(new Color(120, 120, 120));
            for (Caja c : cajas) {          // primero las líneas
                if (c.izq != null) {
                    g2.drawLine(c.x, c.y, c.izq.x, c.izq.y);
                }
                if (c.der != null) {
                    g2.drawLine(c.x, c.y, c.der.x, c.der.y);
                }
            }
            g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
            FontMetrics fm = g2.getFontMetrics();
            for (Caja c : cajas) {          // luego los nodos encima
                Integer id = c.nodo.getDato().getId();
                boolean resaltado = id.equals(idResaltado);
                int x = c.x - DIAMETRO / 2;
                int y = c.y - DIAMETRO / 2;
                g2.setColor(resaltado ? new Color(255, 214, 102)
                        : (c == raizCaja ? new Color(163, 214, 163) : new Color(170, 205, 240)));
                g2.fillOval(x, y, DIAMETRO, DIAMETRO);
                g2.setColor(new Color(40, 60, 90));
                g2.drawOval(x, y, DIAMETRO, DIAMETRO);
                String texto = String.valueOf(id);
                if (fm.stringWidth(texto) > DIAMETRO - 6) {
                    texto = texto.substring(0, 3) + "…";
                }
                g2.setColor(Color.BLACK);
                g2.drawString(texto, c.x - fm.stringWidth(texto) / 2, c.y + fm.getAscent() / 2 - 1);
            }
        }
        g2.dispose();
    }

    private void mensajeCentrado(Graphics2D g2, String texto) {
        g2.setColor(Color.GRAY);
        g2.setFont(getFont().deriveFont(Font.ITALIC, 15f));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, (getWidth() - fm.stringWidth(texto)) / 2, getHeight() / 2);
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        for (Caja c : cajas) {
            int dx = e.getX() - c.x;
            int dy = e.getY() - c.y;
            if (dx * dx + dy * dy <= (DIAMETRO / 2) * (DIAMETRO / 2)) {
                Empleado emp = c.nodo.getDato();
                return "<html><b>ID " + emp.getId() + "</b><br>" + escapar(emp.getNombre())
                        + "<br>" + escapar(emp.getPuesto()) + " - " + escapar(emp.getDepartamento())
                        + "<br>Salario: " + String.format(java.util.Locale.US, "$%,.2f", emp.getSalario()) + "</html>";
            }
        }
        return null;
    }

    private static String escapar(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
