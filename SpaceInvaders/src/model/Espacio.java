package model;
 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import javax.swing.Timer;
 
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
 
    /** Notificaci�n a observadores: eliminar de la flota al enemigo que contiene la celda (x,y). */
    public static int MSG_ELIMINAR_ENEMIGO = 18;
    
    /** Notificaci�n a observadores: eliminar disparo que colisiono con enemigo. */
    public static int MSG_ELIMINAR_DISPARO = 19;
 
    // SINGLETON    
    private static Espacio miEspacio;
 
    // PROPIEDADES DE ESTADO    
    private static int anchura = 100;
    private static int altura  = 60;
 
    /** [x][y]: copia del estado visual seg�n las mismas notificaciones que la vista. */
    private int[][] tablero;
 
    private boolean gameOver;
 
    private boolean colisionDetectadaEnDisparoSubir;
    
    /** ID del enemigo detectado durante la colisi�n en movimiento de disparo. */
    private int enemigoIdColision;
 
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
        JugadorBueno.getJugadorBueno().crearNaveParaPartida();
        FlotaEnemigos.getFlotaEnemigos().inicializar(anchura);
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
    private void inicializarEnemigosEnMatriz() {
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
 
    /** Método público para que FlotaEnemigos pueda limpiar celdas. */
    public void limpiarCelda(int x, int y) {
        setCeldaMatriz(x, y, CELDA_VACIO);
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
    		System.out.println("gameOver ya era true");
    		return true;
    	}
        if (FlotaEnemigos.getFlotaEnemigos().algunoLlegoAbajo(altura)) {
            System.out.println("GAME OVER: enemigo llego abajo");
            gameOver = true;
            return true;
        }
        if (hayColisionJugadorEnemigo()) {
            System.out.println("GAME OVER: colision jugador-enemigo");
            return true;
        }
        return false;
    }
 
    // Victoria: la flota existe y todos los enemigos han sido destruidos
    public boolean isGameWon() {
        return FlotaEnemigos.getFlotaEnemigos().todosDestruidos();
    }
    
    // COLISION JUGADOR - ENEMIGO detectada usando el tablero espejo.
    // Antes iteraba directamente sobre FlotaEnemigos.getEnemigos(), lo que
    // provocaba ConcurrentModificationException cuando el TimerEnemigo (200ms)
    // eliminaba un enemigo mientras el TimerDisparo (50ms) iteraba la lista,
    // congelando el EDT silenciosamente (bug del borde izquierdo).
    private boolean hayColisionJugadorEnemigo() {
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
 
    // GESTI�N DE DISPAROS 
    
    /**
     * Comprueba si un disparo espec�fico colisiona con alg�n enemigo.
     * @param disparo El disparo a comprobar
     * @return true si hay colisi�n, false en caso contrario
     */
    public boolean comprobarColisionDisparoEnemigo(Component disparo) {
        if (disparo == null || !disparo.isActivo()) {
            return false;
        }
        
        // Obtener las celdas ocupadas por el disparo
        int[][] celdasDisparo = getCeldasOcupadas(disparo);
        
        // Verificar si alguna celda del disparo coincide con un enemigo
        for (int[] celda : celdasDisparo) {
            int valorCelda = getCelda(celda[0], celda[1]);
            if (esEnemigoId(valorCelda)) {
                // Hay colision - notificar a los observadores
                setChanged();
                notifyObservers(new int[] { MSG_ELIMINAR_DISPARO, celda[0], celda[1] });
                
                // Tambien eliminar el enemigo
                notificarFlotaEliminarEnemigo(celda[0], celda[1]);
                
                return true;
            }
        }
        
        return false;
    }
    
    public void actualizarDisparos() {
        if (getNaveJugador() == null) return;
 
        ArrayList<Component> disparos = getNaveJugador().getDisparos();
        ArrayList<Component> disparosParaMantener = new ArrayList<>();
 
        for (Component d : disparos) {
            colisionDetectadaEnDisparoSubir = false;
            enemigoIdColision = 0;
 
            if (disparoDetectarColisionMatrizAntesMover(d)) {
                disparoResolverImpactoConEnemigoSuperposicion(d);
                if (isGameWon()) {
                    notificarVictoria();
                }
                continue;
            }
 
            d.mover(0, -1, 0);

            // Usar la nueva funcion de colision que notifica via Observer pattern
            if (comprobarColisionDisparoEnemigo(d)) {
                // La colision ya fue manejada por la funcion
                continue;
            }

            if (colisionDetectadaEnDisparoSubir) {
                disparoResolverImpactoConEnemigoMovimiento(d);
                if (isGameWon()) {
                    notificarVictoria();
                }
                continue;
            }
 
            if (!d.isActivo()) {
                int[][] celdasDisparo = getCeldasOcupadas(d);
                for (int[] celda : celdasDisparo) {
                    setCeldaMatriz(celda[0], celda[1], CELDA_VACIO);
                    setChanged();
                    notifyObservers(new int[] {3, celda[0], celda[1]});
                }
            } else {
                disparosParaMantener.add(d);
            }
        }
        getNaveJugador().getDisparos().retainAll(disparosParaMantener);
    }
 
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
 
    private void disparoResolverImpactoConEnemigoMovimiento(Component d) {
        int[][] celdasDisparo = getCeldasOcupadas(d);
        d.setActivo(false);
        
        // Usar el ID del enemigo guardado durante la detección de colisión
        if (esEnemigoId(enemigoIdColision)) {
            notificarFlotaEliminarEnemigoConId(enemigoIdColision);
            enemigoIdColision = 0; // Resetear para evitar reutilización
        }
        
        // Luego borrar visual del disparo
        for (int[] celda : celdasDisparo) {
            setCeldaMatriz(celda[0], celda[1], CELDA_VACIO);
            setChanged();
            notifyObservers(new int[] {3, celda[0], celda[1]});
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
 
    /**
     * Enemigo que baja entra en celdas con disparo en el espejo: desactiva esos disparos, borra
     * visual y notifica a la flota eliminación por cada celda impactada.
     */
    private void resolverImpactoEnemigoConDisparo(ArrayList<int[]> celdasDisparoImpactadas) {
        ArrayList<Component> disparos = getNaveJugador().getDisparos();
        for (Component d : new ArrayList<>(disparos)) {
            if (!d.isActivo()) {
                continue;
            }
            boolean tocado = false;
            for (int[] c : getCeldasOcupadas(d)) {
                for (int[] hit : celdasDisparoImpactadas) {
                    if (c[0] == hit[0] && c[1] == hit[1]) {
                        tocado = true;
                        break;
                    }
                }
                if (tocado) {
                    break;
                }
            }
            if (tocado) {
                int[][] celdasD = getCeldasOcupadas(d);
                d.setActivo(false);
                for (int[] cel : celdasD) {
                    setCeldaMatriz(cel[0], cel[1], CELDA_VACIO);
                    setChanged();
                    notifyObservers(new int[] {3, cel[0], cel[1]});
                }
            }
        }
        // Identificar enemigos únicos impactados
        ArrayList<Integer> enemigosImpactados = new ArrayList<>();
        for (int[] hit : celdasDisparoImpactadas) {
            int enemigoId = getCelda(hit[0], hit[1]);
            if (esEnemigoId(enemigoId) && !enemigosImpactados.contains(enemigoId)) {
                enemigosImpactados.add(enemigoId);
            }
        }
        
        // Para cada enemigo impactado, buscar todas sus celdas en la matriz y eliminarlas
        for (Integer enemigoId : enemigosImpactados) {
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
            
            // Borrar visualmente todas las celdas del enemigo
            for (int[] celda : celdasDelEnemigo) {
                setCeldaMatriz(celda[0], celda[1], CELDA_VACIO);
                setChanged();
                notifyObservers(new int[] {12, celda[0], celda[1]});
            }
        }
        
        // Luego limpiar todas las celdas de disparo impactadas
        for (int[] hit : celdasDisparoImpactadas) {
            setCeldaMatriz(hit[0], hit[1], CELDA_VACIO);
            setChanged();
            notifyObservers(new int[] {3, hit[0], hit[1]}); // borrar disparo
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
 
    
    public void notificarMovimientoJugadorCompleto(int[] oldX, int[] oldY, ArrayList<Component> componentes, int tipoNave) {
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
                
                eliminarEnemigoYDisparo(enemigoId, componentes);
            } else {
                // Sin colisión - actualizar matriz normalmente
                // Limpiar posiciones anteriores
                for (int i = 0; i < oldX.length; i++) {
                    setCeldaMatriz(oldX[i], oldY[i], CELDA_VACIO);
                }
                
                // NUEVO: si alguna nueva posicion esta fuera del tablero, eliminar el enemigo
                boolean fueraDeLimites = false;
                for (Component c : componentes) {
                    if (!esValidoCelda(c.getRefX(), c.getRefY())) {
                        fueraDeLimites = true;
                        break;
                    }
                }
                if (fueraDeLimites) {
                    setChanged();
                    notifyObservers(new int[]{MSG_ELIMINAR_ENEMIGO, enemigoId});
                    return;
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
            int tipoMensaje = 15; // Por defecto verde (Nave1) para casos inesperados
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
 
    /** Invocado desde el modelo al cambiar de celda del jugador; dispara el Observer de la vista. */
    public void notificarMovimientoJugador(int oldX, int oldY, int newX, int newY) {
        Naves n = getNaveJugador();
        int celdaJ = (n != null) ? tipoNaveACeldaJugador(n.getTipoNave()) : CELDA_JUGADOR_NAVE1;
        setCeldaMatriz(oldX, oldY, CELDA_VACIO);
        setCeldaMatriz(newX, newY, celdaJ);
        setChanged();
        notifyObservers(new int[] {0, oldX, oldY, newX, newY});
    }
 
    /** Invocado cuando se crea un proyectil en pantalla. */
    public void notificarDisparoNuevo(int x, int y) {
        if (!isGameOver() && !isGameWon()) {
            setCeldaMatriz(x, y, CELDA_DISPARO);
            setChanged();
            notifyObservers(new int[] {1, x, y});
        }
    }
 
    /** Invocado cuando el proyectil se mueve. */
    public void notificarMovimientoDisparo(int oldX, int oldY, int newX, int newY) {
        if (!isGameOver() && !isGameWon()) {
            setCeldaMatriz(oldX, oldY, CELDA_VACIO);
            if (esValidoCelda(newX, newY)) {
                int valorCelda = getCelda(newX, newY);
                if (esEnemigoId(valorCelda)) {
                    if (!colisionDetectadaEnDisparoSubir) {
                        colisionDetectadaEnDisparoSubir = true;
                        enemigoIdColision = valorCelda; // Guardar el ID antes de sobrescribir
                    }
                }
                setCeldaMatriz(newX, newY, CELDA_DISPARO);
            }
            setChanged();
            notifyObservers(new int[] {2, oldX, oldY, newX, newY});
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
    
   
    private void eliminarEnemigoYDisparo(int enemigoId, ArrayList<Component> componentes) {
        // Encontrar todas las posiciones de disparos que colisionaron
        ArrayList<int[]> disparosColisionados = new ArrayList<>();
        
        for (Component c : componentes) {
            if (getCelda(c.getRefX(), c.getRefY()) == CELDA_DISPARO) {
                disparosColisionados.add(new int[]{c.getRefX(), c.getRefY()});
            }
        }
        
        // 1. Limpiar píxeles de disparos de la matriz (las posiciones del enemigo ya se limpiaron antes)
        for (int[] disparo : disparosColisionados) {
            setCeldaMatriz(disparo[0], disparo[1], CELDA_VACIO);
        }
        
        // 2. Notificar MainFrame primero (borrado visual)
        // Borrar enemigo visualmente
        for (Component c : componentes) {
            setChanged();
            notifyObservers(new int[]{12, c.getRefX(), c.getRefY()}); // borrar píxel enemigo
        }
        
        // Borrar disparos visualmente
        for (int[] disparo : disparosColisionados) {
            setChanged();
            notifyObservers(new int[]{3, disparo[0], disparo[1]}); // borrar píxel disparo
        }
        
        // 3. Notificar FlotaEnemigos segundo (eliminar de lista)
        setChanged();
        notifyObservers(new int[]{MSG_ELIMINAR_ENEMIGO, enemigoId}); // MSG_ELIMINAR_ENEMIGO
    }
    
}