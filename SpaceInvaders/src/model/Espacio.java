package model;
 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
 
@SuppressWarnings("deprecation")
public class Espacio extends Observable {
 
    // CONSTANTES    
    /** Espejo del tablero (pintado v�a notificaciones). Lectura para colisi�n disparo-enemigo. Una celda, un valor; �ltimo cambio gana. */
    public static int CELDA_VACIO = 0;
    public static int CELDA_DISPARO = 1;
    public static int CELDA_ENEMIGO = 2;
    public static int CELDA_JUGADOR_NAVE1 = 3;
    public static int CELDA_JUGADOR_NAVE2 = 4;
    public static int CELDA_JUGADOR_NAVE3 = 5;
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
 
    private boolean gameOver;
    private boolean gameVictoria;
 
 
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
        notificarInicializacion();
        
        // Iniciar los timers DESPU�S de que todo est� inicializado
        TimerEnemigo.getInstancia().iniciar();
        TimerDisparo.getInstancia().iniciar();
    }
 
    private void inicializar() {
        gameOver = false;
        gameVictoria = false;
        Disparo.reiniciarContadorIdsDisparo();
        FlotaEnemigos.getFlotaEnemigos().inicializar(anchura); // TODO: HACER CON EL NOTIFY
        inicializarTablero();
    }
 
    // GETTERS Y PROPIEDADES 
 
    private Naves getNaveJugador() {
        return JugadorBueno.getJugadorBueno().getNave();
    }
 
    // GESTI�N DEL TABLERO Y MATRIZ ESPEJO 
    /** Crea el espejo con el tama�o exacto del tablero de juego y lo deja vac�o. */
    private void inicializarTablero() {
        int ancho = anchura;
        int alto = altura;
        tablero = new int[ancho][alto];
        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {
                tablero[x][y] = CELDA_VACIO;
            }
        }
        
        // Inicializar enemigos en la matriz despu�s de crearla
        inicializarEnemigosEnMatriz();
    }
    
    /** Pinta todos los enemigos creados por FlotaEnemigos en la matriz. */
    private void inicializarEnemigosEnMatriz() { // TODO: AL MOVERSE LOS ENEMIGOS, QUE SE REGISTRE
        System.out.println("=== INICIALIZANDO ENEMIGOS EN MATRIZ ===");
        // Crear una copia para evitar ConcurrentModificationException
        ArrayList<Enemigo> enemigos = new ArrayList<>(FlotaEnemigos.getFlotaEnemigos().getEnemigos());
        
        for (Enemigo enemigo : enemigos) {
            if (enemigo.isVivo()) {
                int enemigoId = enemigo.getId();
                int[][] celdas = enemigo.celdasOcupadas();
                System.out.println("Inicializando enemigo ID " + enemigoId + " con " + celdas.length + " píxeles");
                
                // Pintar cada píxel del enemigo en la matriz con su ID
                for (int[] celda : celdas) {
                    setCeldaMatriz(celda[0], celda[1], enemigoId);
                    // Notificar a la vista para pintar el píxel del enemigo
                    setChanged();
                    notifyObservers(new int[]{12, celda[0], celda[1]});
                }
            }
        }
        System.out.println("=== FIN INICIALIZACIÓN ENEMIGOS ===");
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
 
    private static int tipoNaveACeldaJugador(int tipoNave) {
        switch (tipoNave) {
            case 1: return CELDA_JUGADOR_NAVE1;
            case 2: return CELDA_JUGADOR_NAVE2;
            case 3: return CELDA_JUGADOR_NAVE3;
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
        if (FlotaEnemigos.getFlotaEnemigos().algunoLlegoAbajo(altura)) { // TODO, QUE SE HAGA MIRANDO LA MATRIZ
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
 
    // Victoria: la flota existe y todos los enemigos han sido destruidos
    public boolean isGameWon() {
        if (gameVictoria) {
            return true;
        }
        if (FlotaEnemigos.getFlotaEnemigos().todosDestruidos()) { // TODO: QUE SE HAGA CONTANDO AQUI LOS ENEMIGOS
            System.out.println("Victoria detectada!");
            gameVictoria = true;
            notificarVictoria();
            return true;
        }
        return false;
    }
    
    // COLISION JUGADOR - ENEMIGO detectada usando el tablero espejo.
    private boolean hayColisionJugadorEnemigo() { // TODO: ESTO NOS LO VAMOS A FUMAR, HAY QUE BUSCAR SI EL DETECTOR DE COLISIONES AL MOVER TENDRA UNA PARTE DONDE DETECTA ESTO
        Naves j = getNaveJugador();
        if (j == null || !j.isVivo()) return false;
 
        Component componenteJugador = j.getComponente();
        if (!(componenteJugador instanceof Composite raiz)) return false;
 
        for (Component c : raiz.getComponents()) {
            int x = c.getRefX();
            int y = c.getRefY();
            // Colisión exacta: la celda del jugador coincide con un ID de enemigo
            if (esEnemigoId(getCelda(x, y))) {
                j.morirComoJugador();
                return true;
            }
        }
        return false;
    }
// TODO: REVISAR ESTO
    private boolean disparoCubriendoCelda(Component d, int x, int y) {
        if (d instanceof Composite comp) {
            for (int[] cel : comp.celdasOcupadasActivas()) {
                if (cel[0] == x && cel[1] == y) {
                    return true;
                }
            }
            return false;
        }
        return d.getRefX() == x && d.getRefY() == y;
    }

// TODO: HACERLO CON LA MATRIZ
    private int idDisparoEnCeldaJugador(int x, int y) {
        Naves nav = getNaveJugador();
        if (nav == null) {
            return NO_ID_DISPARO;
        }
        for (Component d : nav.getDisparos()) {
            if (disparoCubriendoCelda(d, x, y)) {
                return d.getDisparoId();
            }
        }
        return NO_ID_DISPARO;
    }

    /** Todas las celdas ocupadas por el proyectil del jugador con ese id (modelo actual). */
    // TODO: REVISAR ESTO, NO SE QUE HACE PERO NO DEBERIA SER ASI
    private ArrayList<int[]> celdasDelProyectilJugadorPorId(int disparoId) {
        ArrayList<int[]> celdas = new ArrayList<>();
        if (disparoId == NO_ID_DISPARO) {
            return celdas;
        }
        Naves nav = getNaveJugador();
        if (nav == null) {
            return celdas;
        }
        for (Component d : nav.getDisparos()) {
            if (d.getDisparoId() != disparoId) {
                continue;
            }
            if (d instanceof Composite comp) {
                celdas.addAll(comp.celdasOcupadasActivas());
            } else {
                celdas.add(new int[] { d.getRefX(), d.getRefY() });
            }
            break;
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
            if (esValidoCelda(cx, cy) && getCelda(cx, cy) == CELDA_DISPARO) {
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
     * Añade celdas donde la matriz tiene {@link #CELDA_DISPARO} y, según la lista del jugador,
     * pertenecen a ese {@code disparoId} (refuerzo frente a desfase modelo/espejo en un tick).
     */
    private void agregarCeldasDisparoEnMatrizQueCoincidenConId(ArrayList<int[]> celdas, int disparoId) {
        for (int i = 0; i < anchura; i++) {
            for (int j = 0; j < altura; j++) {
                if (getCelda(i, j) != CELDA_DISPARO) {
                    continue;
                }
                if (idDisparoEnCeldaJugador(i, j) != disparoId) {
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
 
    private void notificarInicializacion() { // FUTURO: esto se le debería llamar desde la inicializacion
        Naves n = getNaveJugador();
        if (n == null) return;
        
        // Obtener tipo de nave para usar el color correcto
        int tipoMensaje = 15; // Por defecto
        int tipoNave = n.getTipoNave();
        int celdaJugador = tipoNaveACeldaJugador(tipoNave);
        switch (tipoNave) {
            case 1: tipoMensaje = 15; break; // Verde (Nave1)
            case 2: tipoMensaje = 16; break; // Azul (Nave2)
            case 3: tipoMensaje = 17; break; // Morado (Nave3)
        }
        
        // Pintar la nave del jugador usando los componentes directamente
        Component naveJugadorComp = n.getComponente();
        if (naveJugadorComp instanceof Composite raiz) {
            for (Component c : raiz.getComponents()) {
                setCeldaMatriz(c.getRefX(), c.getRefY(), celdaJugador);
                setChanged();
                notifyObservers(new int[] {tipoMensaje, c.getRefX(), c.getRefY()});
            }
        }
        // TODO NO SE LE DEBERIA LLAMAR   
        // Pintar los enemigos - pintar TODOS los píxeles de cada enemigo con su ID específico
        ArrayList<Enemigo> enemigos = FlotaEnemigos.getFlotaEnemigos().getEnemigos();
        // Usar iteración por índice para evitar ConcurrentModificationException
        for (int i = 0; i < enemigos.size(); i++) {
            Enemigo e = enemigos.get(i);
            if (e.isVivo()) {
                int enemigoId = e.getId();
                Component componenteEnemigo = e.getComponente();
                if (componenteEnemigo instanceof Composite raiz) {
                    for (Component c : raiz.getComponents()) {
                        setCeldaMatriz(c.getRefX(), c.getRefY(), enemigoId);
                        setChanged();
                        notifyObservers(new int[] {14, c.getRefX(), c.getRefY()});
                    }
                }
                else {
                    setCeldaMatriz(componenteEnemigo.getRefX(), componenteEnemigo.getRefY(), enemigoId);
                    setChanged();
                    notifyObservers(new int[] {14, componenteEnemigo.getRefX(), componenteEnemigo.getRefY()});
                }
            }
        }
    }
 
    
    public void notificarMovimientoJugadorYEnemigo(int[] oldX, int[] oldY, ArrayList<Component> componentes, int tipoNave) {
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
            if (verificarColisionEnemigoDespuesMovimiento(enemigoId, componentes)) {
                // Hay colisión - eliminar enemigo y disparos
                // Primero limpiar posiciones anteriores del enemigo
                for (int i = 0; i < oldX.length; i++) {
                    setCeldaMatriz(oldX[i], oldY[i], CELDA_VACIO);
                }
                
                eliminarEnemigoYDisparo(enemigoId, componentes, oldX, oldY);
            } else {
                // Sin colisión - actualizar matriz normalmente
                // Limpiar posiciones anteriores
                for (int i = 0; i < oldX.length; i++) {
                    setCeldaMatriz(oldX[i], oldY[i], CELDA_VACIO);
                }
                
                // Establecer nuevas posiciones con el ID del enemigo
                for (Component c : componentes) {
                    setCeldaMatriz(c.getRefX(), c.getRefY(), enemigoId);
                }
                
                // Notificar MainFrame para actualizar visualización
                for (int i = 0; i < oldX.length; i++) {
                    setChanged();
                    notifyObservers(new int[] {10, oldX[i], oldY[i]}); // borrar píxel anterior
                }
                
                for (Component c : componentes) {
                    setChanged();
                    notifyObservers(new int[] {14, c.getRefX(), c.getRefY()}); // pintar píxel nuevo
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
            }
            // Luego pinta todas las celdas nuevas con el color correcto
            for (Component c : componentes) {
                setCeldaMatriz(c.getRefX(), c.getRefY(), celdaJugador);
                setChanged();
                notifyObservers(new int[] {tipoMensaje, c.getRefX(), c.getRefY()});
            }
        }
    }
 
    public void notificarMuerteJugador(int[][] posiciones) {
        if (gameOver) return;
        System.out.println("MUERTE JUGADOR llamado");
        gameOver = true;
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
            setCeldaMatriz(x, y, CELDA_DISPARO);
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
                    
                    // Notificar eliminación del enemigo a FlotaEnemigos
                    setChanged();
                    notifyObservers(new int[]{MSG_ELIMINAR_ENEMIGO, enemigoId});
                    
                    // C. Verificar victoria
                    boolean victoria = isGameWon();
                    if (victoria) {
                        notificarVictoria();
                    }
                    
                } else {
                    // Sin colisión: actualizar matriz normalmente
                    setCeldaMatriz(newX, newY, CELDA_DISPARO);
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
    private boolean verificarColisionEnemigoDespuesMovimiento(int enemigoId, ArrayList<Component> componentes) {
        // Verificar si algún píxel del enemigo coincide con un disparo
        for (Component c : componentes) {
            int valorCelda = getCelda(c.getRefX(), c.getRefY());
            if (valorCelda == CELDA_DISPARO) {
                System.out.println("¡COLISIÓN DETECTADA!");
                return true; // Colisión detectada
            }
        }
        return false; // No hay colisión
    }
    
   
    private void eliminarEnemigoYDisparo(int enemigoId, ArrayList<Component> componentes, int[] oldX, int[] oldY) {
        ArrayList<int[]> disparosColisionados = new ArrayList<>();
        for (Component c : componentes) {
            if (getCelda(c.getRefX(), c.getRefY()) == CELDA_DISPARO) {
                disparosColisionados.add(new int[] { c.getRefX(), c.getRefY() });
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

        setChanged();
        notifyObservers(new int[] { MSG_ELIMINAR_ENEMIGO, enemigoId });
    }
    
}