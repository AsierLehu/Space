package viewController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.RenderingHints;
import java.util.Observable;
import java.util.Observer;

import model.Espacio;
import model.JugadorBueno;

@SuppressWarnings("deprecation")
public class MainFrame extends JFrame implements Observer {

    private static final Color COLOR_FONDO   = new Color(20, 20, 20);
    private static final Color COLOR_JUGADOR = Color.MAGENTA;
    private static final Color COLOR_ENEMIGO = Color.RED;
    private static final Color COLOR_DISPARO = Color.WHITE;
    private static final Color COLOR_GAME_OVER = new Color(255, 102, 102);
    
    // Colores específicos por tipo de nave
    private static final Color COLOR_NAVE1_VERDE = Color.GREEN;
    private static final Color COLOR_NAVE2_AZUL = Color.BLUE;
    private static final Color COLOR_NAVE3_MORADO = Color.MAGENTA; // Morado para Nave3

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

        JPanel gamePanel = new JPanel(new GridLayout(rows, cols, 0, 0)) {
            private java.util.Random random = new java.util.Random(42069);
            private int[][] estrellas = null;
            
            private void generarEstrellas() {
                if (estrellas == null) {
                    estrellas = new int[250][2];
                    for (int i = 0; i < estrellas.length; i++) {
                        estrellas[i][0] = random.nextInt(getWidth());
                        estrellas[i][1] = random.nextInt(getHeight());
                    }
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo
                g2d.setColor(COLOR_FONDO);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Estrellas
                generarEstrellas();
                for (int[] estrella : estrellas) {
                    float brillo = 0.6f + (random.nextFloat() * 0.4f);
                    g2d.setColor(new Color(brillo, brillo, Math.min(brillo + 0.2f, 1.0f)));
                    g2d.fillOval(estrella[0], estrella[1], 2, 2);
                }
            }
        };
        gamePanel.setBackground(COLOR_FONDO);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                JLabel lbl = new JLabel();
                lbl.setPreferredSize(new Dimension(10,10));
                lbl.setOpaque(false);
                lbl.setBackground(new Color(0, 0, 0, 0)); // Background transparente inicial
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
    			setCeldaColor(datos[1], datos[2], COLOR_FONDO);
    			setCeldaColor(datos[3], datos[4], COLOR_JUGADOR);
    			break;
        
    		case 1: // disparo nuevo - [tipo, newX, newY]
    			setCeldaColor(datos[1], datos[2], COLOR_DISPARO);
    			break;
    			
    		case 2: // disparo se mueve - [tipo, oldX, oldY, newX, newY]
    			setCeldaColor(datos[1], datos[2], COLOR_FONDO);
    			setCeldaColor(datos[3], datos[4], COLOR_DISPARO);
    			break;
    			
    		case 3: // disparo salio del tablero - [tipo, oldX, oldY]
    			setCeldaColor(datos[1], datos[2], COLOR_FONDO);
    			break;
    		
    		case 4: // enemigo baja - [tipo, oldX, oldY, newX, newY]
    			setCeldaColor(datos[1], datos[2], COLOR_FONDO);
    			setCeldaColor(datos[3], datos[4], COLOR_ENEMIGO);
    			break;
    		
    		case 5: // colision - [tipo, disparoX, disparoY, enemigoX, enemigoY, ]
    			setCeldaColor(datos[1], datos[2], COLOR_FONDO);
    			setCeldaColor(datos[3], datos[4], COLOR_FONDO);
    			break;
    			
    		case 6: // inicialización del juego - [tipo, jugadorX, jugadorY, enemigoX, enemigoY]
    			setCeldaColor(datos[1], datos[2], COLOR_JUGADOR);
    			setCeldaColor(datos[3], datos[4], COLOR_ENEMIGO);
    			break;
    		
    		case 12: // borrar píxel de enemigo - [tipo, x, y]
    			setCeldaColor(datos[1], datos[2], COLOR_FONDO);
    			break;
    		
    		case 13: // inicialización de nave - [tipo, x, y]
    			setCeldaColor(datos[1], datos[2], COLOR_JUGADOR);
    			break;
    		
    		case 14: // inicialización de enemigo o pintar píxel de enemigo - [tipo, x, y]
    			setCeldaColor(datos[1], datos[2], COLOR_ENEMIGO);
    			break;
    			
    		case 7: //mostrarMensajeFin("GAME OVER",   Color.RED);   break;
            mostrarGameOver();
                break;
    		case 8: //mostrarMensajeFin("HAS GANADO!", Color.GREEN); break;
            mostrarGameWon();
                break;
    		
    		case 10: // borrar celda de jugador - [tipo, x, y]
    			setCeldaColor(datos[1], datos[2], COLOR_FONDO);
    			break;
    		
    		case 15: // pintar nave verde (Nave1) - [tipo, x, y]
    			setCeldaColor(datos[1], datos[2], COLOR_NAVE1_VERDE);
    			break;
    		
    		case 16: // pintar nave azul (Nave2) - [tipo, x, y]
    			setCeldaColor(datos[1], datos[2], COLOR_NAVE2_AZUL);
    			break;
    		
    		case 17: // pintar nave morada (Nave3) - [tipo, x, y]
    			setCeldaColor(datos[1], datos[2], COLOR_NAVE3_MORADO);
    			break;
    	}
    }
    
    private boolean esValido(int x, int y) {
    	return x >= 0 && x < 100 && y >= 0 && y < 60;
    }
    
    private void setCeldaColor(int x, int y, Color color) {
        if (esValido(x, y)) {
            if (color == COLOR_FONDO) {
                celdas[x][y].setOpaque(false); // Transparente para ver las estrellas
                celdas[x][y].setBackground(new Color(0, 0, 0, 0)); // Background transparente
            } else {
                celdas[x][y].setOpaque(true);
                celdas[x][y].setBackground(color);
            }
        }
    }
    
    private void pintarCelda(int x, int y, Color color) {
        if (esValido(x, y)) {
            celdas[x][y].setOpaque(true);
            celdas[x][y].setBackground(color);
        }
    }
    
    private class Controller implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:  JugadorBueno.getJugadorBueno().mover(-1,  0); break;
                case KeyEvent.VK_RIGHT: JugadorBueno.getJugadorBueno().mover( 1,  0); break;
                case KeyEvent.VK_UP:    JugadorBueno.getJugadorBueno().mover( 0, -1); break;
                case KeyEvent.VK_DOWN:  JugadorBueno.getJugadorBueno().mover( 0,  1); break;
                case KeyEvent.VK_SPACE: JugadorBueno.getJugadorBueno().disparar();           break;
                case KeyEvent.VK_M:     JugadorBueno.getJugadorBueno().cambiarTipoDisparo(); break;
            }
        }

        @Override public void keyReleased(KeyEvent e) {}
        @Override public void keyTyped(KeyEvent e) {}
    }



