package viewController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.Observer;

import model.Espacio;
import model.JugadorBueno;

/**
 * Ventana principal del juego: tablero 100×60, escucha del modelo y teclado.
 */
@SuppressWarnings("deprecation")
public class MainFrame extends JFrame implements Observer {

    private static Color COLOR_ENEMIGO = Color.RED;
    private static Color COLOR_DISPARO = Color.WHITE;

    private static Color COLOR_NAVE1_VERDE = Color.GREEN;
    private static Color COLOR_NAVE2_AZUL = Color.BLUE;
    private static Color COLOR_NAVE3_MORADO = Color.MAGENTA;
    private static Color COLOR_NAVE4_AMARILLO = Color.YELLOW;

    private JLabel[][] celdas;
    private JLabel labelPuntuacion;

    /** Construye la ventana, se registra como observador de {@link Espacio} y monta el panel en capas. */
    public MainFrame() {
        Espacio.getEspacio().addObserver(this);

        setTitle("Space Invaders - Juego");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initPanel();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /** Recibe mensajes numéricos del modelo y actualiza celdas o transición de pantalla. */
    @Override
    public void update(Observable o, Object arg) {
        int[] datos = (int[]) arg;
        procesarNotificacion(datos);
    }

    /** Construye el grid de etiquetas, fondo y capa de puntuación. */
    private void initPanel() {
        int cols = 100;
        int rows = 60;
        int ancho = cols * 10;
        int alto = rows * 10;

        celdas = new JLabel[cols][rows];

        java.net.URL urlImagen = getClass().getResource("/images/fondo2.png");
        Image imgEscalada = new ImageIcon(urlImagen).getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        JLabel fondoLabel = new JLabel(new ImageIcon(imgEscalada));
        fondoLabel.setBounds(0, 0, ancho, alto);

        JPanel gridPanel = new JPanel(new GridLayout(rows, cols, 0, 0));
        gridPanel.setOpaque(false);
        gridPanel.setBounds(0, 0, ancho, alto);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                JLabel lbl = new JLabel();
                lbl.setPreferredSize(new Dimension(10, 10));
                lbl.setOpaque(false);
                celdas[x][y] = lbl;
                gridPanel.add(lbl);
            }
        }

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(ancho, alto));
        layeredPane.add(fondoLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(gridPanel, JLayeredPane.PALETTE_LAYER);

        labelPuntuacion = new JLabel("Puntuacion: 0");
        labelPuntuacion.setForeground(Color.WHITE);
        labelPuntuacion.setFont(new Font("Monospaced", Font.BOLD, 16));
        labelPuntuacion.setBounds(10, 5, 250, 25);
        layeredPane.add(labelPuntuacion, JLayeredPane.MODAL_LAYER);
        addKeyListener(new Controller());
        add(layeredPane);
    }

    /** Despacha por código de mensaje las acciones de pintado, borrado o fin de partida. */
    private void procesarNotificacion(int[] datos) {
        int tipo = datos[0];

        switch (tipo) {

            case 1:
                pintarCelda(datos[1], datos[2], COLOR_DISPARO);
                break;

            case 2:
                borrarCelda(datos[1], datos[2]);
                pintarCelda(datos[3], datos[4], COLOR_DISPARO);
                break;

            case 3:
                borrarCelda(datos[1], datos[2]);
                break;

            case 12:
                borrarCelda(datos[1], datos[2]);
                break;

            case 14:
                pintarCelda(datos[1], datos[2], COLOR_ENEMIGO);
                break;

            case 7:
                System.out.println("Game Over");
                abrirFinalFrame(false, datos[1]);
                break;
            case 8:
                System.out.println("Victoria");
                abrirFinalFrame(true, datos[1]);
                break;

            case 10:
                borrarCelda(datos[1], datos[2]);
                break;

            case 15:
                pintarCelda(datos[1], datos[2], COLOR_NAVE1_VERDE);
                break;

            case 16:
                pintarCelda(datos[1], datos[2], COLOR_NAVE2_AZUL);
                break;

            case 17:
                pintarCelda(datos[1], datos[2], COLOR_NAVE3_MORADO);
                break;

            case 21:
                pintarCelda(datos[1], datos[2], COLOR_NAVE4_AMARILLO);
                break;

            case 22:
                labelPuntuacion.setText("Puntuacion: " + datos[1]);
                break;
        }
    }

    /** Oculta el juego y abre la pantalla de resultado con la puntuación. */
    private void abrirFinalFrame(boolean victoria, int puntuacion) {
        Espacio.getEspacio().deleteObserver(this);
        this.setVisible(false);
        new FinalFrame(victoria, puntuacion);
    }

    /** Deja la celda transparente (sin color de foreground). */
    private void borrarCelda(int x, int y) {
        if (esValido(x, y)) {
            celdas[x][y].setOpaque(false);
            celdas[x][y].repaint();
        }
    }

    /** Rellena la celda con el color indicado y la marca opaca. */
    private void pintarCelda(int x, int y, Color c) {
        if (esValido(x, y)) {
            celdas[x][y].setOpaque(true);
            celdas[x][y].setBackground(c);
            celdas[x][y].repaint();
        }
    }

    /** Comprueba límites del tablero lógico 100×60. */
    private boolean esValido(int x, int y) {
        return x >= 0 && x < 100 && y >= 0 && y < 60;
    }

    /** Teclado en partida: movimiento, disparo y cambio de estrategia. */
    private class Controller implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:  JugadorBueno.getJugadorBueno().mover(-1, 0); break;
                case KeyEvent.VK_RIGHT: JugadorBueno.getJugadorBueno().mover(1, 0); break;
                case KeyEvent.VK_UP:    JugadorBueno.getJugadorBueno().mover(0, -1); break;
                case KeyEvent.VK_DOWN:  JugadorBueno.getJugadorBueno().mover(0, 1); break;
                case KeyEvent.VK_SPACE: JugadorBueno.getJugadorBueno().disparar(); break;
                case KeyEvent.VK_M:     JugadorBueno.getJugadorBueno().cambiarTipoDisparo(); break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }
    }
}
