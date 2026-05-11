package model;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Random;

@SuppressWarnings("deprecation")
public class Espacio extends Observable {

    // CONSTANTES
    public static int CELDA_VACIO = 0;
    public static int CELDA_DISPARO = 1;
    public static int CELDA_ENEMIGO = 2;
    public static int CELDA_JUGADOR_NAVE1 = 3;
    public static int CELDA_JUGADOR_NAVE2 = 4;
    public static int CELDA_JUGADOR_NAVE3 = 5;
    public static int CELDA_JUGADOR_NAVE4 = 6;
    private static int NO_ID_DISPARO = -1;
    /** Notificación a observadores: eliminar de la flota al enemigo que contiene la celda (x,y). */
    public static int MSG_ELIMINAR_ENEMIGO = 18;

    /** Notificación a observadores: { [MSG_ELIMINAR_DISPARO, idDisparo]}. */
    public static int MSG_ELIMINAR_DISPARO = 19;

    private static Espacio miEspacio;

    private static int anchura = 100;
    private static int altura = 60;

    private int puntuacion = 0;

    /** [x][y]: copia del estado visual según las mismas notificaciones que la vista. */
    private int[][] tablero;

    /** Celdas ocupadas por la nave del jugador (espejo); colisión con enemigos solo leyendo, evitamos recorrer la matriz completa */
    private ArrayList<int[]> huellaJugadorEnMatriz = new ArrayList<>();

    private boolean gameOver;
    private boolean gameVictoria;

    /** Enemigos vivos en esta partida (incrementa al crearse cada {@link Enemigo}, decrementa al eliminarlo). */
    private int enemigosVivosRestantes;
    /** Evita restar dos veces si varias rutas notifican la muerte del mismo id. */
    private ArrayList<Integer> idsEnemigoYaRestadosEnEliminacion = new ArrayList<>();

    /** Constructor privado del singleton. */
    private Espacio() {
    }

    /** Devuelve la única instancia de {@link Espacio}. */
    public static Espacio getEspacio() {
        if (miEspacio == null) {
            miEspacio = new Espacio();
        }
        return miEspacio;
    }

    /** Registra observadores de partida, reinicia estado y notifica cambio a pantalla principal. */
    public void cambiarAMain() {
        addObserver(FlotaEnemigos.getFlotaEnemigos());
        addObserver(JugadorBueno.getJugadorBueno());
        inicializar();
        notificarCambioPantalla();
    }

    /** Incrementa el contador de enemigos vivos al crearse un enemigo. */
    public void registrarEnemigoCreadoEnConteo() {
        enemigosVivosRestantes++;
    }

    /** Pinta la celda inicial del enemigo en el espejo y avisa a la vista (mensaje 14). */
    public void registrarCeldaEnemigoInicialEnMatrizYVista(int x, int y, int idEnemigo) {
        if (!esValidoCelda(x, y)) {
            return;
        }
        setCeldaMatriz(x, y, idEnemigo);
        setChanged();
        notifyObservers(new int[] { 14, x, y });
    }

    /** Comprueba si (x, y) está dentro del tablero. */
    public boolean esValidoCelda(int x, int y) {
        return x >= 0 && x < anchura && y >= 0 && y < altura;
    }

