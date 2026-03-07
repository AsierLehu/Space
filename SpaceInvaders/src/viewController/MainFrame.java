package viewController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.border.EmptyBorder;

import model.Disparo;
import model.Enemigo;
import model.Espacio;
import model.Jugador;

@SuppressWarnings("deprecation")
public class MainFrame extends JFrame implements Observer {

	// Tamaño de cada celda en píxeles de pantalla
    private static final int CELL_SIZE = 10;

    private JPanel gamePanel;

    // Referencia al modelo
    private Espacio espacio;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
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
	public MainFrame() {
		espacio = Espacio.getEspacio();
		espacio.addObserver(this);

        setTitle("Space Invaders — Juego");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initPanel();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // El foco debe estar en el frame para capturar teclado
        requestFocusInWindow();
	}
	
    private void initPanel() {
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujar(g);
            }
        };
        gamePanel.setBackground(Color.BLACK);
        gamePanel.setPreferredSize(new Dimension(
                espacio.getAnchura() * CELL_SIZE,
                espacio.getAltura()  * CELL_SIZE
        ));

        // Añadir el Controller (clase privada interna)
        Controller controller = new Controller();
        addKeyListener(controller);

        add(gamePanel);
    }

    private void dibujar(Graphics g) {

        // Fondo cuadrícula (opcional, ayuda a ver el tablero)
        g.setColor(new Color(20, 20, 20));
        for (int x = 0; x < espacio.getAnchura(); x++) {
            for (int y = 0; y < espacio.getAltura(); y++) {
                g.drawRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        // Dibujar jugador (nave con forma)
        Jugador jugador = espacio.getJugador();
        if (jugador != null && jugador.isVivo()) {
            dibujarNave(g, jugador.getX(), jugador.getY());
        }

        // Dibujar enemigos (píxel rojo)
        for (Enemigo e : espacio.getEnemigos()) {
            if (e.isVivo()) {
                g.setColor(Color.RED);
                g.fillRect(
                    e.getX() * CELL_SIZE,
                    e.getY() * CELL_SIZE,
                    CELL_SIZE, CELL_SIZE
                );
            }
        }

        // Dibujar disparo (píxel amarillo)
        if (jugador != null) {
            Disparo d = jugador.getDisparo();
            if (d.isActivo()) {
                g.setColor(Color.YELLOW);
                g.fillRect(
                    d.getX() * CELL_SIZE,
                    d.getY() * CELL_SIZE,
                    CELL_SIZE, CELL_SIZE
                );
            }
        }

        // Mensajes de fin de partida
        if (espacio.isGameWon()) {
            dibujarMensaje(g, "¡HAS GANADO!", Color.GREEN);
        } else if (espacio.isGameOver()) {
            dibujarMensaje(g, "GAME OVER", Color.RED);
        }
    }

    private void dibujarMensaje(Graphics g, String mensaje, Color color) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, espacio.getAltura() * CELL_SIZE / 2 - 30,
                   espacio.getAnchura() * CELL_SIZE, 60);
        g.setColor(color);
        g.setFont(new Font("Monospaced", Font.BOLD, 32));
        FontMetrics fm = g.getFontMetrics();
        int tx = (espacio.getAnchura() * CELL_SIZE - fm.stringWidth(mensaje)) / 2;
        int ty = espacio.getAltura() * CELL_SIZE / 2 + fm.getAscent() / 2;
        g.drawString(mensaje, tx, ty);
    }

    private void dibujarNave(Graphics g, int x, int y) {
        g.setColor(Color.MAGENTA);
        
        g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);

    }

    @Override
    public void update(Observable o, Object arg) {
        gamePanel.repaint();
    }

    private class Controller implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            if (espacio.isGameOver() || espacio.isGameWon()) return;

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:  espacio.moverJugador(-1,  0); break;
                case KeyEvent.VK_RIGHT: espacio.moverJugador( 1,  0); break;
                case KeyEvent.VK_UP:    espacio.moverJugador( 0, -1); break;
                case KeyEvent.VK_DOWN:  espacio.moverJugador( 0,  1); break;
                case KeyEvent.VK_SPACE: espacio.disparar();           break;
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


