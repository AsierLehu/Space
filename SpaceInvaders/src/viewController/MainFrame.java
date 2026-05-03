package viewController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.Observer;

import model.Espacio;
import model.JugadorBueno;

@SuppressWarnings("deprecation")
public class MainFrame extends JFrame implements Observer {

    private static Color COLOR_JUGADOR = Color.MAGENTA;
    private static Color COLOR_ENEMIGO = Color.RED;
    private static Color COLOR_DISPARO = Color.WHITE;
    
    // Colores específicos por tipo de nave
    private static Color COLOR_NAVE1_VERDE  = Color.GREEN;
    private static Color COLOR_NAVE2_AZUL   = Color.BLUE;
    private static Color COLOR_NAVE3_MORADO = Color.MAGENTA;

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
        int ancho = cols * 10; // 1000px
        int alto  = rows * 10; // 600px

        celdas = new JLabel[cols][rows];

        // --- Capa 1: imagen de fondo escalada al tamaño del panel ---
        java.net.URL urlImagen = getClass().getResource("/images/fondo2.png");
        Image imgEscalada = new ImageIcon(urlImagen).getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        JLabel fondoLabel = new JLabel(new ImageIcon(imgEscalada));
        fondoLabel.setBounds(0, 0, ancho, alto);

        // --- Capa 2: grid de celdas transparente encima ---
        JPanel gridPanel = new JPanel(new GridLayout(rows, cols, 0, 0));
        gridPanel.setOpaque(false);
        gridPanel.setBounds(0, 0, ancho, alto);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                JLabel lbl = new JLabel();
                lbl.setPreferredSize(new Dimension(10, 10));
                lbl.setOpaque(false);
                celdas[x][y] = lbl;
                gridPanel.add(lbl);
            }
        }

        // --- JLayeredPane: apila fondoLabel debajo y gridPanel encima ---
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(ancho, alto));
        layeredPane.add(fondoLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(gridPanel,  JLayeredPane.PALETTE_LAYER);

        addKeyListener(new Controller());
        add(layeredPane);
    }
    
    private void borrarCelda(int x, int y) {
        if (esValido(x, y)) {
            celdas[x][y].setOpaque(false);
            celdas[x][y].repaint();
        }
    }
    
    private void pintarCelda(int x, int y, Color c) {
        if (esValido(x, y)) {
            celdas[x][y].setOpaque(true);
            celdas[x][y].setBackground(c);
            celdas[x][y].repaint();
        }
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
    			borrarCelda(datos[1], datos[2]);
    			pintarCelda(datos[3], datos[4], COLOR_JUGADOR);
    			break;
        
    		case 1: // disparo nuevo - [tipo, newX, newY]
    			pintarCelda(datos[1], datos[2], COLOR_DISPARO);
    			break;
    			
    		case 2: // disparo se mueve - [tipo, oldX, oldY, newX, newY]
    			borrarCelda(datos[1], datos[2]);
    			pintarCelda(datos[3], datos[4], COLOR_DISPARO);
    			break;
    			
    		case 3: // disparo salio del tablero - [tipo, oldX, oldY]
    			borrarCelda(datos[1], datos[2]);
    			break;
    		
    		case 4: // enemigo baja - [tipo, oldX, oldY, newX, newY]
    			borrarCelda(datos[1], datos[2]);
    			pintarCelda(datos[3], datos[4], COLOR_ENEMIGO);
    			break;
    		
    		case 5: // colision - [tipo, disparoX, disparoY, enemigoX, enemigoY, ]
    			borrarCelda(datos[1], datos[2]);
    			borrarCelda(datos[3], datos[4]);
    			break;
    			
    		case 6: // inicialización del juego - [tipo, jugadorX, jugadorY, enemigoX, enemigoY]
    			pintarCelda(datos[1], datos[2], COLOR_JUGADOR);
    			pintarCelda(datos[3], datos[4], COLOR_ENEMIGO);
    			break;
    		
    		case 12: // borrar píxel de enemigo - [tipo, x, y]
    			borrarCelda(datos[1], datos[2]);
    			break;
    		
    		case 13: // inicialización de nave - [tipo, x, y]
    			pintarCelda(datos[1], datos[2], COLOR_JUGADOR);
    			break;
    		
    		case 14: // inicialización de enemigo o pintar píxel de enemigo - [tipo, x, y]
    			pintarCelda(datos[1], datos[2], COLOR_ENEMIGO);
    			break;
    			
    		case 7: 
            System.out.println("Game Over");
            abrirFinalFrame(false);
                break;
    		case 8: 
            System.out.println("Victoria");
            abrirFinalFrame(true);
                break;
    		
    		case 10: // borrar celda de jugador - [tipo, x, y]
    			borrarCelda(datos[1], datos[2]);
    			break;
    		
    		case 15: // pintar nave verde (Nave1) - [tipo, x, y]
    			pintarCelda(datos[1], datos[2], COLOR_NAVE1_VERDE);
    			break;
    		
    		case 16: // pintar nave azul (Nave2) - [tipo, x, y]
    			pintarCelda(datos[1], datos[2], COLOR_NAVE2_AZUL);
    			break;
    		
    		case 17: // pintar nave morada (Nave3) - [tipo, x, y]
    			pintarCelda(datos[1], datos[2], COLOR_NAVE3_MORADO);
    			break;

            case 18: // eliminar enemigo de la flota (modelo); vista ya actualizada con tipos 3 y 12
                break;
    	}
    }
    
    private boolean esValido(int x, int y) {
    	return x >= 0 && x < 100 && y >= 0 && y < 60;
    }
    
    // ==================== TRANSICIÓN DE PANTALLA ====================
    
    private void abrirFinalFrame(boolean victoria) {
        Espacio.getEspacio().deleteObserver(this);
        this.setVisible(false);
        new FinalFrame(victoria);
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



}