    /** Comprueba que cada desplazamiento (dx, dy) desde xs/ys siga dentro del tablero. */
    public boolean puedeMoverse(int[] xs, int[] ys, int dx, int dy, int tipoNave) {
        for (int i = 0; i < xs.length; i++) {
            int newX = xs[i] + dx;
            int newY = ys[i] + dy;
            if (!esValidoCelda(newX, newY)) {
                if (tipoNave > 0) {
                    System.out.println("LIMITE alcanzado: pixel en " + xs[i] + "," + ys[i] + " intentaba ir a " + newX + "," + newY);
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Indica si la nave puede desplazarse: límites del tablero, partida activa y,
     * para el jugador ({@code tipoNave > 0}), que ninguna celda destino tenga un enemigo.
     * No modifica la matriz ni el {@link Composite}; quien llama debe mover la nave y luego
     */
    public boolean puedeAplicarMovimientoNave(int[] oldX, int[] oldY, int dx, int dy, int tipoNave) {

        if (!puedeMoverse(oldX, oldY, dx, dy, tipoNave)) {
            return false;
        }
        if (tipoNave > 0) {
            for (int i = 0; i < oldX.length; i++) {
                int nx = oldX[i] + dx;
                int ny = oldY[i] + dy;
                if (esEnemigoId(getCelda(nx, ny))) {
                    gameOver = true;
                    notificarGameOver();
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Tras mover los {@link Pixel} de la nave, actualiza el espejo {@link #tablero} y notifica a la vista.
     */
    public void realizarMovimientoNaveConMatriz(int[] oldX, int[] oldY, int dx, int dy, int tipoNave) {
        int[] currentX = new int[oldX.length];
        int[] currentY = new int[oldY.length];
        for (int i = 0; i < oldX.length; i++) {
            currentX[i] = oldX[i] + dx;
            currentY[i] = oldY[i] + dy;
        }
        notificarMovimientoJugadorYEnemigo(oldX, oldY, currentX, currentY, tipoNave);
    }

    /**
     * Derrota si hay enemigo en la última fila del espejo ({@code y == altura - 1}), equivalente a {@code y >= altura - 1} en coordenadas válidas.
     */
    public boolean isGameOver() {
        if (gameOver) {
            notificarGameOver();
            return true;
        }
        if (algunEnemigoLlegoAbajo()) {
            System.out.println("GAME OVER: enemigo llego abajo");
            gameOver = true;
            notificarGameOver();
            return true;
        }
        if (hayColisionJugadorEnemigo()) {
            System.out.println("GAME OVER: colision jugador-enemigo");
            gameOver = true;
            notificarGameOver();
            return true;
        }

        return false;
    }

    // Victoria: contador de enemigos vivos llegó a 0 (véase {@link #registrarEnemigoCreadoEnConteo} / {@link #registrarEnemigoEliminadoEnConteo}).
    /** Indica si la partida se ganó (todos los enemigos eliminados). */
    public boolean isGameWon() {
        if (gameVictoria) {
            return true;
        }
        if (enemigosVivosRestantes <= 0) {
            System.out.println("Victoria detectada!");
            gameVictoria = true;
            notificarVictoria();
            return true;
        }
        return false;
    }

    /**
     * Una celda de nave jugadora en la posición inicial: actualiza el espejo y notifica a la vista (tipos 15–17).
     */
    public void registrarCeldaJugadorInicialEnMatrizYVista(int x, int y, int tipoNave) {
        if (tipoNave <= 0 || tipoNave > 4) {
            return;
        }
        int tipoMensaje = 15;
        switch (tipoNave) {
            case 1: tipoMensaje = 15; break;
            case 2: tipoMensaje = 16; break;
            case 3: tipoMensaje = 17; break;
            case 4: tipoMensaje = 21; break;
        }
        int celdaJugador = tipoNaveACeldaJugador(tipoNave);
        setCeldaMatriz(x, y, celdaJugador);
        registrarCeldaEnHuellaJugador(x, y);
        setChanged();
        notifyObservers(new int[] {tipoMensaje, x, y});
    }

    /**
     * Actualiza matriz y vista tras mover jugador o enemigo; resuelve colisiones enemigo-disparo y jugador-enemigo.
     */
    public void notificarMovimientoJugadorYEnemigo(int[] oldX, int[] oldY, int[] currentX, int[] currentY, int tipoNave) {
        if (this.isGameOver() || this.isGameWon()) {
            return;
        }

        // Enemigo: flujo de colisiones con disparos
        if (tipoNave == 0) {

            int enemigoId = -1;
            if (oldX.length > 0 && oldY.length > 0) {
                int valorCelda = getCelda(oldX[0], oldY[0]);
                if (esEnemigoId(valorCelda)) {
                    enemigoId = valorCelda;
                }
            }

            if (enemigoId == -1) {
                System.out.println("ERROR: No se pudo identificar el enemigo en la matriz");
                return;
            }

            if (verificarColisionEnemigoAntesMovimiento(enemigoId, currentX, currentY)) {
                for (int i = 0; i < oldX.length; i++) {
                    setCeldaMatriz(oldX[i], oldY[i], CELDA_VACIO);
                }

                eliminarEnemigoYDisparo(enemigoId, currentX, currentY, oldX, oldY);
            } else {
                for (int i = 0; i < oldX.length; i++) {
                    setCeldaMatriz(oldX[i], oldY[i], CELDA_VACIO);
                }

                for (int i = 0; i < currentX.length; i++) {
                    setCeldaMatriz(currentX[i], currentY[i], enemigoId);
                }

                for (int i = 0; i < oldX.length; i++) {
                    setChanged();
                    notifyObservers(new int[] {10, oldX[i], oldY[i]});
                }

                for (int i = 0; i < currentX.length; i++) {
                    setChanged();
                    notifyObservers(new int[] {14, currentX[i], currentY[i]});
                }
            }
        } else {
            int celdaJugador = tipoNaveACeldaJugador(tipoNave);
            for (int i = 0; i < oldX.length; i++) {
                setCeldaMatriz(oldX[i], oldY[i], CELDA_VACIO);
                setChanged();
                notifyObservers(new int[] {10, oldX[i], oldY[i]});
            }

            for (int i = 0; i < currentX.length; i++) {
                if (esEnemigoId(getCelda(currentX[i], currentY[i]))) {
                    gameOver = true;
                    notificarGameOver();
                    return;
                }
            }

            int tipoMensaje = 15;
            switch (tipoNave) {
                case 1: tipoMensaje = 15; break;
                case 2: tipoMensaje = 16; break;
                case 3: tipoMensaje = 17; break;
                case 4: tipoMensaje = 21; break;
            }
            for (int i = 0; i < currentX.length; i++) {
                setCeldaMatriz(currentX[i], currentY[i], celdaJugador);
                setChanged();
                notifyObservers(new int[] {tipoMensaje, currentX[i], currentY[i]});
            }
            actualizarHuellaJugador(currentX, currentY);
        }
    }

    /** Marca game over, limpia celdas del jugador en matriz y vista y notifica derrota. */
    public void notificarMuerteJugador(int[][] posiciones) {
        if (gameOver) return;
        System.out.println("MUERTE JUGADOR llamado");
        gameOver = true;
        limpiarHuellaJugadorEnMatriz();
        for (int[] posicion : posiciones) {
            setCeldaMatriz(posicion[0], posicion[1], CELDA_VACIO);
            setChanged();
            notifyObservers(new int[] {10, posicion[0], posicion[1]});
        }
        notificarGameOver();
    }

// TODO: REVISAR ESTO
    public void notificarDisparoNuevo(int x, int y, int disparoId) {
        if (!isGameOver() && !isGameWon()) {
            setCeldaMatriz(x, y, disparoId);
            setChanged();
            notifyObservers(new int[] {1, x, y});
        }
    }

// TODO: REVISAR ESTO
    public void notificarMovimientoDisparo(int oldX, int oldY, int newX, int newY, int disparoId) {

        if (!isGameOver() && !isGameWon()) {
            setCeldaMatriz(oldX, oldY, CELDA_VACIO);

            if (esValidoCelda(newX, newY)) {
                int valorCelda = getCelda(newX, newY);

                if (esEnemigoId(valorCelda)) {
                	System.out.println("Colision detectada, disparoId recibido: " + disparoId);
                    int enemigoId = valorCelda;

                    borrarProyectilCompletoTrasImpacto(disparoId, oldX, oldY);

                    ArrayList<int[]> celdasDelEnemigo = new ArrayList<>();
                    for (int i = 0; i < anchura; i++) {
                        for (int j = 0; j < altura; j++) {
                            if (getCelda(i, j) == enemigoId) {
                                celdasDelEnemigo.add(new int[]{i, j});
                            }
                        }
                    }

                    for (int[] celda : celdasDelEnemigo) {
                        setCeldaMatriz(celda[0], celda[1], CELDA_VACIO);
                        setChanged();
                        notifyObservers(new int[]{12, celda[0], celda[1]});
                    }

                    registrarEnemigoEliminadoEnConteo(enemigoId);
                    setChanged();
                    notifyObservers(new int[]{MSG_ELIMINAR_ENEMIGO, enemigoId});

                } else {
                    setCeldaMatriz(newX, newY, disparoId);
                    setChanged();
                    notifyObservers(new int[] {2, oldX, oldY, newX, newY});
                }
            } else {
                setChanged();
                notifyObservers(new int[] {3, oldX, oldY});
            }
        }
    }

    /** Reinicia flags, puntuación, tablero y avisa a la flota para volver a poblar enemigos. */
    private void inicializar() {
        gameOver = false;
        gameVictoria = false;
        enemigosVivosRestantes = 0;
        idsEnemigoYaRestadosEnEliminacion.clear();
        puntuacion = 0;
        // Disparo.reiniciarContadorIdsDisparo();
        inicializarTablero();
        notificarInicializarFlota();
    }

    /** Decrementa enemigos vivos, suma puntos o declara victoria si no queda ninguno. */
    private void registrarEnemigoEliminadoEnConteo(int enemigoId) {
        if (!esEnemigoId(enemigoId)) {
            return;
        }
        for (int i = 0; i < idsEnemigoYaRestadosEnEliminacion.size(); i++) {
            if (idsEnemigoYaRestadosEnEliminacion.get(i) == enemigoId) {
                return;
            }
        }
        idsEnemigoYaRestadosEnEliminacion.add(enemigoId);
        enemigosVivosRestantes--;

        if (enemigosVivosRestantes > 0) {
            puntuacion += 100;
            setChanged();
            notifyObservers(new int[] {22, puntuacion});
        }
        else {
            gameVictoria = true;
            notificarVictoria();
            return;
        }
    }

    /** Elige un número aleatorio de enemigos y notifica mensaje 20 a observadores. */
    private void notificarInicializarFlota() {
        Random rand = new Random();
        int nEnemigos = rand.nextInt(5) + 4;
        setChanged();
        notifyObservers(new int[] {20, nEnemigos});
    }

    /** Crea el espejo con el tamaño exacto del tablero de juego y lo deja vacío. */
    private void inicializarTablero() {
        limpiarHuellaJugadorEnMatriz();
        int ancho = anchura;
        int alto = altura;
        tablero = new int[ancho][alto];
        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {
                tablero[x][y] = CELDA_VACIO;
            }
        }
    }

    /** Escribe un valor en el espejo si la celda es válida. */
    private void setCeldaMatriz(int x, int y, int tipo) {
        if (esValidoCelda(x, y)) {
            tablero[x][y] = tipo;
        }
    }

    /** Verifica si un valor representa un ID de enemigo (>= 11). */
    private boolean esEnemigoId(int valor) {
        return valor >= 11;
    }

    /** Devuelve true si algún enemigo ocupa la fila inferior del tablero. */
    private boolean algunEnemigoLlegoAbajo() {
        if (tablero == null) {
            return false;
        }
        int yInferior = altura - 1;
        for (int x = 0; x < anchura; x++) {
            if (esEnemigoId(tablero[x][yInferior])) {
                return true;
            }
        }
        return false;
    }

    /** Vacía la huella del jugador usada para colisiones rápidas con enemigos. */
    private void limpiarHuellaJugadorEnMatriz() {
        huellaJugadorEnMatriz.clear();
    }

    /** Añade una celda a la huella del jugador si aún no estaba. */
    private void registrarCeldaEnHuellaJugador(int x, int y) {
        for (int[] p : huellaJugadorEnMatriz) {
            if (p[0] == x && p[1] == y) {
                return;
            }
        }
        huellaJugadorEnMatriz.add(new int[] { x, y });
    }

    /** Reemplaza la huella del jugador por las celdas actuales tras un movimiento. */
    private void actualizarHuellaJugador(int[] xs, int[] ys) {
        huellaJugadorEnMatriz.clear();
        for (int i = 0; i < xs.length; i++) {
            huellaJugadorEnMatriz.add(new int[] { xs[i], ys[i] });
        }
    }

    /** Mapea tipo de nave (1–4) al código de celda del jugador en el espejo. */
    private static int tipoNaveACeldaJugador(int tipoNave) {
        switch (tipoNave) {
            case 1: return CELDA_JUGADOR_NAVE1;
            case 2: return CELDA_JUGADOR_NAVE2;
            case 3: return CELDA_JUGADOR_NAVE3;
            case 4: return CELDA_JUGADOR_NAVE4;
            default: return CELDA_JUGADOR_NAVE1;
        }
    }

    /** Comprueba si la huella del jugador coincide con alguna celda de enemigo en el espejo. */
    private boolean hayColisionJugadorEnemigo() {
        if (tablero == null || huellaJugadorEnMatriz.isEmpty()) {
            return false;
        }
        for (int[] p : huellaJugadorEnMatriz) {
            int x = p[0];
            int y = p[1];
            if (esEnemigoId(getCelda(x, y))) {
                return true;
            }
        }
        return false;
    }

    /** Lista todas las celdas del tablero con el valor igual al id del proyectil. */
    private ArrayList<int[]> celdasDelProyectilPorId(int disparoId) {
        ArrayList<int[]> celdas = new ArrayList<>();
        if (disparoId == NO_ID_DISPARO || tablero == null) {
            return celdas;
        }
        for (int x = 0; x < anchura; x++) {
            for (int y = 0; y < altura; y++) {
                if (tablero[x][y] == disparoId) {
                    celdas.add(new int[] { x, y });
                }
            }
        }
        return celdas;
    }

    /** Añade (x,y) a la lista de celdas si no está ya incluida. */
    private void agregarCeldaDisparoSiFalta(ArrayList<int[]> celdas, int x, int y) {
        for (int[] c : celdas) {
            if (c[0] == x && c[1] == y) {
                return;
            }
        }
        celdas.add(new int[] { x, y });
    }

    // APUNTE: incluirCeldaOrigen es para cuando un disparo impacta y sube, se incluye la celda de origen para que se borre la visualmente
    private void borrarProyectilCompletoPorId(int disparoId, boolean incluirCeldaOrigen, int oldX, int oldY) {
        ArrayList<int[]> celdas = celdasDelProyectilPorId(disparoId);
        if (incluirCeldaOrigen) {
            agregarCeldaDisparoSiFalta(celdas, oldX, oldY);
        }
        // APUNTE: añadir todas las celdas del disparo a la matriz para que se borren todas las celdas del disparo
        agregarCeldasDisparoEnMatrizQueCoincidenConId(celdas, disparoId);
        setChanged();
        notifyObservers(new int[] { MSG_ELIMINAR_DISPARO, disparoId });
        for (int[] cel : celdas) {
            int cx = cel[0];
            int cy = cel[1];
            if (esValidoCelda(cx, cy) && getCelda(cx, cy) == disparoId) {
                setCeldaMatriz(cx, cy, CELDA_VACIO);
            }
            setChanged();
            notifyObservers(new int[] { 3, cx, cy });
        }
    }

    /** Elimina el proyectil tras impactar un enemigo, incluyendo la celda de origen en el borrado visual. */
    private void borrarProyectilCompletoTrasImpacto(int disparoId, int oldX, int oldY) {
        borrarProyectilCompletoPorId(disparoId, true, oldX, oldY);
    }

    /**
     * Añade celdas del espejo que tienen exactamente ese {@code disparoId} (refuerzo frente a desfase en un tick).
     */
    private void agregarCeldasDisparoEnMatrizQueCoincidenConId(ArrayList<int[]> celdas, int disparoId) {
        for (int i = 0; i < anchura; i++) {
            for (int j = 0; j < altura; j++) {
                if (getCelda(i, j) != disparoId) {
                    continue;
                }
                agregarCeldaDisparoSiFalta(celdas, i, j);
            }
        }
    }

    /** Avisa a la vista que debe mostrar la pantalla principal (mensaje 9). */
    private void notificarCambioPantalla() {
        setChanged();
        notifyObservers(new int[] {9});
    }

    /** Notifica derrota con la puntuación actual (mensaje 7). */
    private void notificarGameOver() {
        setChanged();
        notifyObservers(new int[] {7, puntuacion});
    }

    /** Notifica victoria con la puntuación actual (mensaje 8). */
    private void notificarVictoria() {
        setChanged();
        notifyObservers(new int[] {8, puntuacion});
    }

    /** Comprueba si alguna celda destino del enemigo contiene un proyectil (valor >= 21). */
    private boolean verificarColisionEnemigoAntesMovimiento(int enemigoId, int[] currentX, int[] currentY) {
        for (int i = 0; i < currentX.length; i++) {
            int valorCelda = getCelda(currentX[i], currentY[i]);
            if (valorCelda >= 21) {
                System.out.println("¡COLISIÓN DETECTADA!");
                return true;
            }
        }
        return false;
    }

    /** Elimina enemigo y proyectiles implicados en colisión lateral y notifica a flota y vista. */
    private void eliminarEnemigoYDisparo(int enemigoId, int[] currentX, int[] currentY, int[] oldX, int[] oldY) {
        ArrayList<int[]> disparosColisionados = new ArrayList<>();
        for (int i = 0; i < currentX.length; i++) {
            if (getCelda(currentX[i], currentY[i]) >= 21) {
                disparosColisionados.add(new int[] { currentX[i], currentY[i] });
            }
        }
        // APU
        ArrayList<Integer> idsDisparos = new ArrayList<>();
        for (int[] disparo : disparosColisionados) {
            int id = getCelda(disparo[0], disparo[1]);
        	if (id < 21) {
                continue;
            }
            boolean repetido = false;
            for (int k = 0; k < idsDisparos.size(); k++) {
                if (idsDisparos.get(k) == id) {
                    repetido = true;
                    break;
                }
            }
            if (!repetido) {
                idsDisparos.add(id);
            }
        }

        // APUNTE: el codigo esto hecho pensando en que varios disparos puedan impactar la misma nave, aunque esto sea improbable
        for (int i = 0; i < oldX.length; i++) {
            setChanged();
            notifyObservers(new int[] { 12, oldX[i], oldY[i] });
        }

        for (int i = 0; i < idsDisparos.size(); i++) {
            borrarProyectilCompletoPorId(idsDisparos.get(i), false, 0, 0);
        }

        registrarEnemigoEliminadoEnConteo(enemigoId);
        setChanged();
        notifyObservers(new int[] { MSG_ELIMINAR_ENEMIGO, enemigoId });
    }

    /** Lectura del espejo; fuera de rango o antes de la primera partida devuelve {@link #CELDA_VACIO}. */
    public int getCelda(int x, int y) {
        if (tablero == null || !esValidoCelda(x, y)) {
            return CELDA_VACIO;
        }
        return tablero[x][y];
    }

}
