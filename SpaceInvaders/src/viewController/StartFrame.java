package viewController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

import model.Espacio;
import model.JugadorBueno;

/**
 * Pantalla de título: selección de nave y arranque de partida al pulsar SPACE.
 */
@SuppressWarnings("deprecation")
public class StartFrame extends JFrame implements Observer {

    private JLabel etiquetaNave;

    /** Selección en pantalla de inicio; solo se copia a JugadorBueno al pulsar SPACE. */
    private String tipoNavePendiente = "Nave1";

    /** Crea la ventana de inicio, el panel con fondo y se registra como observador de {@link Espacio}. */
    public StartFrame() {
        setTitle("Space Invaders");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        Component panel = crearPanelPrincipalConEstrellas();

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

    /** Ante el mensaje 9 del espacio, cierra esta ventana y abre {@link MainFrame}. */
    @Override
    public void update(Observable o, Object arg) {
        int[] datos = (int[]) arg;
        int tipo = datos[0];
        if (tipo == 9) {
            Espacio.getEspacio().deleteObserver(this);
            this.setVisible(false);
            new MainFrame();
        }
    }

    /** Actualiza el texto que muestra la nave pendiente de confirmar. */
    private void actualizarTextoNave() {
        etiquetaNave.setText("Nave elegida: " + tipoNavePendiente + "   (pulsa 1, 2, 3 o 4 para cambiar)");
    }

    /** Monta el fondo, textos de ayuda y la etiqueta de selección de nave. */
    private Component crearPanelPrincipalConEstrellas() {
        java.net.URL urlImagen = getClass().getResource("/images/fondo1.png");
        Image imgEscalada = new ImageIcon(urlImagen).getImage().getScaledInstance(900, 700, Image.SCALE_SMOOTH);
        JLabel panelFondo = new JLabel(new ImageIcon(imgEscalada));
        panelFondo.setPreferredSize(new Dimension(900, 700));

        JPanel panelContenido = new JPanel(new GridBagLayout());
        panelContenido.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;

        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        JPanel espacioSuperior = new JPanel();
        espacioSuperior.setOpaque(false);
        panelContenido.add(espacioSuperior, gbc);

        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;

        etiquetaNave = new JLabel();
        etiquetaNave.setForeground(new Color(255, 200, 0));
        etiquetaNave.setFont(new Font("Monospaced", Font.BOLD, 20));
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 20, 20, 20);
        panelContenido.add(etiquetaNave, gbc);
        actualizarTextoNave();

        JLabel pressSpace = new JLabel(">> PULSA SPACE PARA JUGAR <<");
        pressSpace.setForeground(new Color(0, 255, 150));
        pressSpace.setFont(new Font("Monospaced", Font.BOLD, 26));
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 20, 30, 20);
        panelContenido.add(pressSpace, gbc);

        JLabel control1 = new JLabel("1 / 2 / 3  ==  Tipo de nave");
        control1.setForeground(Color.WHITE);
        control1.setFont(new Font("Monospaced", Font.PLAIN, 16));
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 20, 10, 20);
        panelContenido.add(control1, gbc);

        JLabel control2 = new JLabel("FLECHAS (^ v < >)  ==  Mover nave");
        control2.setForeground(Color.WHITE);
        control2.setFont(new Font("Monospaced", Font.PLAIN, 16));
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 20, 10, 20);
        panelContenido.add(control2, gbc);

        JLabel control3 = new JLabel("SPACE  ==  Disparar");
        control3.setForeground(Color.WHITE);
        control3.setFont(new Font("Monospaced", Font.PLAIN, 16));
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 20, 10, 20);
        panelContenido.add(control3, gbc);

        JLabel control4 = new JLabel("M  ==  Cambiar tipo de disparo");
        control4.setForeground(Color.WHITE);
        control4.setFont(new Font("Monospaced", Font.PLAIN, 16));
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 20, 120, 20);
        panelContenido.add(control4, gbc);

        panelFondo.setLayout(new BorderLayout());
        panelFondo.add(panelContenido, BorderLayout.CENTER);

        return panelFondo;
    }

    /** Teclado en menú: teclas 1–4 eligen nave; SPACE confirma e inicializa al jugador. */
    private class Controller extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int k = e.getKeyCode();
            if (k == KeyEvent.VK_1) {
                tipoNavePendiente = "Nave1";
                actualizarTextoNave();
                return;
            }
            if (k == KeyEvent.VK_2) {
                tipoNavePendiente = "Nave2";
                actualizarTextoNave();
                return;
            }
            if (k == KeyEvent.VK_3) {
                tipoNavePendiente = "Nave3";
                actualizarTextoNave();
                return;
            }
            if (k == KeyEvent.VK_4) {
                tipoNavePendiente = "Nave4";
                actualizarTextoNave();
                return;
            }
            if (k == KeyEvent.VK_SPACE) {
                JugadorBueno.getJugadorBueno().inicializar(tipoNavePendiente);
            }
        }
    }
}
