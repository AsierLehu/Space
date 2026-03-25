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
        setResizable(false); // así no se puede redimensionar la ventana

        initPanel();

        pack(); // ajusta el tamaño de la ventana al contenido, si no la ponemos, no se abre bien
        setLocationRelativeTo(null); // centra la ventana en la pantalla
        setVisible(true);
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
        pack(); // ajusta el tamaño de la ventana al contenido, si no la ponemos, no se abre bien
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
    		
    		case 5: // colision - [tipo, disparoX, disparoY, enemigoX, enemigoY, ]
    			celdas[datos[1]][datos[2]].setBackground(COLOR_FONDO); // borrar disparo
    			celdas[datos[3]][datos[4]].setBackground(COLOR_FONDO); // borrar enemigo
    			break;
    			
    		case 6: // inicialización del juego - [tipo, jugadorX, jugadorY, enemigoX, enemigoY]
    			celdas[datos[1]][datos[2]].setBackground(COLOR_JUGADOR); // pintar jugador
    			celdas[datos[3]][datos[4]].setBackground(COLOR_ENEMIGO); // pintar enemigo
    			break;
    			
    		case 7: //mostrarMensajeFin("GAME OVER",   Color.RED);   break;
            mostrarGameOver();
                break;
    		case 8: //mostrarMensajeFin("HAS GANADO!", Color.GREEN); break;
            mostrarGameWon();
                break;
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



private void mostrarGameOver() {
        // --- PALABRA: GAME ---

// Letra G
celdas[30][25].setBackground(Color.RED); celdas[31][25].setBackground(Color.RED); celdas[32][25].setBackground(Color.RED);
celdas[30][26].setBackground(Color.RED);
celdas[30][27].setBackground(Color.RED); celdas[32][27].setBackground(Color.RED);
celdas[30][28].setBackground(Color.RED); celdas[32][28].setBackground(Color.RED);
celdas[30][29].setBackground(Color.RED); celdas[31][29].setBackground(Color.RED); celdas[32][29].setBackground(Color.RED);

// Letra A
celdas[35][25].setBackground(Color.RED); celdas[36][25].setBackground(Color.RED); celdas[37][25].setBackground(Color.RED);
celdas[35][26].setBackground(Color.RED); celdas[37][26].setBackground(Color.RED);
celdas[35][27].setBackground(Color.RED); celdas[36][27].setBackground(Color.RED); celdas[37][27].setBackground(Color.RED);
celdas[35][28].setBackground(Color.RED); celdas[37][28].setBackground(Color.RED);
celdas[35][29].setBackground(Color.RED); celdas[37][29].setBackground(Color.RED);

// Letra M
celdas[40][25].setBackground(Color.RED); celdas[44][25].setBackground(Color.RED);
celdas[40][26].setBackground(Color.RED); celdas[41][26].setBackground(Color.RED); celdas[43][26].setBackground(Color.RED); celdas[44][26].setBackground(Color.RED);
celdas[40][27].setBackground(Color.RED); celdas[42][27].setBackground(Color.RED); celdas[44][27].setBackground(Color.RED);
celdas[40][28].setBackground(Color.RED); celdas[44][28].setBackground(Color.RED);
celdas[40][29].setBackground(Color.RED); celdas[44][29].setBackground(Color.RED);

// Letra E
celdas[47][25].setBackground(Color.RED); celdas[48][25].setBackground(Color.RED); celdas[49][25].setBackground(Color.RED);
celdas[47][26].setBackground(Color.RED);
celdas[47][27].setBackground(Color.RED); celdas[48][27].setBackground(Color.RED);
celdas[47][28].setBackground(Color.RED);
celdas[47][29].setBackground(Color.RED); celdas[48][29].setBackground(Color.RED); celdas[49][29].setBackground(Color.RED);

// --- PALABRA: OVER ---

// Letra O
celdas[55][25].setBackground(Color.RED); celdas[56][25].setBackground(Color.RED); celdas[57][25].setBackground(Color.RED);
celdas[55][26].setBackground(Color.RED); celdas[57][26].setBackground(Color.RED);
celdas[55][27].setBackground(Color.RED); celdas[57][27].setBackground(Color.RED);
celdas[55][28].setBackground(Color.RED); celdas[57][28].setBackground(Color.RED);
celdas[55][29].setBackground(Color.RED); celdas[56][29].setBackground(Color.RED); celdas[57][29].setBackground(Color.RED);

// Letra V
celdas[60][25].setBackground(Color.RED); celdas[62][25].setBackground(Color.RED);
celdas[60][26].setBackground(Color.RED); celdas[62][26].setBackground(Color.RED);
celdas[60][27].setBackground(Color.RED); celdas[62][27].setBackground(Color.RED);
celdas[60][28].setBackground(Color.RED); celdas[62][28].setBackground(Color.RED);
celdas[61][29].setBackground(Color.RED);

// Letra E
celdas[65][25].setBackground(Color.RED); celdas[66][25].setBackground(Color.RED); celdas[67][25].setBackground(Color.RED);
celdas[65][26].setBackground(Color.RED);
celdas[65][27].setBackground(Color.RED); celdas[66][27].setBackground(Color.RED);
celdas[65][28].setBackground(Color.RED);
celdas[65][29].setBackground(Color.RED); celdas[66][29].setBackground(Color.RED); celdas[67][29].setBackground(Color.RED);

// Letra R
celdas[70][25].setBackground(Color.RED); celdas[71][25].setBackground(Color.RED);
celdas[70][26].setBackground(Color.RED); celdas[72][26].setBackground(Color.RED);
celdas[70][27].setBackground(Color.RED); celdas[71][27].setBackground(Color.RED);
celdas[70][28].setBackground(Color.RED); celdas[72][28].setBackground(Color.RED);
celdas[70][29].setBackground(Color.RED); celdas[72][29].setBackground(Color.RED);
        
        
        
        }
