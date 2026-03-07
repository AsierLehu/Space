package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Random;
import javax.swing.Timer;

@SuppressWarnings("deprecation")
public class Espacio extends Observable {
    private static Espacio miEspacio;
	private ArrayList<Naves> naves;
	private static int anchura = 100;
	private static int altura = 60;
	
	// Timer del juego - Lógica de negocio del modelo
	private Timer gameTimer;
	private int frameCount;
	
    private Espacio() {
        this.naves   = new ArrayList<>();
    }
    public static Espacio getEspacio() {
        if (miEspacio == null) {
            miEspacio = new Espacio();
        }
        return miEspacio;
    }
    public void cambiarAMain() {
    	inicializar();
    	iniciarJuegoLoop(); // Iniciar el timer del juego
    	setChanged();       
    	notifyObservers();   
    }
    private void inicializar() {
        // Crear jugador en posición inicial (50, 55)
        naves.add(new Jugador());

        // Crear 1 enemigo aleatorio en Sprint 1 (HU4: single-pixel enemy)
        Random rand = new Random();
        int ex = rand.nextInt(anchura);   // x aleatoria entre 0 y 99
        int ey = rand.nextInt(5);          // y en la parte superior (0-4)
        naves.add(new Enemigo(ex, ey));
    }

    public Jugador getJugador() {
        for (Naves n : naves) {
            if (n instanceof Jugador) return (Jugador) n;
        }
        return null;
    }

    public ArrayList<Enemigo> getEnemigos() {
    	ArrayList<Enemigo> enemigos = new ArrayList<>();
        for (Naves n : naves) {
            if (n instanceof Enemigo) enemigos.add((Enemigo) n);
        }
        return enemigos;
    }

    public ArrayList<Naves> getNaves() {
        return naves;
    }

    public void moverJugador(int dx, int dy) {
        Jugador j = getJugador();
        if (j != null && j.isVivo()) {
            j.mover(dx, dy);
            notificarVista();
        }
    }

    public void disparar() {
        Jugador j = getJugador();
        if (j != null && j.isVivo()) {
            j.disparar();
            notificarVista();
        }
    }

    // Llamado cada 50ms: mueve el disparo 1 píxel hacia arriba
    public void actualizarDisparo() {
        Jugador j = getJugador();
        if (j == null) return;

        Disparo d = j.getDisparo();
        if (d.isActivo()) {
            d.subir();
            comprobarColisiones(d);
            notificarVista();
        }
    }

    // Llamado cada 200ms: baja los enemigos 1 píxel
    public void actualizarEnemigos() {
        for (Enemigo e : getEnemigos()) {
            if (e.isVivo()) {
                e.mover();
            }
        }
        notificarVista();
    }

    private void comprobarColisiones(Disparo d) {
        for (Enemigo e : getEnemigos()) {
            if (e.isVivo() && d.getX() == e.getX() && d.getY() == e.getY()) {
                e.setVivo(false);
                d.setActivo(false);
                notificarVista(); // Notificar inmediatamente la colisión
            }
        }
    }

    // Derrota: un enemigo llega a la fila del jugador o más abajo
    public boolean isGameOver() {
        Jugador j = getJugador();
        if (j == null || !j.isVivo()) return true;

        for (Enemigo e : getEnemigos()) {
            if (e.isVivo() && e.getY() >= j.getY()) return true;
        }
        return false;
    }

    // Victoria: hay al menos un enemigo Y todos están eliminados
    public boolean isGameWon() {
    	ArrayList<Enemigo> enemigos = getEnemigos();
        if (enemigos.isEmpty()) return false;
        for (Enemigo e : enemigos) {
            if (e.isVivo()) return false;
        }
        return true;
    }


    public int getAnchura() { return anchura; }
    public int getAltura()  { return altura;  }

    /**
     * Inicia el bucle principal del juego (game loop) - Lógica de negocio
     * Tick cada 50ms para disparos, cada 200ms para enemigos
     */
    public void iniciarJuegoLoop() {
        frameCount = 0;
        // Tick cada 50ms (20 FPS)
        gameTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isGameOver() && !isGameWon()) {
                    actualizarDisparo();
                    frameCount++;
                    // Cada 4 ticks = 200ms: bajar enemigos
                    if (frameCount % 4 == 0) {
                        actualizarEnemigos();
                    }
                }
            }
        });
        gameTimer.start();
    }

    /**
     * Detiene el bucle principal del juego
     */
    public void detenerJuegoLoop() {
        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer = null;
        }
    }

    private void notificarVista() {
        setChanged();
        notifyObservers();
    }
}
