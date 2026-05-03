package viewController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.Observer;

import model.Espacio;
import model.JugadorBueno;

@SuppressWarnings("deprecation")
public class StartFrame extends JFrame implements Observer {

    private JLabel etiquetaNave;

    /** Selección en pantalla de inicio; solo se copia a JugadorBueno al pulsar SPACE. */
    private String tipoNavePendiente = "Nave1";

	/**
	 * Create the frame.
	 */
    public StartFrame() {
        setTitle("Space Invaders");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Panel principal con componentes de estrellas
        Component panel = crearPanelPrincipalConEstrellas();

        add(panel); // Añadir el panel principal al frame
        pack(); // Ajustar el tamaño del frame al contenido
        setLocationRelativeTo(null); // Centrar la ventana en la pantalla
        setVisible(true); // Hacer visible la ventana

        // Configurar el foco para capturar eventos de teclado
        setFocusable(true); // Permitir que el frame reciba el foco
        requestFocusInWindow(); // Solicitar el foco activamente

        Controller controller = new Controller(); 
        addKeyListener(controller); 
        
        Espacio.getEspacio().addObserver(this);
    }

    private void actualizarTextoNave() {
        etiquetaNave.setText("Nave elegida: " + tipoNavePendiente + "   (pulsa 1, 2, 3 o 4 para cambiar)");
    }

    /**
     * Crea el panel principal con estrellas como componentes de Swing
     */
    private Component crearPanelPrincipalConEstrellas() {
        // Fondo con imagen escalada al tamaño del frame
        java.net.URL urlImagen = getClass().getResource("/images/fondo1.png");
        Image imgEscalada = new ImageIcon(urlImagen).getImage().getScaledInstance(900, 700, Image.SCALE_SMOOTH);
        JLabel panelFondo = new JLabel(new ImageIcon(imgEscalada));
        panelFondo.setPreferredSize(new Dimension(900, 700));

        // Panel de contenido con GridBagLayout
        JPanel panelContenido = new JPanel(new GridBagLayout());
        panelContenido.setOpaque(false); // Transparente para que se vea el fondo
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;

        // Espacio vertical arriba: absorbe altura extra y baja el bloque de texto
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        JPanel espacioSuperior = new JPanel();
        espacioSuperior.setOpaque(false);
        panelContenido.add(espacioSuperior, gbc);

        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;

        // Selección de nave (1 / 2 / 3 / 4)
        etiquetaNave = new JLabel();
        etiquetaNave.setForeground(new Color(255, 200, 0));
        etiquetaNave.setFont(new Font("Monospaced", Font.BOLD, 20));
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 20, 20, 20);
        panelContenido.add(etiquetaNave, gbc);
        actualizarTextoNave();

        // Instrucción para iniciar (pulsante)
        JLabel pressSpace = new JLabel(">> PULSA SPACE PARA JUGAR <<");
        pressSpace.setForeground(new Color(0, 255, 150));
        pressSpace.setFont(new Font("Monospaced", Font.BOLD, 26));
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 20, 30, 20);
        panelContenido.add(pressSpace, gbc);

        // Controles (con mejor formato)
        JLabel control1 = new JLabel("1 / 2 / 3  ==  Tipo de nave");
        control1.setForeground(new Color(150, 150, 255));
        control1.setFont(new Font("Monospaced", Font.PLAIN, 16));
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 20, 10, 20);
        panelContenido.add(control1, gbc);

        JLabel control2 = new JLabel("FLECHAS (^ v < >)  ==  Mover nave");
        control2.setForeground(new Color(150, 150, 255));
        control2.setFont(new Font("Monospaced", Font.PLAIN, 16));
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 20, 10, 20);
        panelContenido.add(control2, gbc);

        JLabel control3 = new JLabel("SPACE  ==  Disparar");
        control3.setForeground(new Color(150, 150, 255));
        control3.setFont(new Font("Monospaced", Font.PLAIN, 16));
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 20, 10, 20);
        panelContenido.add(control3, gbc);

        JLabel control4 = new JLabel("M  ==  Cambiar tipo de disparo");
        control4.setForeground(new Color(150, 150, 255));
        control4.setFont(new Font("Monospaced", Font.PLAIN, 16));
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 20, 60, 20);
        panelContenido.add(control4, gbc);
        
        // Poner el contenido dentro de la imagen de fondo
        panelFondo.setLayout(new BorderLayout());
        panelFondo.add(panelContenido, BorderLayout.CENTER);
        
        return panelFondo;
    }


	@Override
	public void update(Observable o, Object arg) {
		int[] datos = (int[]) arg;
        int tipo = datos[0];
	    if (tipo == 9) { // Notificación para cambiar de pantalla
	        Espacio.getEspacio().deleteObserver(this);
            this.setVisible(false);
            new MainFrame();
        }
    }

    /**
     * Controlador para gestionar la interacción del usuario con la pantalla de inicio
     * Clase privada interna según patrón MVC
     */
    private class Controller implements KeyListener {

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

        @Override
        public void keyReleased(KeyEvent e) {
            // No se necesita acción al soltar la tecla
        }

        @Override
        public void keyTyped(KeyEvent e) {
            // No se necesita acción
        }
    }
}
