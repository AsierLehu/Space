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
import model.JugadorBueno;

@SuppressWarnings("deprecation")
public class StartFrame extends JFrame implements Observer {

    private JLabel etiquetaNave;

    /** Selección en pantalla de inicio; solo se copia a JugadorBueno al pulsar SPACE. */
    private String tipoNavePendiente = "Nave1";

	/**
	 * Create the frame.
	 */
    public StartFrame() {
        setTitle("Space Invaders");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Panel principal con degradado personalizado y cielo estrellado
        JPanel panel = new JPanel(new GridBagLayout()) {
            private java.util.Random random = new java.util.Random(12345); // Seed fija para consistencia
            private int[][] estrellas = null;
            
            private void generarEstrellas() {
                if (estrellas == null) {
                    estrellas = new int[150][2]; // 150 estrellas
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
                    int tamanio = random.nextInt(3) + 1; // Tamaño aleatorio entre 1-3
                    float brillo = 0.3f + (random.nextFloat() * 0.7f); // Brillo variable
                    g2d.setColor(new Color(brillo, brillo, Math.min(brillo + 0.2f, 1.0f), 0.9f));
                    g2d.fillOval(estrella[0], estrella[1], tamanio, tamanio);
                }
                
                // Líneas decorativas horizontales sutiles
                g2d.setColor(new Color(0, 100, 100, 15));
                g2d.setStroke(new BasicStroke(1));
                for (int i = 0; i < getHeight(); i += 40) {
                    g2d.drawLine(0, i, getWidth(), i);
                }
            }
        };
        panel.setOpaque(true);
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(900, 700));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(30, 20, 30, 20);

        // T�tulo grande y llamativo
        JLabel titulo = new JLabel("SPACE INVADERS");
        titulo.setForeground(new Color(0, 255, 100));
        titulo.setFont(new Font("Monospaced", Font.BOLD, 72));
        gbc.gridy = 0;
        panel.add(titulo, gbc);

        // Subt�tulo con estilo
        JLabel subtitulo = new JLabel("Sprint 2 - Battle Edition");
        subtitulo.setForeground(new Color(100, 200, 255));
        subtitulo.setFont(new Font("Monospaced", Font.ITALIC, 24));
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 20, 40, 20);
        panel.add(subtitulo, gbc);

        // Línea decorativa
        JLabel linea1 = new JLabel("==================================================");
        linea1.setForeground(new Color(0, 200, 200));
        linea1.setFont(new Font("Monospaced", Font.PLAIN, 16));
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 20, 20, 20);
        panel.add(linea1, gbc);

        // Selección de nave (1 / 2 / 3)
        etiquetaNave = new JLabel();
        etiquetaNave.setForeground(new Color(255, 200, 0));
        etiquetaNave.setFont(new Font("Monospaced", Font.BOLD, 20));
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 20, 20, 20);
        panel.add(etiquetaNave, gbc);
        actualizarTextoNave();

        // Instrucci�n para iniciar (pulsante)
        JLabel pressSpace = new JLabel(">> PULSA SPACE PARA JUGAR <<");
        pressSpace.setForeground(new Color(0, 255, 150));
        pressSpace.setFont(new Font("Monospaced", Font.BOLD, 26));
        gbc.gridy = 4;
        gbc.insets = new Insets(30, 20, 30, 20);
        panel.add(pressSpace, gbc);

        // Línea decorativa
        JLabel linea2 = new JLabel("==================================================");
        linea2.setForeground(new Color(0, 200, 200));
        linea2.setFont(new Font("Monospaced", Font.PLAIN, 16));
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 20, 30, 20);
        panel.add(linea2, gbc);

        // Controles (con mejor formato)
        JLabel control1 = new JLabel("1 / 2 / 3  ==  Tipo de nave");
        control1.setForeground(new Color(150, 150, 255));
        control1.setFont(new Font("Monospaced", Font.PLAIN, 16));
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 20, 10, 20);
        panel.add(control1, gbc);

        JLabel control2 = new JLabel("FLECHAS (^ v < >)  ==  Mover nave");
        control2.setForeground(new Color(150, 150, 255));
        control2.setFont(new Font("Monospaced", Font.PLAIN, 16));
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 20, 10, 20);
        panel.add(control2, gbc);

        JLabel control3 = new JLabel("SPACE  ==  Disparar");
        control3.setForeground(new Color(150, 150, 255));
        control3.setFont(new Font("Monospaced", Font.PLAIN, 16));
        gbc.gridy = 8;
        gbc.insets = new Insets(10, 20, 10, 20);
        panel.add(control3, gbc);

        JLabel control4 = new JLabel("M  ==  Cambiar tipo de disparo");
        control4.setForeground(new Color(150, 150, 255));
        control4.setFont(new Font("Monospaced", Font.PLAIN, 16));
        gbc.gridy = 9;
        gbc.insets = new Insets(10, 20, 20, 20);
        panel.add(control4, gbc);

        add(panel); // Añadir el panel principal al frame
        pack(); // Ajustar el tamaño del frame al contenido
        setLocationRelativeTo(null); // Centrar la ventana en la pantalla
        setVisible(true); // Hacer visible la ventana

        // Configurar el foco para capturar eventos de teclado
        setFocusable(true); // Permitir que el frame reciba el foco
        requestFocusInWindow(); // Solicitar el foco activamente

        Controller controller = new Controller(); 
        addKeyListener(controller); 
        
        Espacio.getEspacio().addObserver(this);
    }

    private void actualizarTextoNave() {
        etiquetaNave.setText("Nave elegida: " + tipoNavePendiente + "   (pulsa 1, 2 o 3 para cambiar)");
    }


	@Override
	public void update(Observable o, Object arg) {
		int[] datos = (int[]) arg;
        int tipo = datos[0];
	    if (tipo == 9) { // Notificación para cambiar de pantalla
	        Espacio.getEspacio().deleteObserver(this);
            this.setVisible(false);
            new MainFrame();
        }
    }

    /**
     * Controlador para gestionar la interacción del usuario con la pantalla de inicio
     * Clase privada interna según patrón MVC
     */
    private class Controller implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            int k = e.getKeyCode();
            if (k == KeyEvent.VK_1) {
                tipoNavePendiente = "Nave1";
                actualizarTextoNave();
                return;
            }
            if (k == KeyEvent.VK_2) {
                tipoNavePendiente = "Nave2";
                actualizarTextoNave();
                return;
            }
            if (k == KeyEvent.VK_3) {
                tipoNavePendiente = "Nave3";
                actualizarTextoNave();
                return;
            }
            if (k == KeyEvent.VK_SPACE) {
                JugadorBueno.getJugadorBueno().setTipoNaveElegido(tipoNavePendiente);
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
