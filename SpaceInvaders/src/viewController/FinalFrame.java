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
public class FinalFrame extends JFrame implements Observer {

    private boolean esVictoria;

	/**
	 * Create the frame.
	 */
    public FinalFrame(boolean victoria) {
        this.esVictoria = victoria;
        
        setTitle("Space Invaders - Fin del Juego");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = crearPanelPrincipal();
        add(panel);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        setFocusable(true);
        requestFocusInWindow();

        Controller controller = new Controller(); 
        addKeyListener(controller); 
        
        Espacio.getEspacio().addObserver(this);
    }

    // ==================== MÉTODOS GRÁFICOS ====================

    private JPanel crearPanelPrincipal() {
        JPanel panel = crearPanelConFondo();
        panel.setOpaque(true);
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(900, 700));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(100, 20, 100, 20);

        // Título del mensaje de fin
        JLabel titulo = crearTituloMensaje();
        gbc.gridy = 0;
        panel.add(titulo, gbc);

        // Instrucción para volver
        JLabel instruccion = crearInstruccion();
        gbc.gridy = 1;
        gbc.insets = new Insets(50, 20, 50, 20);
        panel.add(instruccion, gbc);

        return panel;
    }

    private JPanel crearPanelConFondo() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            private java.util.Random random = new java.util.Random(12345);
            private int[][] estrellas = null;
            
            private void generarEstrellas() {
                if (estrellas == null) {
                    estrellas = new int[150][2];
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
                
                // Fondo con degradado vertical
                GradientPaint gradient = new GradientPaint(0, 0, new Color(10, 10, 30), 
                                                          0, getHeight(), new Color(0, 0, 0));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Generar estrellas
                generarEstrellas();
                
                // Dibujar estrellas
                for (int[] estrella : estrellas) {
                    int tamanio = random.nextInt(3) + 1;
                    float brillo = 0.3f + (random.nextFloat() * 0.7f);
                    g2d.setColor(new Color(brillo, brillo, Math.min(brillo + 0.2f, 1.0f), 0.9f));
                    g2d.fillOval(estrella[0], estrella[1], tamanio, tamanio);
                }
                
                // Líneas decorativas
                g2d.setColor(new Color(0, 100, 100, 15));
                g2d.setStroke(new BasicStroke(1));
                for (int i = 0; i < getHeight(); i += 40) {
                    g2d.drawLine(0, i, getWidth(), i);
                }
            }
        };
        return panel;
    }

    private JLabel crearTituloMensaje() {
        String texto = esVictoria ? "VICTORIA" : "GAME OVER";
        Color color = esVictoria ? new Color(0, 255, 100) : new Color(255, 102, 102);
        
        JLabel titulo = new JLabel(texto);
        titulo.setForeground(color);
        titulo.setFont(new Font("Monospaced", Font.BOLD, 72));
        return titulo;
    }

    private JLabel crearInstruccion() {
        JLabel instruccion = new JLabel(">> PULSA SPACE PARA VOLVER <<");
        instruccion.setForeground(new Color(0, 255, 150));
        instruccion.setFont(new Font("Monospaced", Font.BOLD, 26));
        return instruccion;
    }

    // ==================== MÉTODOS DE LÓGICA ====================

    private void volverAlMenu() {
        Espacio.getEspacio().deleteObserver(this);
        this.setVisible(false);
        new StartFrame();
    }

    // ==================== OBSERVER ====================

	@Override
	public void update(Observable o, Object arg) {
		// No se necesita procesar notificaciones
	}

    // ==================== CONTROLADOR ====================

    /**
     * Controlador para gestionar la interacción del usuario
     * Clase privada interna según patrón MVC
     */
    private class Controller implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                volverAlMenu();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }
    }
}
