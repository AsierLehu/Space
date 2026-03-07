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
	private ArrayList<Enemigo> enemigos;
	private Jugador jugador;
	private int anchura = 100;
	private int altura = 60;
	
	// Timer del juego - Lógica de negocio del modelo
	private Timer gameTimer;
	private int frameCount;
	
    private Espacio() {
        this.enemigos = new ArrayList<>();
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
        jugador = new Jugador();

        Random rand = new Random();
        int ex = rand.nextInt(anchura);
        int ey = rand.nextInt(5);
        enemigos.add(new Enemigo(ex, ey));
    }

    public Jugador getJugador() {
        return jugador;
    }

    public ArrayList<Enemigo> getEnemigos() {
        return enemigos;
    }



    public void moverJugador(int dx, int dy) {
        if (jugador != null && jugador.isVivo()) {
            jugador.mover(dx, dy);
            notificarVista();
        }
    }

    public void disparar() {
        if (jugador != null && jugador.isVivo()) {
            jugador.disparar();
            notificarVista();
        }
    }

    // Llamado cada 50ms: mueve el disparo 1 píxel hacia arriba
    public void actualizarDisparo() {
        if (jugador == null) return;

        Disparo d = jugador.getDisparo();
        if (d.isActivo()) {
            d.subir();
            comprobarColisiones(d);
            notificarVista();
        }
    }

    // Llamado cada 200ms: baja los enemigos 1 píxel
    public void actualizarEnemigos() {
        Disparo d = jugador.getDisparo();
        for (Enemigo e : enemigos) {
            if (e.isVivo()) {
                e.mover(0, 1);
                if (d != null && d.isActivo()) comprobarColisiones(d);
            }
        }
        notificarVista();
    }

    private void comprobarColisiones(Disparo d) {
        for (Enemigo e : enemigos) {
            if (e.isVivo() && d.getX() == e.getX()
                    && d.getY() <= e.getY() && d.getY() >= e.getY() - 1) { // si es menor, debe cumplir que sea 1 pixel por debajo del enemigo. si es el mismo, cumple.
                e.setVivo(false);
                d.setActivo(false);
                notificarVista();
            }
        }
    }

    // Derrota: un enemigo llega a la fila del jugador o más abajo
    public boolean isGameOver() {
        if (jugador == null || !jugador.isVivo()) return true;

        for (Enemigo e : enemigos) {
            if (e.isVivo() && e.getY() >= jugador.getY()) return true;
        }
        return false;
    }

    // Victoria: hay al menos un enemigo Y todos están eliminados
    public boolean isGameWon() {
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