private void mostrarGameOver() {
        // --- PALABRA: GAME ---
// Letra G
pintarCelda(30, 25, COLOR_GAME_OVER); pintarCelda(31, 25, COLOR_GAME_OVER); pintarCelda(32, 25, COLOR_GAME_OVER);
pintarCelda(30, 26, COLOR_GAME_OVER);
pintarCelda(30, 27, COLOR_GAME_OVER); pintarCelda(32, 27, COLOR_GAME_OVER);
pintarCelda(30, 28, COLOR_GAME_OVER); pintarCelda(32, 28, COLOR_GAME_OVER);
pintarCelda(30, 29, COLOR_GAME_OVER); pintarCelda(31, 29, COLOR_GAME_OVER); pintarCelda(32, 29, COLOR_GAME_OVER);

// Letra A
pintarCelda(35, 25, COLOR_GAME_OVER); pintarCelda(36, 25, COLOR_GAME_OVER); pintarCelda(37, 25, COLOR_GAME_OVER);
pintarCelda(35, 26, COLOR_GAME_OVER); pintarCelda(37, 26, COLOR_GAME_OVER);
pintarCelda(35, 27, COLOR_GAME_OVER); pintarCelda(36, 27, COLOR_GAME_OVER); pintarCelda(37, 27, COLOR_GAME_OVER);
pintarCelda(35, 28, COLOR_GAME_OVER); pintarCelda(37, 28, COLOR_GAME_OVER);
pintarCelda(35, 29, COLOR_GAME_OVER); pintarCelda(37, 29, COLOR_GAME_OVER);

// Letra M
pintarCelda(40, 25, COLOR_GAME_OVER); pintarCelda(44, 25, COLOR_GAME_OVER);
pintarCelda(40, 26, COLOR_GAME_OVER); pintarCelda(41, 26, COLOR_GAME_OVER); pintarCelda(43, 26, COLOR_GAME_OVER); pintarCelda(44, 26, COLOR_GAME_OVER);
pintarCelda(40, 27, COLOR_GAME_OVER); pintarCelda(42, 27, COLOR_GAME_OVER); pintarCelda(44, 27, COLOR_GAME_OVER);
pintarCelda(40, 28, COLOR_GAME_OVER); pintarCelda(44, 28, COLOR_GAME_OVER);
pintarCelda(40, 29, COLOR_GAME_OVER); pintarCelda(44, 29, COLOR_GAME_OVER);

// Letra E
pintarCelda(47, 25, COLOR_GAME_OVER); pintarCelda(48, 25, COLOR_GAME_OVER); pintarCelda(49, 25, COLOR_GAME_OVER);
pintarCelda(47, 26, COLOR_GAME_OVER);
pintarCelda(47, 27, COLOR_GAME_OVER); pintarCelda(48, 27, COLOR_GAME_OVER);
pintarCelda(47, 28, COLOR_GAME_OVER);
pintarCelda(47, 29, COLOR_GAME_OVER); pintarCelda(48, 29, COLOR_GAME_OVER); pintarCelda(49, 29, COLOR_GAME_OVER);

// --- PALABRA: OVER ---

// Letra O
pintarCelda(55, 25, COLOR_GAME_OVER); pintarCelda(56, 25, COLOR_GAME_OVER); pintarCelda(57, 25, COLOR_GAME_OVER);
pintarCelda(55, 26, COLOR_GAME_OVER); pintarCelda(57, 26, COLOR_GAME_OVER);
pintarCelda(55, 27, COLOR_GAME_OVER); pintarCelda(57, 27, COLOR_GAME_OVER);
pintarCelda(55, 28, COLOR_GAME_OVER); pintarCelda(57, 28, COLOR_GAME_OVER);
pintarCelda(55, 29, COLOR_GAME_OVER); pintarCelda(56, 29, COLOR_GAME_OVER); pintarCelda(57, 29, COLOR_GAME_OVER);

// Letra V
pintarCelda(60, 25, COLOR_GAME_OVER); pintarCelda(62, 25, COLOR_GAME_OVER);
pintarCelda(60, 26, COLOR_GAME_OVER); pintarCelda(62, 26, COLOR_GAME_OVER);
pintarCelda(60, 27, COLOR_GAME_OVER); pintarCelda(62, 27, COLOR_GAME_OVER);
pintarCelda(60, 28, COLOR_GAME_OVER); pintarCelda(62, 28, COLOR_GAME_OVER);
pintarCelda(61, 29, COLOR_GAME_OVER);

// Letra E
pintarCelda(65, 25, COLOR_GAME_OVER); pintarCelda(66, 25, COLOR_GAME_OVER); pintarCelda(67, 25, COLOR_GAME_OVER);
pintarCelda(65, 26, COLOR_GAME_OVER);
pintarCelda(65, 27, COLOR_GAME_OVER); pintarCelda(66, 27, COLOR_GAME_OVER);
pintarCelda(65, 28, COLOR_GAME_OVER);
pintarCelda(65, 29, COLOR_GAME_OVER); pintarCelda(66, 29, COLOR_GAME_OVER); pintarCelda(67, 29, COLOR_GAME_OVER);

// Letra R
pintarCelda(70, 25, COLOR_GAME_OVER); pintarCelda(71, 25, COLOR_GAME_OVER);
pintarCelda(70, 26, COLOR_GAME_OVER); pintarCelda(72, 26, COLOR_GAME_OVER);
pintarCelda(70, 27, COLOR_GAME_OVER); pintarCelda(71, 27, COLOR_GAME_OVER);
pintarCelda(70, 28, COLOR_GAME_OVER); pintarCelda(72, 28, COLOR_GAME_OVER);
pintarCelda(70, 29, COLOR_GAME_OVER); pintarCelda(72, 29, COLOR_GAME_OVER);
        }
