package model;
 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    /** Notificaci�n a observadores: eliminar de la flota al enemigo que contiene la celda (x,y). */
    public static int MSG_ELIMINAR_ENEMIGO = 18;
    
    /** Notificaci�n a observadores: { [MSG_ELIMINAR_DISPARO, idDisparo]}. */
    public static int MSG_ELIMINAR_DISPARO = 19;
 
    // SINGLETON    
    private static Espacio miEspacio;
 
    // PROPIEDADES DE ESTADO    
    private static int anchura = 100;
    private static int altura  = 60;
 
    /** [x][y]: copia del estado visual seg�n las mismas notificaciones que la vista. */
    private int[][] tablero;

    /** Celdas ocupadas por la nave del jugador (espejo); colisión con enemigos solo leyendo {@link #tablero}. */
    private ArrayList<int[]> huellaJugadorEnMatriz = new ArrayList<>();
 
    private boolean gameOver;
    private boolean gameVictoria;

    /** Enemigos vivos en esta partida (incrementa al crearse cada {@link Enemigo}, decrementa al eliminarlo). */
    private int enemigosVivosRestantes;
    /** Evita restar dos veces si varias rutas notifican la muerte del mismo id. */
    private ArrayList<Integer> idsEnemigoYaRestadosEnEliminacion = new ArrayList<>();
 
 
    // CONSTRUCTOR Y PATR�N SINGLETON 
    private Espacio() {
    }
 
    public static Espacio getEspacio() {
        if (miEspacio == null) {
            miEspacio = new Espacio();
        }
        return miEspacio;
    }
 
    // INICIALIZACI�N DEL JUEGO
    public void cambiarAMain() {
        addObserver(FlotaEnemigos.getFlotaEnemigos());
        addObserver(JugadorBueno.getJugadorBueno());
        inicializar();
        notificarCambioPantalla();
        TimerEnemigo.getInstancia().iniciar();
        TimerDisparo.getInstancia().iniciar();
    }
 
    private void inicializar() {
        gameOver = false;
        gameVictoria = false;
        enemigosVivosRestantes = 0;
        idsEnemigoYaRestadosEnEliminacion.clear();
        // Disparo.reiniciarContadorIdsDisparo();
        inicializarTablero();
        notificarInicializarFlota();
    }

    public void registrarEnemigoCreadoEnConteo() {
        enemigosVivosRestantes++;
    }

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
        if (enemigosVivosRestantes > 0) {
            enemigosVivosRestantes--;
        }
    }
    
    private void notificarInicializarFlota() {
        Random rand = new Random();
        int n_enemigos = rand.nextInt(5) + 4;
        setChanged();
        notifyObservers(new int[] {20, n_enemigos});
    }
 
    // GESTI�N DEL TABLERO Y MATRIZ ESPEJO 
    /** Crea el espejo con el tama�o exacto del tablero de juego y lo deja vac�o. */
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
        // Los enemigos se pintan al crearse la flota (mensaje 20): cada {@link Enemigo} notifica vía {@link Component}.
    }

    /** Celda inicial de enemigo: espejo + notificación de pintado (mensaje 14), igual que el movimiento. */
    public void registrarCeldaEnemigoInicialEnMatrizYVista(int x, int y, int idEnemigo) {
        if (!esValidoCelda(x, y)) {
            return;
        }
        setCeldaMatriz(x, y, idEnemigo);
        setChanged();
        notifyObservers(new int[] { 14, x, y });
    }
 
    /** Lectura del espejo; fuera de rango o antes de la primera partida devuelve {@link #CELDA_VACIO}. */
    public int getCelda(int x, int y) {
        if (tablero == null || !esValidoCelda(x, y)) {
            return CELDA_VACIO;
        }
        return tablero[x][y];
    }
 
    private void setCeldaMatriz(int x, int y, int tipo) {
        if (esValidoCelda(x, y)) {
            tablero[x][y] = tipo;
        }
    }
   
    public boolean esValidoCelda(int x, int y) {
        return x >= 0 && x < anchura && y >= 0 && y < altura;
    }
 
    /** Verifica si un valor representa un ID de enemigo (>= 11). */
    private boolean esEnemigoId(int valor) {
        return valor >= 11;
    }

    /** Derrota si hay enemigo en la última fila del espejo ({@code y == altura - 1}), equivalente a {@code y >= altura - 1} en coordenadas válidas. */
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

    private void limpiarHuellaJugadorEnMatriz() {
        huellaJugadorEnMatriz.clear();
    }

    private void registrarCeldaEnHuellaJugador(int x, int y) {
        for (int[] p : huellaJugadorEnMatriz) {
            if (p[0] == x && p[1] == y) {
                return;
            }
        }
        huellaJugadorEnMatriz.add(new int[] { x, y });
    }

    private void reemplazarHuellaJugadorDesdeArreglos(int[] xs, int[] ys) {
        huellaJugadorEnMatriz.clear();
        for (int i = 0; i < xs.length; i++) {
            huellaJugadorEnMatriz.add(new int[] { xs[i], ys[i] });
        }
    }

    private static int tipoNaveACeldaJugador(int tipoNave) {
        switch (tipoNave) {
            case 1: return CELDA_JUGADOR_NAVE1;
            case 2: return CELDA_JUGADOR_NAVE2;
            case 3: return CELDA_JUGADOR_NAVE3;
            case 4: return CELDA_JUGADOR_NAVE4;
            default: return CELDA_JUGADOR_NAVE1;
        }
    }
 
    /** Método público para que FlotaEnemigos pueda hacer notificaciones. */
    public void notificarCambio(int[] mensaje) {
        setChanged();
        notifyObservers(mensaje);
    }
 
    // LÓGICA DE ESTADO DEL JUEGO 
    // Derrota: jugador muerto, algún enemigo llegó al límite inferior, o colisión jugador-enemigo
    public boolean isGameOver() {
        if (gameOver) {
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
    
    // COLISION JUGADOR - ENEMIGO: huella del jugador en espejo + valor en {@link #tablero}.
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

    private int idDisparoEnCeldaJugador(int x, int y) {
        if (!esValidoCelda(x, y)) {
            return NO_ID_DISPARO;
        }
        int valor = getCelda(x, y);
        if (valor >= 21) {
            return valor;
        }
        return NO_ID_DISPARO;
    }

    /** Celdas del espejo cuyo valor es exactamente este {@code disparoId}. */
    private ArrayList<int[]> celdasDelProyectilJugadorPorId(int disparoId) {
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

    private void agregarCeldaDisparoSiFalta(ArrayList<int[]> celdas, int x, int y) {
        for (int[] c : celdas) {
            if (c[0] == x && c[1] == y) {
                return;
            }
        }
        celdas.add(new int[] { x, y });
    }

    /**
     * Borra todo el proyectil con ese id (lista del jugador, matriz y vista).
     * @param incluirCeldaOrigen si true, añade {@code (oldX, oldY)} (píxel que se movió al impactar subiendo).
     */
    private void borrarProyectilCompletoPorId(int disparoId, boolean incluirCeldaOrigen, int oldX, int oldY) {
        ArrayList<int[]> celdas = celdasDelProyectilJugadorPorId(disparoId);
        if (incluirCeldaOrigen) {
            agregarCeldaDisparoSiFalta(celdas, oldX, oldY);
        }
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

    /** Impacto disparo subiendo contra enemigo (desde {@link #notificarMovimientoDisparo}). */
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
 
    // GESTI�N DE DISPAROS 
    
   
    
    
 
    /** Superposición en el espejo: alguna celda del disparo coincide con {@link #CELDA_ENEMIGO}. */
    private boolean disparoDetectarColisionMatrizAntesMover(Component d) {
        for (int[] celda : getCeldasOcupadas(d)) {
            if (esEnemigoId(getCelda(celda[0], celda[1]))) {
                return true;
            }
        }
        return false;
    }
 
    private void disparoResolverImpactoConEnemigoSuperposicion(Component d) {
        int[][] celdasDisparo = getCeldasOcupadas(d);
        d.setActivo(false);
        // Borrar visual del disparo y notificar eliminación de enemigos en cada celda con colisión
        for (int[] celda : celdasDisparo) {
            if (esEnemigoId(getCelda(celda[0], celda[1]))) {
                // Primero notificar eliminación del enemigo (antes de limpiar la celda)
                notificarFlotaEliminarEnemigo(celda[0], celda[1]);
                // Luego limpiar el disparo
                setCeldaMatriz(celda[0], celda[1], CELDA_VACIO);
                setChanged();
                notifyObservers(new int[] {3, celda[0], celda[1]});  // borrar disparo
            } else {
                setCeldaMatriz(celda[0], celda[1], CELDA_VACIO);
                setChanged();
                notifyObservers(new int[] {3, celda[0], celda[1]});
            }
        }
    }
 
 
    /** Elimina un enemigo usando directamente su ID. */
    private void notificarFlotaEliminarEnemigoConId(int enemigoId) {
        // Buscar todas las celdas en la matriz que tengan este mismo ID
        ArrayList<int[]> celdasDelEnemigo = new ArrayList<>();
        for (int i = 0; i < anchura; i++) {
            for (int j = 0; j < altura; j++) {
                if (getCelda(i, j) == enemigoId) {
                    celdasDelEnemigo.add(new int[]{i, j});
                }
            }
        }
        
        registrarEnemigoEliminadoEnConteo(enemigoId);
        // Notificar a FlotaEnemigos para que elimine el enemigo de su lista
        setChanged();
        notifyObservers(new int[] { MSG_ELIMINAR_ENEMIGO, enemigoId, -1, -1 });
        
        // Borrar visualmente todas las celdas del enemigo encontradas en la matriz
        for (int[] celda : celdasDelEnemigo) {
            setCeldaMatriz(celda[0], celda[1], CELDA_VACIO);
            setChanged();
            notifyObservers(new int[] {12, celda[0], celda[1]});
        }
    }
    
    /** Elimina un enemigo: busca todas las celdas con su ID en la matriz y las borra. */
    private void notificarFlotaEliminarEnemigo(int x, int y) {
        int enemigoId = getCelda(x, y); // Obtener el ID del enemigo desde la matriz
        if (esEnemigoId(enemigoId)) {
            // Buscar todas las celdas en la matriz que tengan este mismo ID
            ArrayList<int[]> celdasDelEnemigo = new ArrayList<>();
            for (int i = 0; i < anchura; i++) {
                for (int j = 0; j < altura; j++) {
                    if (getCelda(i, j) == enemigoId) {
                        celdasDelEnemigo.add(new int[]{i, j});
                    }
                }
            }
            
            registrarEnemigoEliminadoEnConteo(enemigoId);
            // Notificar a FlotaEnemigos para que elimine el enemigo de su lista
            setChanged();
            notifyObservers(new int[] { MSG_ELIMINAR_ENEMIGO, enemigoId, x, y });
            
            // Borrar visualmente todas las celdas del enemigo encontradas en la matriz
            for (int[] celda : celdasDelEnemigo) {
                setCeldaMatriz(celda[0], celda[1], CELDA_VACIO);
                setChanged();
                notifyObservers(new int[] {12, celda[0], celda[1]});
            }
        }
    }
 
    
 
    // Método auxiliar para obtener celdas ocupadas por un Component
    private int[][] getCeldasOcupadas(Component disparo) {
        if (disparo instanceof Composite comp) {
            ArrayList<int[]> lista = comp.celdasOcupadasActivas();
            return lista.toArray(new int[0][]);
        }
        return new int[][] { { disparo.getRefX(), disparo.getRefY() } };
    }
 
    // GESTIÓN DE ENEMIGOS 
    // Llamado cada 200 ms: baja los enemigos 1 píxel
    
    /** Elimina un enemigo que colisiona con disparo durante su movimiento. */
    private void enemigoEliminarPorColisionEnMovimiento(int enemigoId, ArrayList<int[]> celdasActuales, ArrayList<int[]> colisionesConDisparos) {
        // Limpiar todas las celdas actuales del enemigo
        for (int[] celda : celdasActuales) {
            setCeldaMatriz(celda[0], celda[1], CELDA_VACIO);
            setChanged();
            notifyObservers(new int[]{12, celda[0], celda[1]}); // borrar enemigo
        }
        
        // Limpiar disparos que colisionaron - TODO: RESOLVER EL HECHO DE QUE SOLO SE BORRAN LOS PIXELES DEL DISPARO QUE COLISIONAN
        for (int[] colision : colisionesConDisparos) {
            setCeldaMatriz(colision[0], colision[1], CELDA_VACIO);
            setChanged();
            notifyObservers(new int[]{3, colision[0], colision[1]}); // borrar disparo
        }
        
        registrarEnemigoEliminadoEnConteo(enemigoId);
        // Notificar a FlotaEnemigos para eliminar de la lista
        setChanged();
        notifyObservers(new int[]{MSG_ELIMINAR_ENEMIGO, enemigoId, -1, -1});
    }
    
    /** Mueve un enemigo en la matriz de una posición a otra. */
    private void enemigoMoverEnMatriz(int enemigoId, ArrayList<int[]> celdasActuales, ArrayList<int[]> nuevasPosiciones) {
        // Limpiar posiciones anteriores
        for (int[] celda : celdasActuales) {
            setCeldaMatriz(celda[0], celda[1], CELDA_VACIO);
            setChanged();
            notifyObservers(new int[]{12, celda[0], celda[1]}); // borrar píxel anterior
        }
        
        // Establecer nuevas posiciones con el ID del enemigo
        for (int[] nuevaPos : nuevasPosiciones) {
            setCeldaMatriz(nuevaPos[0], nuevaPos[1], enemigoId);
            setChanged();
            notifyObservers(new int[]{14, nuevaPos[0], nuevaPos[1]}); // pintar enemigo
        }
    }
 
    // SISTEMA DE NOTIFICACIONES 
    private void notificarCambioPantalla() {
        setChanged();
        notifyObservers(new int[] {9});
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
 
    
    public void notificarMovimientoJugadorYEnemigo(int[] oldX, int[] oldY, int[] currentX, int[] currentY, int tipoNave) {
        if (this.isGameOver() || this.isGameWon()) {
            return;
        }
        
        // Si tipoNave es 0, es un enemigo - usar nuevo flujo de colisiones
        if (tipoNave == 0) {
            
            // 1. Obtener ID del enemigo desde la matriz (posición anterior)
            int enemigoId = -1;
            if (oldX.length > 0 && oldY.length > 0) {
                int valorCelda = getCelda(oldX[0], oldY[0]);
                if (esEnemigoId(valorCelda)) {
                    enemigoId = valorCelda;
                }
            }
            
            if (enemigoId == -1) {
                System.out.println("ERROR: No se pudo identificar el enemigo en la matriz");
                return; // No se pudo identificar el enemigo en la matriz
            }
            
            // 2. PRIMERO verificar colisiones en las nuevas posiciones (antes de actualizar matriz)
            if (verificarColisionEnemigoDespuesMovimiento(enemigoId, currentX, currentY)) {
                // Hay colisión - eliminar enemigo y disparos
                // Primero limpiar posiciones anteriores del enemigo
                for (int i = 0; i < oldX.length; i++) {
                    setCeldaMatriz(oldX[i], oldY[i], CELDA_VACIO);
                }
                
                eliminarEnemigoYDisparo(enemigoId, currentX, currentY, oldX, oldY);
            } else {
                // Sin colisión - actualizar matriz normalmente
                // Limpiar posiciones anteriores
                for (int i = 0; i < oldX.length; i++) {
                    setCeldaMatriz(oldX[i], oldY[i], CELDA_VACIO);
                }
                
                // Establecer nuevas posiciones con el ID del enemigo
                for (int i = 0; i < currentX.length; i++) {
                    setCeldaMatriz(currentX[i], currentY[i], enemigoId);
                }
                
                // Notificar MainFrame para actualizar visualización
                for (int i = 0; i < oldX.length; i++) {
                    setChanged();
                    notifyObservers(new int[] {10, oldX[i], oldY[i]}); // borrar píxel anterior
                }
                
                for (int i = 0; i < currentX.length; i++) {
                    setChanged();
                    notifyObservers(new int[] {14, currentX[i], currentY[i]}); // pintar píxel nuevo
                }
            }
        } else {
            // Lógica original para el jugador
            int celdaJugador = tipoNaveACeldaJugador(tipoNave);
            for (int i = 0; i < oldX.length; i++) {
                setCeldaMatriz(oldX[i], oldY[i], CELDA_VACIO);
                setChanged();
                notifyObservers(new int[] {10, oldX[i], oldY[i]});
            }
            // Determinar tipo de mensaje según el tipo de nave recibido
            int tipoMensaje = 15; 
            switch (tipoNave) {
                case 1: tipoMensaje = 15; break; // Verde (Nave1)
                case 2: tipoMensaje = 16; break; // Azul (Nave2)
                case 3: tipoMensaje = 17; break; // Morado (Nave3)
                case 4: tipoMensaje = 21; break; // Amarillo (Nave4)
            }
            // Luego pinta todas las celdas nuevas con el color correcto
            for (int i = 0; i < currentX.length; i++) {
                setCeldaMatriz(currentX[i], currentY[i], celdaJugador);
                setChanged();
                notifyObservers(new int[] {tipoMensaje, currentX[i], currentY[i]});
            }
            reemplazarHuellaJugadorDesdeArreglos(currentX, currentY);
        }
    }
 
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
        boolean gameOver = isGameOver();
        boolean gameWon = isGameWon();
        
        if (!gameOver && !gameWon) {
            setCeldaMatriz(oldX, oldY, CELDA_VACIO);
            
            if (esValidoCelda(newX, newY)) {
                int valorCelda = getCelda(newX, newY);
                
                // Detectar colisión con enemigo
                if (esEnemigoId(valorCelda)) {
                    int enemigoId = valorCelda;

                    // A. Todo el proyectil (mismo disparoId): matriz, vista y lista del jugador
                    borrarProyectilCompletoTrasImpacto(disparoId, oldX, oldY);
                    
                    // B. Eliminar el enemigo completo
                    // Buscar todas las posiciones del enemigo en la matriz
                    ArrayList<int[]> celdasDelEnemigo = new ArrayList<>();
                    for (int i = 0; i < anchura; i++) {
                        for (int j = 0; j < altura; j++) {
                            if (getCelda(i, j) == enemigoId) {
                                celdasDelEnemigo.add(new int[]{i, j});
                            }
                        }
                    }
                    
                    // Limpiar todas las posiciones del enemigo de la matriz
                    for (int[] celda : celdasDelEnemigo) {
                        setCeldaMatriz(celda[0], celda[1], CELDA_VACIO);
                        // Notificar borrado visual de cada píxel del enemigo
                        setChanged();
                        notifyObservers(new int[]{12, celda[0], celda[1]});
                    }
                    
                    registrarEnemigoEliminadoEnConteo(enemigoId);
                    // Notificar eliminación del enemigo a FlotaEnemigos
                    setChanged();
                    notifyObservers(new int[]{MSG_ELIMINAR_ENEMIGO, enemigoId});
                    
                } else {
                    // Sin colisión: actualizar matriz normalmente
                    setCeldaMatriz(newX, newY, disparoId);
                    setChanged();
                    notifyObservers(new int[] {2, oldX, oldY, newX, newY});
                }
            } else {
                // Nueva celda fuera del tablero (p. ej. disparo sube por y < 0): el espejo ya vació oldX,oldY
                setChanged();
                notifyObservers(new int[] {3, oldX, oldY});
            }
        } else {
            System.out.println("DEBUG: Movimiento de disparo BLOQUEADO por estado del juego");
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
 
    // BUCLE PRINCIPAL DE JUEGO 
    private boolean verificarColisionEnemigoDespuesMovimiento(int enemigoId, int[] currentX, int[] currentY) {
        // Verificar si algún píxel del enemigo coincide con un disparo
        for (int i = 0; i < currentX.length; i++) {
            int valorCelda = getCelda(currentX[i], currentY[i]);
            if (valorCelda >= 21) {
                System.out.println("¡COLISIÓN DETECTADA!");
                return true; // Colisión detectada
            }
        }
        return false; // No hay colisión
    }
    
   
    private void eliminarEnemigoYDisparo(int enemigoId, int[] currentX, int[] currentY, int[] oldX, int[] oldY) {
        ArrayList<int[]> disparosColisionados = new ArrayList<>();
        for (int i = 0; i < currentX.length; i++) {
            if (getCelda(currentX[i], currentY[i]) >= 21) {
                disparosColisionados.add(new int[] { currentX[i], currentY[i] });
            }
        }

        ArrayList<Integer> idsDisparos = new ArrayList<>();
        for (int[] disparo : disparosColisionados) {
            int id = idDisparoEnCeldaJugador(disparo[0], disparo[1]);
            if (id == NO_ID_DISPARO) {
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
    
}