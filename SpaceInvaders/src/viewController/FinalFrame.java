package viewController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.Observer;

import model.Espacio;

/**
 * Pantalla de victoria o derrota con puntuación y opción de volver al menú.
 */
@SuppressWarnings("deprecation")
public class FinalFrame extends JFrame implements Observer {

    private boolean esVictoria;
    private int puntuacion;

    /** Monta la ventana final según resultado y registra observador por si el modelo notificara. */
    public FinalFrame(boolean victoria, int puntuacion) {
        this.esVictoria = victoria;
        this.puntuacion = puntuacion;

        setTitle("Space Invaders - Fin del Juego");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        Component panel = crearPanelPrincipal();
        add(panel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        setFocusable(true);
        requestFocusInWindow();

        Controller controller = new Controller();
        addKeyListener(controller);

        Espacio.getEspacio().addObserver(this);
    }

    /** Reservado; el modelo no usa esta ventana para mensajes adicionales. */
    @Override
    public void update(Observable o, Object arg) {
    }

    /** Envuelve el panel con fondo fijando tamaño preferido 900×700. */
    private Component crearPanelPrincipal() {
        Component comp = crearPanelConFondoYEstrellas();
        if (comp instanceof JComponent) {
            ((JComponent) comp).setPreferredSize(new Dimension(900, 700));
        }
        return comp;
    }

    /** Construye fondo, título VICTORIA/GAME OVER, puntuación e instrucción SPACE. */
    private Component crearPanelConFondoYEstrellas() {
        java.net.URL urlImagen = getClass().getResource("/images/fondo.png");
        Image imgEscalada = new ImageIcon(urlImagen).getImage().getScaledInstance(900, 700, Image.SCALE_SMOOTH);
        JLabel panelFondo = new JLabel(new ImageIcon(imgEscalada));
        panelFondo.setPreferredSize(new Dimension(900, 700));

        JPanel panelContenido = new JPanel(new GridBagLayout());
        panelContenido.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(100, 20, 100, 20);

        JLabel titulo = crearTituloMensaje();
        gbc.gridy = 0;
        panelContenido.add(titulo, gbc);

        JLabel labelPuntuacion = new JLabel("Puntuacion: " + puntuacion);
        labelPuntuacion.setForeground(Color.WHITE);
        labelPuntuacion.setFont(new Font("Monospaced", Font.BOLD, 28));
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 20, 20, 20);
        panelContenido.add(labelPuntuacion, gbc);

        JLabel instruccion = crearInstruccion();
        gbc.gridy = 2;
        gbc.insets = new Insets(50, 20, 50, 20);
        panelContenido.add(instruccion, gbc);

        panelFondo.setLayout(new BorderLayout());
        panelFondo.add(panelContenido, BorderLayout.CENTER);

        return panelFondo;
    }

    /** Etiqueta grande con el texto de resultado y color acorde. */
    private JLabel crearTituloMensaje() {
        String texto = esVictoria ? "VICTORIA" : "GAME OVER";
        Color color = esVictoria ? new Color(0, 255, 100) : new Color(255, 102, 102);

        JLabel titulo = new JLabel(texto);
        titulo.setForeground(color);
        titulo.setFont(new Font("Monospaced", Font.BOLD, 72));
        return titulo;
    }

    /** Línea que invita a pulsar SPACE para regresar a {@link StartFrame}. */
    private JLabel crearInstruccion() {
        JLabel instruccion = new JLabel(">> PULSA SPACE PARA VOLVER <<");
        Color color = esVictoria ? new Color(0, 255, 100) : new Color(255, 102, 102);
        instruccion.setForeground(color);
        instruccion.setFont(new Font("Monospaced", Font.BOLD, 26));
        return instruccion;
    }

    /** Cierra esta ventana y vuelve al menú inicial; deja de observar el espacio. */
    private void volverAlMenu() {
        Espacio.getEspacio().deleteObserver(this);
        this.setVisible(false);
        new StartFrame();
    }

    /** SPACE dispara {@link #volverAlMenu()}. */
    private class Controller implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                volverAlMenu();
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