private void mostrarGameWon() {
    // --- PALABRA: GAME ---

// Letra G
pintarCelda(30, 25, Color.GREEN); pintarCelda(31, 25, Color.GREEN); pintarCelda(32, 25, Color.GREEN);
pintarCelda(30, 26, Color.GREEN);
pintarCelda(30, 27, Color.GREEN); pintarCelda(32, 27, Color.GREEN);
pintarCelda(30, 28, Color.GREEN); pintarCelda(32, 28, Color.GREEN);
pintarCelda(30, 29, Color.GREEN); pintarCelda(31, 29, Color.GREEN); pintarCelda(32, 29, Color.GREEN);

// Letra A
pintarCelda(35, 25, Color.GREEN); pintarCelda(36, 25, Color.GREEN); pintarCelda(37, 25, Color.GREEN);
pintarCelda(35, 26, Color.GREEN); pintarCelda(37, 26, Color.GREEN);
pintarCelda(35, 27, Color.GREEN); pintarCelda(36, 27, Color.GREEN); pintarCelda(37, 27, Color.GREEN);
pintarCelda(35, 28, Color.GREEN); pintarCelda(37, 28, Color.GREEN);
pintarCelda(35, 29, Color.GREEN); pintarCelda(37, 29, Color.GREEN);

// Letra M
pintarCelda(40, 25, Color.GREEN); pintarCelda(44, 25, Color.GREEN);
pintarCelda(40, 26, Color.GREEN); pintarCelda(41, 26, Color.GREEN); pintarCelda(43, 26, Color.GREEN); pintarCelda(44, 26, Color.GREEN);
pintarCelda(40, 27, Color.GREEN); pintarCelda(42, 27, Color.GREEN); pintarCelda(44, 27, Color.GREEN);
pintarCelda(40, 28, Color.GREEN); pintarCelda(44, 28, Color.GREEN);
pintarCelda(40, 29, Color.GREEN); pintarCelda(44, 29, Color.GREEN);

// Letra E
pintarCelda(47, 25, Color.GREEN); pintarCelda(48, 25, Color.GREEN); pintarCelda(49, 25, Color.GREEN);
pintarCelda(47, 26, Color.GREEN);
pintarCelda(47, 27, Color.GREEN); pintarCelda(48, 27, Color.GREEN);
pintarCelda(47, 28, Color.GREEN);
pintarCelda(47, 29, Color.GREEN); pintarCelda(48, 29, Color.GREEN); pintarCelda(49, 29, Color.GREEN);

// --- PALABRA: WON ---

// Letra W
pintarCelda(55, 25, Color.GREEN); pintarCelda(59, 25, Color.GREEN);
pintarCelda(55, 26, Color.GREEN); pintarCelda(59, 26, Color.GREEN);
pintarCelda(55, 27, Color.GREEN); pintarCelda(57, 27, Color.GREEN); pintarCelda(59, 27, Color.GREEN);
pintarCelda(55, 28, Color.GREEN); pintarCelda(56, 28, Color.GREEN); pintarCelda(58, 28, Color.GREEN); pintarCelda(59, 28, Color.GREEN);
pintarCelda(55, 29, Color.GREEN); pintarCelda(59, 29, Color.GREEN);

// Letra O
pintarCelda(62, 25, Color.GREEN); pintarCelda(63, 25, Color.GREEN); pintarCelda(64, 25, Color.GREEN);
pintarCelda(62, 26, Color.GREEN); pintarCelda(64, 26, Color.GREEN);
pintarCelda(62, 27, Color.GREEN); pintarCelda(64, 27, Color.GREEN);
pintarCelda(62, 28, Color.GREEN); pintarCelda(64, 28, Color.GREEN);
pintarCelda(62, 29, Color.GREEN); pintarCelda(63, 29, Color.GREEN); pintarCelda(64, 29, Color.GREEN);

// Letra N
pintarCelda(67, 25, Color.GREEN); pintarCelda(71, 25, Color.GREEN);
pintarCelda(67, 26, Color.GREEN); pintarCelda(68, 26, Color.GREEN); pintarCelda(71, 26, Color.GREEN);
pintarCelda(67, 27, Color.GREEN); pintarCelda(69, 27, Color.GREEN); pintarCelda(71, 27, Color.GREEN);
pintarCelda(67, 28, Color.GREEN); pintarCelda(70, 28, Color.GREEN); pintarCelda(71, 28, Color.GREEN);
pintarCelda(67, 29, Color.GREEN); pintarCelda(71, 29, Color.GREEN);}
}