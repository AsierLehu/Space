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

        Component panel = crearPanelPrincipal();
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

    private Component crearPanelPrincipal() {
        Component comp = crearPanelConFondoYEstrellas();
        if (comp instanceof JComponent) {
            ((JComponent) comp).setPreferredSize(new Dimension(900, 700));
        }
        return comp;
    }

    private Component crearPanelConFondoYEstrellas() {
        JPanel panelFondo = new JPanel(null) {
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
                
                // Líneas decorativas
                g2d.setColor(new Color(0, 100, 100, 15));
                g2d.setStroke(new BasicStroke(1));
                for (int i = 0; i < getHeight(); i += 40) {
                    g2d.drawLine(0, i, getWidth(), i);
                }
            }
        };
        panelFondo.setBackground(Color.BLACK);
        panelFondo.setOpaque(true);
        
        // Agregar estrellas como componentes
        java.util.Random random = new java.util.Random(12345);
        for (int i = 0; i < 150; i++) {
            int x = random.nextInt(900);
            int y = random.nextInt(700);
            int tamanio = random.nextInt(3) + 1;
            float brillo = 0.3f + (random.nextFloat() * 0.7f);
            
            JPanel estrella = new JPanel();
            estrella.setBackground(new Color(brillo, brillo, Math.min(brillo + 0.2f, 1.0f)));
            estrella.setBounds(x, y, tamanio, tamanio);
            panelFondo.add(estrella);
        }
        
        // Panel de contenido con GridBagLayout
        JPanel panelContenido = new JPanel(new GridBagLayout());
        panelContenido.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(100, 20, 100, 20);

        // Título del mensaje de fin
        JLabel titulo = crearTituloMensaje();
        gbc.gridy = 0;
        panelContenido.add(titulo, gbc);

        // Instrucción para volver
        JLabel instruccion = crearInstruccion();
        gbc.gridy = 1;
        gbc.insets = new Insets(50, 20, 50, 20);
        panelContenido.add(instruccion, gbc);
        
        // Usar JLayeredPane para superponer los paneles correctamente
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(900, 700));
        
        // Agregar panelFondo en la capa inferior
        layeredPane.add(panelFondo, JLayeredPane.DEFAULT_LAYER);
        panelFondo.setBounds(0, 0, 900, 700);
        
        // Agregar panelContenido en la capa superior
        layeredPane.add(panelContenido, JLayeredPane.PALETTE_LAYER);
        panelContenido.setBounds(0, 0, 900, 700);
        
        return layeredPane;
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
        Color color = esVictoria ? new Color(0, 255, 100) : new Color(255, 102, 102);
        instruccion.setForeground(color);
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
