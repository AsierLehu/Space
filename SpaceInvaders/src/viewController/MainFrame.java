package viewController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.Observer;

import model.Disparo;
import model.Enemigo;
import model.Espacio;
import model.Jugador;

@SuppressWarnings("deprecation")
public class MainFrame extends JFrame implements Observer {

    private static final Color COLOR_FONDO   = new Color(20, 20, 20);
    private static final Color COLOR_JUGADOR = Color.MAGENTA;
    private static final Color COLOR_ENEMIGO = Color.RED;
    private static final Color COLOR_DISPARO = Color.YELLOW;

    private JButton[][] celdas;
    private Espacio espacio;

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
        requestFocusInWindow();
    }

    private void initPanel() {
        int cols = espacio.getAnchura();
        int rows = espacio.getAltura();

        celdas = new JButton[cols][rows];

        JPanel gamePanel = new JPanel(new GridLayout(rows, cols, 0, 0));
        gamePanel.setBackground(COLOR_FONDO);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(10, 10));
                btn.setBackground(COLOR_FONDO);
                btn.setBorder(BorderFactory.createLineBorder(new Color(35, 35, 35), 1));
                btn.setFocusable(false);
                celdas[x][y] = btn;
                gamePanel.add(btn);
            }
        }

        addKeyListener(new Controller());
        add(gamePanel);
    }

    private void actualizarVista() {
        int cols = espacio.getAnchura();
        int rows = espacio.getAltura();

        // Resetear todas las celdas al color de fondo
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                celdas[x][y].setBackground(COLOR_FONDO);
            }
        }

        // Pintar jugador
        Jugador jugador = espacio.getJugador();
        if (jugador != null && jugador.isVivo()) {
            celdas[jugador.getX()][jugador.getY()].setBackground(COLOR_JUGADOR);
        }

        // Pintar enemigos
        for (Enemigo e : espacio.getEnemigos()) {
            if (e.isVivo()) {
                celdas[e.getX()][e.getY()].setBackground(COLOR_ENEMIGO);
            }
        }

        // Pintar disparo
        if (jugador != null) {
            Disparo d = jugador.getDisparo();
            if (d.isActivo()) {
                celdas[d.getX()][d.getY()].setBackground(COLOR_DISPARO);
            }
        }

        // Mensaje de fin de partida
        if (espacio.isGameWon()) {
            JOptionPane.showMessageDialog(this, "¡HAS GANADO!", "Fin", JOptionPane.INFORMATION_MESSAGE);
        } else if (espacio.isGameOver()) {
            JOptionPane.showMessageDialog(this, "GAME OVER", "Fin", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        SwingUtilities.invokeLater(this::actualizarVista);
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

        @Override public void keyReleased(KeyEvent e) {}
        @Override public void keyTyped(KeyEvent e) {}
    }
}
