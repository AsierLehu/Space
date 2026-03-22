package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import javax.swing.Timer;

@SuppressWarnings("deprecation")
public class Espacio extends Observable {

    // ─── Singleton ────────────────────────────────────────────────────────────
    private static Espacio miEspacio;

    // ─── Estado del mundo ─────────────────────────────────────────────────────
    private FlotaEnemigos flotaEnemigos;
    private Jugador jugador;
    private static int anchura = 100;
    private static int altura  = 60;

    // Timer del juego
    private Timer gameTimer;
    private int frameCount;

    // ─── Constructor / Singleton ──────────────────────────────────────────────

    private Espacio() {
        this.flotaEnemigos = new FlotaEnemigos();
    }

    public static Espacio getEspacio() {
        if (miEspacio == null) {
            miEspacio = new Espacio();
        }
        return miEspacio;
    }

    // ─── Inicio de partida ────────────────────────────────────────────────────

    public void cambiarAMain() {
        inicializar();
        iniciarJuegoLoop();
        notificarCambioPantalla();
        notificarInicializacion();
    }

    private void inicializar() {
        jugador = new Jugador(50, 55);
        flotaEnemigos.inicializar(anchura);
    }

    // ─── Consultas públicas ───────────────────────────────────────────────────

    public Jugador getJugador() {
        return jugador;
    }

    public ArrayList<Enemigo> getEnemigos() {
        return flotaEnemigos.getEnemigos();
    }

    public int getAnchura() { return anchura; }
    public int getAltura()  { return altura;  }

    // ─── Estado del juego ─────────────────────────────────────────────────────

    // Derrota: jugador muerto, algún enemigo llegó al límite inferior, o colisión jugador-enemigo
    public boolean isGameOver() {
        if (jugador == null || !jugador.isVivo()) return true;
        if (flotaEnemigos.algunoLlegoAbajo(altura)) return true;
        return hayColisionJugadorEnemigo();
    }
    
    // Verifica si el jugador ha colisionado directamente con algún enemigo
    private boolean hayColisionJugadorEnemigo() {
        if (jugador == null || !jugador.isVivo()) return false;
        
        for (Enemigo enemigo : flotaEnemigos.getEnemigos()) {
            if (enemigo.isVivo() && 
                jugador.getX() == enemigo.getX() && 
                jugador.getY() == enemigo.getY()) {
                return true;
            }
        }
        return false;
    }

    // Victoria: la flota existe y todos los enemigos han sido destruidos
    public boolean isGameWon() {
        return flotaEnemigos.todosDestruidos();
    }

    // ─── Acciones del jugador ─────────────────────────────────────────────────

    public void moverJugador(int dx, int dy) {
        if (jugador != null && jugador.isVivo() && !isGameOver() && !isGameWon()) {
            int oldX = jugador.getX();
            int oldY = jugador.getY();
            jugador.mover(dx, dy);
            notificarMovimientoJugador(oldX, oldY);
        }
        if (isGameOver()) {
            notificarGameOver();
        }
    }

    public void disparar() {
        if (jugador != null && jugador.isVivo() && !isGameOver() && !isGameWon()) {
            jugador.disparar();
            notificarDisparoNuevo(jugador.getDisparo());
        }
    }

    // ─── Actualización del disparo ────────────────────────────────────────────

    // Llamado cada 50 ms: mueve el disparo y comprueba colisiones
    public void actualizarDisparo() {
        if (jugador == null) return;

        Disparo d = jugador.getDisparo();
        if (!d.isActivo()) return;

        int oldX = d.getX();
        int oldY = d.getY();
        d.subir();

        if (!d.isActivo()) {
            notificarDisparoFueraDeTablero(oldX, oldY);
        } else {
            Enemigo golpeado = flotaEnemigos.comprobarColision(d);
            if (golpeado != null) {
                notificarColision(oldX, oldY, golpeado);
                if (isGameWon()) {
                    notificarVictoria();
                }
            } else {
                notificarMovimientoDisparo(oldX, oldY, d);
            }
        }
    }

    // ─── Actualización de enemigos ────────────────────────────────────────────

    // Llamado cada 200 ms: baja los enemigos 1 píxel
    public void actualizarEnemigos() {
        ArrayList<Enemigo> enemigos = flotaEnemigos.getEnemigos();
        Disparo d = jugador.getDisparo();

        for (Enemigo e : enemigos) {
            if (e.isVivo()) {
                int oldX = e.getX();
                int oldY = e.getY();
                e.mover(0, 1);
                if (d != null && d.isActivo()) {
                    flotaEnemigos.comprobarColision(d);
                }
                notificarMovimientoEnemigo(oldX, oldY, e);
            }
        }

        if (isGameOver()) {
            notificarGameOver();
        }
    }

    // ─── Notificaciones ───────────────────────────────────────────────────────

    private void notificarCambioPantalla() {
        setChanged();
        notifyObservers(new int[] {9});
    }

    private void notificarInicializacion() {
        ArrayList<Enemigo> enemigos = flotaEnemigos.getEnemigos();
        setChanged();
        notifyObservers(new int[] {
            6,
            jugador.getX(), jugador.getY(),
            enemigos.get(0).getX(), enemigos.get(0).getY()
        });
    }

    private void notificarMovimientoJugador(int oldX, int oldY) {
        setChanged();
        notifyObservers(new int[] {0, oldX, oldY, jugador.getX(), jugador.getY()});
    }

    private void notificarDisparoNuevo(Disparo d) {
        setChanged();
        notifyObservers(new int[] {1, d.getX(), d.getY()});
    }

    private void notificarMovimientoDisparo(int oldX, int oldY, Disparo d) {
        setChanged();
        notifyObservers(new int[] {2, oldX, oldY, d.getX(), d.getY()});
    }

    private void notificarDisparoFueraDeTablero(int oldX, int oldY) {
        setChanged();
        notifyObservers(new int[] {3, oldX, oldY});
    }

    private void notificarMovimientoEnemigo(int oldX, int oldY, Enemigo e) {
        setChanged();
        notifyObservers(new int[] {4, oldX, oldY, e.getX(), e.getY()});
    }

    private void notificarColision(int disparoOldX, int disparoOldY, Enemigo golpeado) {
        setChanged();
        notifyObservers(new int[] {5, disparoOldX, disparoOldY + 1, golpeado.getX(), golpeado.getY()});
    }

    private void notificarGameOver() {
        setChanged();
        notifyObservers(new int[] {7});
    }

    private void notificarVictoria() {
        setChanged();
        notifyObservers(new int[] {8});
    }

    // ─── Bucle principal ──────────────────────────────────────────────────────

    // Tick cada 50 ms para disparos, cada 200 ms (4 ticks) para enemigos
    public void iniciarJuegoLoop() {
        frameCount = 0;
        gameTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isGameOver() && !isGameWon()) {
                    actualizarDisparo();
                    frameCount++;
                    if (frameCount % 4 == 0) {
                        actualizarEnemigos();
                    }
                }
            }
        });
        gameTimer.start();
    }
}
