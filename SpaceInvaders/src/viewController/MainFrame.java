package viewController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.Observer;

import model.Espacio;

@SuppressWarnings("deprecation")
public class MainFrame extends JFrame implements Observer {

    private static final Color COLOR_FONDO   = new Color(20, 20, 20);
    private static final Color COLOR_JUGADOR = Color.MAGENTA;
    private static final Color COLOR_ENEMIGO = Color.RED;
    private static final Color COLOR_DISPARO = Color.YELLOW;

    private JLabel[][] celdas;
    private JLabel mensajeFin;

    public MainFrame() {
        Espacio.getEspacio().addObserver(this);

        setTitle("Space Invaders - Juego");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initPanel();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        requestFocusInWindow();
    }

    private void initPanel() {
        int cols = 100;
        int rows = 60;

        celdas = new JLabel[cols][rows];

        JPanel gamePanel = new JPanel(new GridLayout(rows, cols, 0, 0));
        gamePanel.setBackground(COLOR_FONDO);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                JLabel lbl = new JLabel();
                lbl.setPreferredSize(new Dimension(10,10));
                lbl.setOpaque(true);
                lbl.setBackground(COLOR_FONDO);
                celdas[x][y] = lbl;
                gamePanel.add(lbl);
            }
        }
        
        addKeyListener(new Controller());
        add(gamePanel);
    }

    private void mostrarMensajeFin(String texto, Color color) {
        mensajeFin = new JLabel(texto, SwingConstants.CENTER);
        mensajeFin.setFont(new Font("Monospaced", Font.BOLD, 28));
        mensajeFin.setOpaque(true);
        mensajeFin.setBackground(Color.BLACK);
        mensajeFin.setForeground(color);
        mensajeFin.setPreferredSize(new Dimension(100 * 10, 40));
        add(mensajeFin, BorderLayout.SOUTH);
        revalidate();
        pack();
    }

    @Override
    public void update(Observable o, Object arg) {
    	int[] datos = (int[]) arg;
    	procesarNotificacion(datos);
    }

    private void procesarNotificacion(int[] datos) {
    	int tipo = datos[0];
    	
    	switch (tipo) {
    		case 0: // jugador se mueve - [tipo, oldX, oldY, newX, newY]
    			celdas[datos[1]][datos[2]].setBackground(COLOR_FONDO); // borrar posición anterior
    			celdas[datos[3]][datos[4]].setBackground(COLOR_JUGADOR); // pintar nueva posición
    			break;
        
    		case 1: // disparo nuevo - [tipo, newX, newY]
    			celdas[datos[1]][datos[2]].setBackground(COLOR_DISPARO);
    			break;
    			
    		case 2: // disparo se mueve - [tipo, oldX, oldY, newX, newY]
    			celdas[datos[1]][datos[2]].setBackground(COLOR_FONDO); // borrar posición anterior
    			celdas[datos[3]][datos[4]].setBackground(COLOR_DISPARO); // pintar nueva posición
    			break;
    			
    		case 3: // disparo salio del tablero - [tipo, oldX, oldY]
    			celdas[datos[1]][datos[2]].setBackground(COLOR_FONDO); // borrar disparo
    			break;
    		
    		case 4: // enemigo baja - [tipo, oldX, oldY, newX, newY]
    			celdas[datos[1]][datos[2]].setBackground(COLOR_FONDO); // borrar posición anterior
    			celdas[datos[3]][datos[4]].setBackground(COLOR_ENEMIGO); // pintar nueva posición
    			break;
    		
    		case 5: // colision - [tipo, disparoX, disparoY, enemigoX, enemigoY]
    			celdas[datos[1]][datos[2]].setBackground(COLOR_FONDO); // borrar disparo
    			celdas[datos[3]][datos[4]].setBackground(COLOR_FONDO); // borrar enemigo
    			break;
    			
    		case 6: // inicialización del juego - [tipo, jugadorX, jugadorY, enemigoX, enemigoY]
    			celdas[datos[1]][datos[2]].setBackground(COLOR_JUGADOR); // pintar jugador
    			celdas[datos[3]][datos[4]].setBackground(COLOR_ENEMIGO); // pintar enemigo
    			break;
    			
    		case 7: mostrarMensajeFin("GAME OVER",   Color.RED);   break;
    		case 8: mostrarMensajeFin("HAS GANADO!", Color.GREEN); break;
    	}
    }
    
    private class Controller implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:  Espacio.getEspacio().moverJugador(-1,  0); break;
                case KeyEvent.VK_RIGHT: Espacio.getEspacio().moverJugador( 1,  0); break;
                case KeyEvent.VK_UP:    Espacio.getEspacio().moverJugador( 0, -1); break;
                case KeyEvent.VK_DOWN:  Espacio.getEspacio().moverJugador( 0,  1); break;
                case KeyEvent.VK_SPACE: Espacio.getEspacio().disparar();           break;
            }
        }

        @Override public void keyReleased(KeyEvent e) {}
        @Override public void keyTyped(KeyEvent e) {}
    }
}
