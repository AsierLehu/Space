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
    private boolean gameOver;

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
        gameOver = false;
        JugadorBueno.getJugadorBueno().crearNaveParaPartida();
        FlotaEnemigos.getFlotaEnemigos().inicializar(anchura);
    }

    private Naves getNaveJugador() {
        return JugadorBueno.getJugadorBueno().getNave();
    }


    public int getAnchura() { return anchura; }
    public int getAltura()  { return altura;  }

    // ─── Estado del juego ─────────────────────────────────────────────────────

    // Derrota: jugador muerto, algún enemigo llegó al límite inferior, o colisión jugador-enemigo
    public boolean isGameOver() {
        if (gameOver) return true;
        if (FlotaEnemigos.getFlotaEnemigos().algunoLlegoAbajo(altura)) {
            gameOver = true;
            return true;
        }
        return hayColisionJugadorEnemigo();
    }
    
    // Verifica si el jugador ha colisionado directamente con algún enemigo
    private boolean hayColisionJugadorEnemigo() {
        Naves j = getNaveJugador();
        if (j == null || !j.isVivo()) return false;

        // Obtener todos los píxeles de la nave del jugador
        Component componenteJugador = j.getComponente();
        if (componenteJugador == null) return false;
        
        // Verificar colisión pixel a pixel
        for (Enemigo enemigo : FlotaEnemigos.getFlotaEnemigos().getEnemigos()) {
            if (!enemigo.isVivo()) continue;
            
            int[][] celdasEnemigo = enemigo.celdasOcupadas();
            if (componenteJugador instanceof Composite raiz) {
                for (Component c : raiz.getComponents()) {
                    int x = c.getRefX();
                    int y = c.getRefY();
                    
                    for (int[] celdaEnemigo : celdasEnemigo) {
                        if (x == celdaEnemigo[0] && y == celdaEnemigo[1]) {
                            j.morirComoJugador();
                            return true; // Hay colisión
                        }
                    }
                }
            }
            else {
                int x_pixel = componenteJugador.getRefX();
                int y_pixel = componenteJugador.getRefY();
                
                for (int[] celdaEnemigo_ : celdasEnemigo) {
                    if (x_pixel == celdaEnemigo_[0] && y_pixel == celdaEnemigo_[1]) {
                        j.morirComoJugador();
                        return true; // Hay colisión
                    }
                }
            }
        }
        return false;
    }

    // Victoria: la flota existe y todos los enemigos han sido destruidos
    public boolean isGameWon() {
        return FlotaEnemigos.getFlotaEnemigos().todosDestruidos();
    }

    // ─── Acciones del jugador ─────────────────────────────────────────────────


    public void cambiarTipoDisparo() {
        Naves j = getNaveJugador();
        if (j != null && j.isVivo()) {
            j.cambiarTipoDisparo();
        }
    }

    // ─── Actualización del disparo ────────────────────────────────────────────

    /**
     * Llamado cada 50 ms: mueve el disparo y comprueba colisiones.
     * 
     * Flujo:
     * 1. Colisión en la posición actual (evita perder el impacto en y=0: si subir() va antes, el
     *    proyectil pasa a y=-1, se desactiva y nunca se comprueba contra el enemigo).
     * 2. Si sigue activo, Disparo.subir(); si sale del tablero, borrar celdas.
     * 3. Si tras subir sigue activo, volver a comprobar colisión (entrada en celda del enemigo).
     */
    public void actualizarDisparo() {
        if (getNaveJugador() == null) return;

        ArrayList<Component> disparos = getNaveJugador().getDisparos();
        ArrayList<Component> disparosParaMantener = new ArrayList<>();

        for (Component d : disparos) {
            Enemigo golpeado = FlotaEnemigos.getFlotaEnemigos().comprobarColision(d);
            if (golpeado != null) {
                int[][] celdasDisparoEnColision = getCeldasOcupadas(d);
                d.setActivo(false);
                notificarColision(celdasDisparoEnColision, golpeado);
                if (isGameWon()) {
                    notificarVictoria();
                }
                continue;
            }

            d.mover(0, -1);

            if (!d.isActivo()) {
                int[][] celdasDisparo = getCeldasOcupadas(d);
                for (int[] celda : celdasDisparo) {
                    setChanged();
                    notifyObservers(new int[] {3, celda[0], celda[1]});
                }
            } else {
                golpeado = FlotaEnemigos.getFlotaEnemigos().comprobarColision(d);
                if (golpeado != null) {
                    int[][] celdasDisparoEnColision = getCeldasOcupadas(d);
                    d.setActivo(false);
                    notificarColision(celdasDisparoEnColision, golpeado);
                    if (isGameWon()) {
                        notificarVictoria();
                    }
                } else {
                    disparosParaMantener.add(d);
                }
            }
        }
        getNaveJugador().getDisparos().retainAll(disparosParaMantener);
    }

    // Método auxiliar para obtener celdas ocupadas por un Component
    private int[][] getCeldasOcupadas(Component disparo) {
        if (disparo instanceof Composite comp) {
            java.util.List<int[]> lista = comp.celdasOcupadasActivas();
            return lista.toArray(new int[0][]);
        }
        return new int[][] { { disparo.getRefX(), disparo.getRefY() } };
    }

    // ─── Actualización de enemigos ────────────────────────────────────────────

    // Llamado cada 200 ms: baja los enemigos 1 píxel
    public void actualizarEnemigos() {
        ArrayList<Enemigo> enemigos = FlotaEnemigos.getFlotaEnemigos().getEnemigos();

        for (Enemigo e : enemigos) {
            if (e.isVivo()) {
                // Guardar posiciones antiguas de todos los píxeles
                ArrayList<int[]> oldPixels = new ArrayList<>();
                Component naveEnemigo = e.getComponente();
                if (naveEnemigo instanceof Composite raiz) {
                    for (Component c : raiz.getComponents()) {
                        oldPixels.add(new int[] { c.getRefX(), c.getRefY() });
                    }
                }
                else {
                    oldPixels.add(new int[] { naveEnemigo.getRefX(), naveEnemigo.getRefY() });
                }
                
                // Mover el enemigo
                e.mover(0, 1);
                
                // Guardar posiciones nuevas
                ArrayList<int[]> newPixels = new ArrayList<>();
                if (naveEnemigo instanceof Composite raiz) {
                    for (Component c : raiz.getComponents()) {
                        newPixels.add(new int[] { c.getRefX(), c.getRefY() });
                    }
                }
                else {
                    newPixels.add(new int[] { naveEnemigo.getRefX(), naveEnemigo.getRefY() });
                }
                
                // Notificar movimiento de todos los píxeles
                notificarMovimientoEnemigo(oldPixels, newPixels);
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
        Naves n = getNaveJugador();
        if (n == null) return;
        
        // Pintar la nave del jugador usando los componentes directamente
        Component naveJugadorComp = n.getComponente();
        if (naveJugadorComp instanceof Composite raiz) {
            for (Component c : raiz.getComponents()) {
                setChanged();
                notifyObservers(new int[] {13, c.getRefX(), c.getRefY()});
            }
        }
        
        // Pintar los enemigos - pintar TODOS los píxeles de cada enemigo
        ArrayList<Enemigo> enemigos = FlotaEnemigos.getFlotaEnemigos().getEnemigos();
        for (Enemigo e : enemigos) {
            if (e.isVivo()) {
                Component componenteEnemigo = e.getComponente();
                if (componenteEnemigo instanceof Composite raiz) {
                    for (Component c : raiz.getComponents()) {
                        setChanged();
                        notifyObservers(new int[] {14, c.getRefX(), c.getRefY()});
                    }
                }
                else {
                    setChanged();
                    notifyObservers(new int[] {14, componenteEnemigo.getRefX(), componenteEnemigo.getRefY()});
                }
            }
        }
    }

    public void notificarMovimientoJugadorCompleto(int[] oldX, int[] oldY, java.util.List<Component> componentes) {
        // Primero borra todas las celdas antiguas
        if (!this.isGameOver() && !this.isGameWon()) {
        for (int i = 0; i < oldX.length; i++) {
                setChanged();
                notifyObservers(new int[] {10, oldX[i], oldY[i]});
            }
            // Luego pinta todas las celdas nuevas
            for (Component c : componentes) {
                setChanged();
                notifyObservers(new int[] {11, c.getRefX(), c.getRefY()});
            }
        }
    }

    public void notificarMuerteJugador(int[][] posiciones) {
        if (gameOver) return;

        gameOver = true;
        for (int[] posicion : posiciones) {
            setChanged();
            notifyObservers(new int[] {10, posicion[0], posicion[1]});
        }
        notificarGameOver();
    }

    /** Invocado desde el modelo al cambiar de celda del jugador; dispara el Observer de la vista. */
    public void notificarMovimientoJugador(int oldX, int oldY, int newX, int newY) {
        setChanged();
        notifyObservers(new int[] {0, oldX, oldY, newX, newY});
    }

    /** Invocado cuando se crea un proyectil en pantalla. */
    public void notificarDisparoNuevo(int x, int y) {
        if (!isGameOver() && !isGameWon()) {
            setChanged();
            notifyObservers(new int[] {1, x, y});
        }
    }

    /** Invocado cuando el proyectil se mueve. */
    public void notificarMovimientoDisparo(int oldX, int oldY, int newX, int newY) {
        if (!isGameOver() && !isGameWon()) {
            setChanged();
            notifyObservers(new int[] {2, oldX, oldY, newX, newY});
        }
    }

    /** Cuando el disparo sale del tablero (durante actualizarDisparo). */
    private void notificarDisparoFueraDeTablero(int oldX, int oldY) {
        setChanged();
        notifyObservers(new int[] {3, oldX, oldY});
    }

    /** Cuando un enemigo se mueve (actualizarEnemigos): borra y repinta todos los píxeles. */
    private void notificarMovimientoEnemigo(ArrayList<int[]> oldPixels, ArrayList<int[]> newPixels) {
    	// Borrar píxeles antiguos
        for (int[] pixel : oldPixels) {
            setChanged();
            notifyObservers(new int[] {12, pixel[0], pixel[1]});  // tipo 12: borrar enemigo
        }
        // Pintar píxeles nuevos
        for (int[] pixel : newPixels) {
            setChanged();
            notifyObservers(new int[] {14, pixel[0], pixel[1]});  // tipo 14: pintar enemigo
        }
    }

    /** Colisión disparo-enemigo durante actualizarDisparo. */
    private void notificarColision(int[][] celdasDisparo, Enemigo golpeado) {
        // Borrar TODOS los píxeles del disparo
        for (int[] celda : celdasDisparo) {
            setChanged();
            notifyObservers(new int[] {3, celda[0], celda[1]});  // tipo 3: borrar píxel disparo
        }
        
        // Borrar TODOS los píxeles del enemigo golpeado
        int[][] celdasEnemigo = golpeado.celdasOcupadas();
        for (int[] celda : celdasEnemigo) {
            setChanged();
            notifyObservers(new int[] {12, celda[0], celda[1]});  // tipo 12: borrar píxel enemigo
        }
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
