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
            // Solo SPACE inicia el juego
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                Espacio.getEspacio().cambiarAMain();
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
