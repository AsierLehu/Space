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
    // La nave del jugador está en JugadorBueno#getNave()
    private static int anchura = 100;
    private static int altura  = 60;

    // Timer del juego
    private Timer gameTimer;
    private int frameCount;

    // ─── Constructor / Singleton ──────────────────────────────────────────────

    private Espacio() {
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
        JugadorBueno.getJugadorBueno().crearNaveParaPartida();
        FlotaEnemigos.getFlotaEnemigos().inicializar(anchura);
    }

    private Naves naveJugador() {
        return JugadorBueno.getJugadorBueno().getNave();
    }


    public int getAnchura() { return anchura; }
    public int getAltura()  { return altura;  }

    // ─── Estado del juego ─────────────────────────────────────────────────────

    // Derrota: jugador muerto, algún enemigo llegó al límite inferior, o colisión jugador-enemigo
    public boolean isGameOver() {
        Naves j = naveJugador();
        if (j == null || !j.isVivo()) return true;
        if (FlotaEnemigos.getFlotaEnemigos().algunoLlegoAbajo(altura)) return true;
        return hayColisionJugadorEnemigo();
    }
    
    // Verifica si el jugador ha colisionado directamente con algún enemigo
    private boolean hayColisionJugadorEnemigo() {
        Naves j = naveJugador();
        if (j == null || !j.isVivo()) return false;

        for (Enemigo enemigo : FlotaEnemigos.getFlotaEnemigos().getEnemigos()) {
            if (enemigo.isVivo()
                    && j.getX() == enemigo.getX()
                    && j.getY() == enemigo.getY()) {
                return true;
            }
        }
        return false;
    }

    // Victoria: la flota existe y todos los enemigos han sido destruidos
    public boolean isGameWon() {
        return FlotaEnemigos.getFlotaEnemigos().todosDestruidos();
    }

    // ─── Acciones del jugador ─────────────────────────────────────────────────

    public void moverJugador(int dx, int dy) {
        JugadorBueno.getJugadorBueno().mover(dx, dy);
    }

    /** Tras un intento de movimiento del jugador: notifica derrota si corresponde. */
    void trasIntentoMoverJugador() {
        if (isGameOver()) {
            notificarGameOver();
        }
    }

    public void cambiarTipoDisparo() {
        Naves j = naveJugador();
        if (j != null && j.isVivo()) {
            j.cambiarTipoDisparo();
        }
    }

    // ─── Actualización del disparo ────────────────────────────────────────────

    /**
     * Llamado cada 50 ms: mueve el disparo y comprueba colisiones.
     * 
     * Flujo:
     * 1. Valida que exista disparo activo
     * 2. Llama a {@link Disparo#subir()} que mueve el {@link ComponenteDisparo}
     * 3. ComponenteDisparo.mover() notifica automáticamente a través de 
     *    {@link ComponenteDisparo#notificarMovimiento(int, int, int, int)}
     * 4. Si el disparo sale del tablero, notifica con {@link #notificarDisparoFueraDeTablero(int, int)}
     * 5. Si hay colisión con enemigo, notifica con {@link #notificarColision(int, int, Enemigo)}
     */
    public void actualizarDisparo() {
        if (naveJugador() == null) return;

        Disparo d = naveJugador().getDisparo();
        if (!d.isActivo()) return;
        int oldX = d.getX();
        int oldY = d.getY();
        d.subir();

        if (!d.isActivo()) {
            notificarDisparoFueraDeTablero(oldX, oldY);
        } else {
            Enemigo golpeado = FlotaEnemigos.getFlotaEnemigos().comprobarColision(d);
            if (golpeado != null) {
                notificarColision(oldX, oldY, golpeado);
                if (isGameWon()) {
                    notificarVictoria();
                }
            }
        }
    }

    // ─── Actualización de enemigos ────────────────────────────────────────────

    // Llamado cada 200 ms: baja los enemigos 1 píxel
    public void actualizarEnemigos() {
        ArrayList<Enemigo> enemigos = FlotaEnemigos.getFlotaEnemigos().getEnemigos();
        Disparo d = naveJugador().getDisparo();

        for (Enemigo e : enemigos) {
            if (e.isVivo()) {
                int oldX = e.getX();
                int oldY = e.getY();
                e.mover(0, 1);
                if (d != null && d.isActivo()) {
                    FlotaEnemigos.getFlotaEnemigos().comprobarColision(d);
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
        Naves n = naveJugador();
        if (n == null) return;
        ArrayList<Enemigo> enemigos = FlotaEnemigos.getFlotaEnemigos().getEnemigos();
        int[][] celdasJ = n.celdasOcupadas();
        for (Enemigo e : enemigos) {
            if (e.isVivo()) {
                for (int[] c : celdasJ) {
                    setChanged();
                    notifyObservers(new int[] {6, c[0], c[1], e.getX(), e.getY()});
                }
            }
        }
    }

    /** Invocado desde {@link ComponenteNave} al cambiar de celda; dispara el Observer de la vista. */
    public void notificarMovimientoJugador(int oldX, int oldY, int newX, int newY) {
        setChanged();
        notifyObservers(new int[] {0, oldX, oldY, newX, newY});
    }

    /** Invocado desde {@link ComponenteDisparo} cuando se dispara un nuevo proyectil. */
    public void notificarDisparoNuevo(int x, int y) {
        setChanged();
        notifyObservers(new int[] {1, x, y});
    }

    /** Invocado desde {@link ComponenteDisparo} cuando el disparo se mueve. */
    public void notificarMovimientoDisparo(int oldX, int oldY, int newX, int newY) {
        setChanged();
        notifyObservers(new int[] {2, oldX, oldY, newX, newY});
    }

    /** Invocado desde {@link Espacio#actualizarDisparo()} cuando el disparo sale del tablero. */
    private void notificarDisparoFueraDeTablero(int oldX, int oldY) {
        setChanged();
        notifyObservers(new int[] {3, oldX, oldY});
    }

    /** Invocado desde {@link Espacio#actualizarEnemigos()} cuando un enemigo se mueve. */
    private void notificarMovimientoEnemigo(int oldX, int oldY, Enemigo e) {
        setChanged();
        notifyObservers(new int[] {4, oldX, oldY, e.getX(), e.getY()});
    }

    /** Invocado desde {@link Espacio#actualizarDisparo()} cuando hay colisión entre disparo y enemigo. */
    private void notificarColision(int disparoOldX, int disparoOldY, Enemigo golpeado) {
        setChanged();
        notifyObservers(new int[] {5, disparoOldX, disparoOldY, golpeado.getX(), golpeado.getY()});
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
