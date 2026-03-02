package sp;

import javax.swing.border.EmptyBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class StartFrame extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartFrame frame = new StartFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

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

        // Título
        JLabel titulo = new JLabel("SPACE INVADERS");
        titulo.setForeground(Color.GREEN);
        titulo.setFont(new Font("Monospaced", Font.BOLD, 28));
        gbc.gridy = 0;
        panel.add(titulo, gbc);

        // Subtítulo
        JLabel subtitulo = new JLabel("Sprint 1");
        subtitulo.setForeground(Color.GRAY);
        subtitulo.setFont(new Font("Monospaced", Font.PLAIN, 14));
        gbc.gridy = 1;
        panel.add(subtitulo, gbc);

        // Instrucción para iniciar
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

        // Solo SPACE inicia el juego
        addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    iniciarJuego();
                }
            }
            @Override public void keyReleased(KeyEvent e) {}
            @Override public void keyTyped(KeyEvent e) {}
        });
    }

    private void iniciarJuego() {
        dispose();
        new MainFrame();
    }

}