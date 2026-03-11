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
    private Espacio espacio;
    
    private int jugadorX;
    private int jugadorY;
    private int disparoX;
    private int disparoY;
    private boolean disparoPintado;

    public MainFrame() {
        espacio = Espacio.getEspacio();
        espacio.addObserver(this);

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
        int cols = espacio.getAnchura();
        int rows = espacio.getAltura();

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
        
        //Inicializamos variables y pintamos al jugador
        jugadorX = 50;
        jugadorY = 55;
        disparoX = 0;
        disparoY = 0;
        disparoPintado = false;
        celdas[jugadorX][jugadorY].setBackground(COLOR_JUGADOR);
        
        addKeyListener(new Controller());
        add(gamePanel);
    }

    @Override
    public void update(Observable o, Object arg) {
    	if (o instanceof Espacio) {
    		int[] datos = (int[]) arg;
            /// investigar esto de invokeLater
    		/*SwingUtilities.invokeLater(new Runnable() {
    			public void run() {
    				procesarNotificacion(datos);
    			}
    		});
    		*/
    		procesarNotificacion(datos);
    	}
    }

    private void procesarNotificacion(int[] datos) {
    	int tipo = datos[0];
    	
    	switch (tipo) {
    		case 0: // jugador se mueve
    			celdas[jugadorX][jugadorY].setBackground(COLOR_FONDO);
    			jugadorX = datos[1];
    			jugadorY = datos[2];
    			celdas[jugadorX][jugadorY].setBackground(COLOR_JUGADOR);
    			break;
        
    		case 1: // disparo se mueve
    			if (disparoPintado) {
    				celdas[disparoX][disparoY].setBackground(COLOR_FONDO);
    			}
    			disparoX = datos[1];
    			disparoY = datos[2];
    			celdas[disparoX][disparoY].setBackground(COLOR_DISPARO);
    			disparoPintado = true;
    			break;
    			
    		case 2: // disparo salio del tablero
    			if (disparoPintado ) {
    				celdas[disparoX][disparoY].setBackground(COLOR_FONDO);
    			}
    			disparoPintado = false;
    			break;
    		
    		case 3: // enemigo baja
    			celdas[datos[2]][datos[3]-1].setBackground(COLOR_FONDO);
    			celdas[datos[2]][datos[3]].setBackground(COLOR_ENEMIGO);
    			break;
    		
    		case 4: // colision: borra disparo y enemigo
    			if (disparoPintado) {
    				celdas[disparoX][disparoY].setBackground(COLOR_FONDO);
    			}
    			disparoPintado = false;
    			celdas[datos[1]][datos[2]].setBackground(COLOR_FONDO);
    			break;
    			
    		case 6: // game over
    			JOptionPane.showMessageDialog(MainFrame.this,
                        "GAME OVER", "Fin", JOptionPane.ERROR_MESSAGE);
                    break;
                    
    		case 7: // game won
    			JOptionPane.showMessageDialog(MainFrame.this,
                        "HAS GANADO!", "Fin", JOptionPane.INFORMATION_MESSAGE);
                    break;
    	}
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