private void mostrarGameWon() {
    // --- PALABRA: GAME ---

// Letra G
celdas[30][25].setBackground(Color.GREEN); celdas[31][25].setBackground(Color.GREEN); celdas[32][25].setBackground(Color.GREEN);
celdas[30][26].setBackground(Color.GREEN);
celdas[30][27].setBackground(Color.GREEN); celdas[32][27].setBackground(Color.GREEN);
celdas[30][28].setBackground(Color.GREEN); celdas[32][28].setBackground(Color.GREEN);
celdas[30][29].setBackground(Color.GREEN); celdas[31][29].setBackground(Color.GREEN); celdas[32][29].setBackground(Color.GREEN);

// Letra A
celdas[35][25].setBackground(Color.GREEN); celdas[36][25].setBackground(Color.GREEN); celdas[37][25].setBackground(Color.GREEN);
celdas[35][26].setBackground(Color.GREEN); celdas[37][26].setBackground(Color.GREEN);
celdas[35][27].setBackground(Color.GREEN); celdas[36][27].setBackground(Color.GREEN); celdas[37][27].setBackground(Color.GREEN);
celdas[35][28].setBackground(Color.GREEN); celdas[37][28].setBackground(Color.GREEN);
celdas[35][29].setBackground(Color.GREEN); celdas[37][29].setBackground(Color.GREEN);

// Letra M
celdas[40][25].setBackground(Color.GREEN); celdas[44][25].setBackground(Color.GREEN);
celdas[40][26].setBackground(Color.GREEN); celdas[41][26].setBackground(Color.GREEN); celdas[43][26].setBackground(Color.GREEN); celdas[44][26].setBackground(Color.GREEN);
celdas[40][27].setBackground(Color.GREEN); celdas[42][27].setBackground(Color.GREEN); celdas[44][27].setBackground(Color.GREEN);
celdas[40][28].setBackground(Color.GREEN); celdas[44][28].setBackground(Color.GREEN);
celdas[40][29].setBackground(Color.GREEN); celdas[44][29].setBackground(Color.GREEN);

// Letra E
celdas[47][25].setBackground(Color.GREEN); celdas[48][25].setBackground(Color.GREEN); celdas[49][25].setBackground(Color.GREEN);
celdas[47][26].setBackground(Color.GREEN);
celdas[47][27].setBackground(Color.GREEN); celdas[48][27].setBackground(Color.GREEN);
celdas[47][28].setBackground(Color.GREEN);
celdas[47][29].setBackground(Color.GREEN); celdas[48][29].setBackground(Color.GREEN); celdas[49][29].setBackground(Color.GREEN);

// --- PALABRA: WON ---

// Letra W
celdas[55][25].setBackground(Color.GREEN); celdas[59][25].setBackground(Color.GREEN);
celdas[55][26].setBackground(Color.GREEN); celdas[59][26].setBackground(Color.GREEN);
celdas[55][27].setBackground(Color.GREEN); celdas[57][27].setBackground(Color.GREEN); celdas[59][27].setBackground(Color.GREEN);
celdas[55][28].setBackground(Color.GREEN); celdas[56][28].setBackground(Color.GREEN); celdas[58][28].setBackground(Color.GREEN); celdas[59][28].setBackground(Color.GREEN);
celdas[55][29].setBackground(Color.GREEN); celdas[59][29].setBackground(Color.GREEN);

// Letra O
celdas[62][25].setBackground(Color.GREEN); celdas[63][25].setBackground(Color.GREEN); celdas[64][25].setBackground(Color.GREEN);
celdas[62][26].setBackground(Color.GREEN); celdas[64][26].setBackground(Color.GREEN);
celdas[62][27].setBackground(Color.GREEN); celdas[64][27].setBackground(Color.GREEN);
celdas[62][28].setBackground(Color.GREEN); celdas[64][28].setBackground(Color.GREEN);
celdas[62][29].setBackground(Color.GREEN); celdas[63][29].setBackground(Color.GREEN); celdas[64][29].setBackground(Color.GREEN);

// Letra N
celdas[67][25].setBackground(Color.GREEN); celdas[71][25].setBackground(Color.GREEN);
celdas[67][26].setBackground(Color.GREEN); celdas[68][26].setBackground(Color.GREEN); celdas[71][26].setBackground(Color.GREEN);
celdas[67][27].setBackground(Color.GREEN); celdas[69][27].setBackground(Color.GREEN); celdas[71][27].setBackground(Color.GREEN);
celdas[67][28].setBackground(Color.GREEN); celdas[70][28].setBackground(Color.GREEN); celdas[71][28].setBackground(Color.GREEN);
celdas[67][29].setBackground(Color.GREEN); celdas[71][29].setBackground(Color.GREEN);}
}