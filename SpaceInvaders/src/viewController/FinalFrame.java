package viewController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.Observer;

import model.Espacio;

@SuppressWarnings("deprecation")
public class FinalFrame extends JFrame implements Observer {

    private boolean esVictoria;
    private int puntuacion;

	/**
	 * Create the frame.
	 */
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

    // ==================== MÉTODOS GR�FICOS ====================

    private Component crearPanelPrincipal() {
        Component comp = crearPanelConFondoYEstrellas();
        if (comp instanceof JComponent) {
            ((JComponent) comp).setPreferredSize(new Dimension(900, 700));
        }
        return comp;
    }

    private Component crearPanelConFondoYEstrellas() {
        // Fondo con imagen escalada al tamaño del frame
        java.net.URL urlImagen = getClass().getResource("/images/fondo.png");
        Image imgEscalada = new ImageIcon(urlImagen).getImage().getScaledInstance(900, 700, Image.SCALE_SMOOTH);
        JLabel panelFondo = new JLabel(new ImageIcon(imgEscalada));
        panelFondo.setPreferredSize(new Dimension(900, 700));

        // Panel de contenido con GridBagLayout
        JPanel panelContenido = new JPanel(new GridBagLayout());
        panelContenido.setOpaque(false); // Transparente para ver el fondo
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(100, 20, 100, 20);

        // Título del mensaje de fin
        JLabel titulo = crearTituloMensaje();
        gbc.gridy = 0;
        panelContenido.add(titulo, gbc);
        
        // Puntuacion obtenida
        JLabel labelPuntuacion = new JLabel("Puntuacion: " + puntuacion);
        labelPuntuacion.setForeground(Color.WHITE);
        labelPuntuacion.setFont(new Font("Monospaced", Font.BOLD, 28));
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 20, 20, 20);
        panelContenido.add(labelPuntuacion, gbc);

        // Instrucción para volver
        JLabel instruccion = crearInstruccion();
        gbc.gridy = 2;
        gbc.insets = new Insets(50, 20, 50, 20);
        panelContenido.add(instruccion, gbc);
        
        panelFondo.setLayout(new BorderLayout());
        panelFondo.add(panelContenido, BorderLayout.CENTER);
        
        return panelFondo;
    }

    private JLabel crearTituloMensaje() {
        String texto = esVictoria ? "VICTORIA" : "GAME OVER";
        Color color = esVictoria ? new Color(0, 255, 100) : new Color(255, 102, 102);
        
        JLabel titulo = new JLabel(texto);
        titulo.setForeground(color);
        titulo.setFont(new Font("Monospaced", Font.BOLD, 72));
        return titulo;
    }

    private JLabel crearInstruccion() {
        JLabel instruccion = new JLabel(">> PULSA SPACE PARA VOLVER <<");
        Color color = esVictoria ? new Color(0, 255, 100) : new Color(255, 102, 102);
        instruccion.setForeground(color);
        instruccion.setFont(new Font("Monospaced", Font.BOLD, 26));
        return instruccion;
    }

    // ==================== MÉTODOS DE LÓGICA ====================

    private void volverAlMenu() {
        Espacio.getEspacio().deleteObserver(this);
        this.setVisible(false);
        new StartFrame();
    }

    // ==================== OBSERVER ====================

	@Override
	public void update(Observable o, Object arg) {
		// No se necesita procesar notificaciones
	}

    // ==================== CONTROLADOR ====================

    /**
     * Controlador para gestionar la interacción del usuario
     * Clase privada interna según patrón MVC
     */
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
