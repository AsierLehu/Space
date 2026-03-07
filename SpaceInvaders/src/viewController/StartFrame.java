package viewController;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.Observer;

import model.Espacio;

@SuppressWarnings("deprecation")
public class StartFrame extends JFrame implements Observer {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
    public StartFrame() {
        setTitle("Space Invaders");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Panel central con fondo negro
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(400, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);

        // T�tulo
        JLabel titulo = new JLabel("SPACE INVADERS");
        titulo.setForeground(Color.GREEN);
        titulo.setFont(new Font("Monospaced", Font.BOLD, 28));
        gbc.gridy = 0;
        panel.add(titulo, gbc);

        // Subt�tulo
        JLabel subtitulo = new JLabel("Sprint 1");
        subtitulo.setForeground(Color.GRAY);
        subtitulo.setFont(new Font("Monospaced", Font.PLAIN, 14));
        gbc.gridy = 1;
        panel.add(subtitulo, gbc);

        // Instrucci�n para iniciar
        JLabel pressSpace = new JLabel("Pulsa SPACE para jugar");
        pressSpace.setForeground(Color.GREEN);
        pressSpace.setFont(new Font("Monospaced", Font.BOLD, 14));
        gbc.gridy = 2;
        panel.add(pressSpace, gbc);

        // Controles
        JLabel controles = new JLabel("FLECHAS mover   |   SPACE disparar");
        controles.setForeground(Color.DARK_GRAY);
        controles.setFont(new Font("Monospaced", Font.PLAIN, 11));
        gbc.gridy = 3;
        panel.add(controles, gbc);

        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Foco en el frame para capturar teclado
        setFocusable(true);
        requestFocusInWindow();

        // Añadir el Controller (clase privada interna)
        Controller controller = new Controller();
        addKeyListener(controller);
        
        Espacio.getEspacio().addObserver(this);
    }

    private void iniciarJuego() {
        Espacio.getEspacio().cambiarAMain();

        
    }

	@Override
	public void update(Observable o, Object arg) {
	    // Cuando Espacio notifica cambios (ej: cambiarAMain()),
	    // esta ventana de inicio debe cerrarse y abrir la ventana del juego
	    if (o instanceof Espacio) {

            Espacio.getEspacio().deleteObserver(this);

	        // Cerrar esta ventana de inicio
	        this.setVisible(false);
	        
	        // Abrir la ventana principal del juego
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
            // Solo SPACE inicia el juego
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                iniciarJuego();
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
