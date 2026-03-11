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
	private static int anchura = 100;
	private static int altura = 60;
	
	// Timer del juego
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
    	notifyObservers(new int[] {5});   
    }
    
    private void inicializar() {
        jugador = new Jugador(50, 55);

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
            
            setChanged();
            notifyObservers(new int[] {0, jugador.getX(), jugador.getY()});
        }
    }

    public void disparar() {
        if (jugador != null && jugador.isVivo()) {
            jugador.disparar();
            Disparo d = jugador.getDisparo();
            setChanged();
            notifyObservers(new int[] {1, d.getX(), d.getY()});
        }
    }

    // Llamado cada 50ms: mueve el disparo 1 p�xel hacia arriba
    public void actualizarDisparo() {
        if (jugador == null) return;

        Disparo d = jugador.getDisparo();
        if (d.isActivo()) {
            d.subir();
            if (!d.isActivo()) {
            	// El disparo sali� del tablero
            	setChanged();
            	notifyObservers(new int[] {2});
            }
            else {
            	boolean colision = comprobarColisiones(d);
            	if (!colision){
            		setChanged();
            		notifyObservers(new int[] {1, d.getX(), d.getY()});
            	}
            }
        }
    }

    // Llamado cada 200ms: baja los enemigos 1 píxel
    public void actualizarEnemigos() {
        Disparo d = jugador.getDisparo();
        for (int i = 0; i < enemigos.size(); i++) {
        	Enemigo e = enemigos.get(i);
            if (e.isVivo()) {
                e.mover(0, 1);
                if (d != null && d.isActivo()) {
                	comprobarColisiones(d);
                }
                // Notifica la nueva posición del enemigo junto a su índice en el array
                setChanged();
                notifyObservers(new int[] {3, i, e.getX(), e.getY()});
            }
        }
        if (isGameOver()) {
        	setChanged();
        	notifyObservers(new int[] {6});
        }
    }

    private boolean comprobarColisiones(Disparo d) {
        for (int i = 0; i < enemigos.size(); i++) {
        	Enemigo e = enemigos.get(i);
            if (e.isVivo() && d.getX() == e.getX()
                    && d.getY() <= e.getY() && d.getY() >= e.getY() - 1) { // ventana de 2 píxeles: evitamos el error de que no "choquen" 
                e.setVivo(false);
                d.setActivo(false);
                setChanged();
                notifyObservers(new int[] {4, e.getX(), e.getY()});
                if (isGameWon()) {
                	setChanged();
                	notifyObservers(new int[] {7});
                }
                return true;
            }
        }
        return false;
    }

    // Derrota: un enemigo llega a la fila del jugador o m�s abajo
    public boolean isGameOver() {
        if (jugador == null || !jugador.isVivo()) return true;

        for (Enemigo e : enemigos) {
            if (e.isVivo() && e.getY() >= jugador.getY()) return true;
        }
        return false;
    }

    // Victor�a: hay al menos un enemigo Y todos est�n eliminados
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
     * Inicia el bucle principal del juego (game loop)
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

}
