package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import javax.swing.Timer;

@SuppressWarnings("deprecation")
public class Espacio extends Observable {

    // ═══════════════════════════════════════════════════════════════════════════════
    // ║ CONSTANTES
    // ═══════════════════════════════════════════════════════════════════════════════
    
    /** Espejo del tablero (pintado vía notificaciones). Lectura para colisión disparo-enemigo. Una celda, un valor; último cambio gana. */
    public static final int CELDA_VACIO = 0;
    public static final int CELDA_DISPARO = 1;
    public static final int CELDA_ENEMIGO = 2;
    public static final int CELDA_JUGADOR_NAVE1 = 3;
    public static final int CELDA_JUGADOR_NAVE2 = 4;
    public static final int CELDA_JUGADOR_NAVE3 = 5;

    /** Notificación a observadores: eliminar de la flota al enemigo que contiene la celda (x,y). */
    public static final int MSG_ELIMINAR_ENEMIGO = 18;

    // ═══════════════════════════════════════════════════════════════════════════════
    // ║ SINGLETON
    // ═══════════════════════════════════════════════════════════════════════════════
    
    private static Espacio miEspacio;

    // ═══════════════════════════════════════════════════════════════════════════════
    // ║ PROPIEDADES DE ESTADO
    // ═══════════════════════════════════════════════════════════════════════════════
    
    private static int anchura = 100;
    private static int altura  = 60;

    /** [x][y]: copia del estado visual según las mismas notificaciones que la vista. */
    private int[][] tablero;

    // Timer del juego
    private Timer gameTimer;
    private int frameCount;
    private boolean gameOver;

    private boolean colisionDetectadaEnDisparoSubir;
    
    /** ID del enemigo detectado durante la colisión en movimiento de disparo. */
    private int enemigoIdColision;

    // ═══════════════════════════════════════════════════════════════════════════════
    // ║ CONSTRUCTOR Y PATRÓN SINGLETON
    // ═══════════════════════════════════════════════════════════════════════════════

    private Espacio() {
    }

    public static Espacio getEspacio() {
        if (miEspacio == null) {
            miEspacio = new Espacio();
        }
        return miEspacio;
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // ║ INICIALIZACIÓN DEL JUEGO
    // ═══════════════════════════════════════════════════════════════════════════════

    public void cambiarAMain() {
        addObserver(FlotaEnemigos.getFlotaEnemigos());
        inicializar();
        iniciarJuegoLoop();
        notificarCambioPantalla();
        notificarInicializacion();
    }

    private void inicializar() {
        gameOver = false;
        JugadorBueno.getJugadorBueno().crearNaveParaPartida();
        FlotaEnemigos.getFlotaEnemigos().inicializar(anchura);
        inicializarTablero();
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // ║ GETTERS Y PROPIEDADES
    // ═══════════════════════════════════════════════════════════════════════════════


    private Naves getNaveJugador() {
        return JugadorBueno.getJugadorBueno().getNave();
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // ║ GESTIÓN DEL TABLERO Y MATRIZ ESPEJO
    // ═══════════════════════════════════════════════════════════════════════════════

    /** Crea el espejo con el tamaño exacto del tablero de juego y lo deja vacío. */
    private void inicializarTablero() {
        int ancho = anchura;
        int alto = altura;
        tablero = new int[ancho][alto];
        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {
                tablero[x][y] = CELDA_VACIO;
            }
        }
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

    private boolean esValidoCelda(int x, int y) {
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

    // ═══════════════════════════════════════════════════════════════════════════════
    // ║ LÓGICA DE ESTADO DEL JUEGO
    // ═══════════════════════════════════════════════════════════════════════════════

    // Derrota: jugador muerto, algún enemigo llegó al límite inferior, o colisión jugador-enemigo
    public boolean isGameOver() {
        if (gameOver) return true;
        if (FlotaEnemigos.getFlotaEnemigos().algunoLlegoAbajo(altura)) {
            gameOver = true;
            return true;
        }
        return hayColisionJugadorEnemigo();
    }

    // Victoria: la flota existe y todos los enemigos han sido destruidos
    public boolean isGameWon() {
        return FlotaEnemigos.getFlotaEnemigos().todosDestruidos();
    }
    
    // COLISION JUGADOR - ENEMIGO, SE LE LLAMA DESDE isGameOver()
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

    // ═══════════════════════════════════════════════════════════════════════════════
    // ║ GESTIÓN DE DISPAROS
    // ═══════════════════════════════════════════════════════════════════════════════

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

    // ═══════════════════════════════════════════════════════════════════════════════
    // ║ GESTIÓN DE ENEMIGOS
    // ═══════════════════════════════════════════════════════════════════════════════

    // Llamado cada 200 ms: baja los enemigos 1 píxel
    public void actualizarEnemigos() {
        // Crear una copia de la lista para evitar problemas con eliminaciones durante la iteración
        ArrayList<Enemigo> enemigos = new ArrayList<>(FlotaEnemigos.getFlotaEnemigos().getEnemigos());
        
        // Procesar cada enemigo - ahora es seguro iterar normalmente
        for (Enemigo enemigo : enemigos) {
            // Solo verificar que está vivo (ya no importa si fue eliminado de la lista original)
            if (!enemigo.isVivo()) {
                continue;
            }
            
            int enemigoId = enemigo.getId();
            int[][] celdasActuales = enemigo.celdasOcupadas();
            
            // Calcular nuevas posiciones (mover hacia abajo)
            ArrayList<int[]> nuevasPosiciones = new ArrayList<>();
            boolean llegaAlFondo = false;
            
            for (int[] celda : celdasActuales) {
                int nuevaY = celda[1] + 1;
                if (nuevaY >= altura) {
                    llegaAlFondo = true;
                    break; // No necesitamos calcular más si ya llega al fondo
                }
                nuevasPosiciones.add(new int[]{celda[0], nuevaY});
            }
            
            if (llegaAlFondo) {
                // Si el enemigo llega al fondo, game over
                gameOver = true;
                break; // Salir inmediatamente del bucle
            }
            
            // Verificar colisiones con disparos en las nuevas posiciones
            ArrayList<int[]> colisionesConDisparos = new ArrayList<>();
            for (int[] nuevaPos : nuevasPosiciones) {
                if (getCelda(nuevaPos[0], nuevaPos[1]) == CELDA_DISPARO) {
                    colisionesConDisparos.add(nuevaPos);
                }
            }
            
            if (!colisionesConDisparos.isEmpty()) {
                // Hay colisión con disparo - eliminar enemigo
                ArrayList<int[]> celdasActualesList = new ArrayList<>();
                for (int[] celda : celdasActuales) {
                    celdasActualesList.add(celda);
                }
                enemigoEliminarPorColisionEnMovimiento(enemigoId, celdasActualesList, colisionesConDisparos);
            } else {
                // Mover enemigo físicamente (actualiza su posición interna)
                enemigo.mover(0, 1, 0); // Mover 1 píxel hacia abajo
                
                // Actualizar matriz visual
                ArrayList<int[]> celdasActualesList = new ArrayList<>();
                for (int[] celda : celdasActuales) {
                    celdasActualesList.add(celda);
                }
                enemigoMoverEnMatriz(enemigoId, celdasActualesList, nuevasPosiciones);
            }
        }

        if (isGameOver()) {
            notificarGameOver();
        }
    }
    
    /** Elimina un enemigo que colisiona con disparo durante su movimiento. */
    private void enemigoEliminarPorColisionEnMovimiento(int enemigoId, ArrayList<int[]> celdasActuales, ArrayList<int[]> colisionesConDisparos) {
        // Limpiar todas las celdas actuales del enemigo
        for (int[] celda : celdasActuales) {
            setCeldaMatriz(celda[0], celda[1], CELDA_VACIO);
            setChanged();
            notifyObservers(new int[]{12, celda[0], celda[1]}); // borrar enemigo
        }
        
        // Limpiar disparos que colisionaron
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

    // ═══════════════════════════════════════════════════════════════════════════════
    // ║ SISTEMA DE NOTIFICACIONES
    // ═══════════════════════════════════════════════════════════════════════════════

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

    public void notificarMovimientoJugadorCompleto(int[] oldX, int[] oldY, ArrayList<Component> componentes) {
        // Método de compatibilidad - obtiene el tipo de nave consultando a JugadorBueno
        Naves naveJugador = JugadorBueno.getJugadorBueno().getNave();
        int tipoNave = (naveJugador != null) ? naveJugador.getTipoNave() : 0;
        notificarMovimientoJugadorCompleto(oldX, oldY, componentes, tipoNave);
    }
    
    public void notificarMovimientoJugadorCompleto(int[] oldX, int[] oldY, ArrayList<Component> componentes, int tipoNave) {
        // Primero borra todas las celdas antiguas
        if (!this.isGameOver() && !this.isGameWon()) {
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

    // ═══════════════════════════════════════════════════════════════════════════════
    // ║ BUCLE PRINCIPAL DE JUEGO
    // ═══════════════════════════════════════════════════════════════════════════════

    // Tick cada 50 ms para disparos, cada 200 ms (4 ticks) para enemigos
    public void iniciarJuegoLoop() {
        frameCount = 0;
        gameTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isGameOver() && !isGameWon()) {
                    actualizarDisparos();
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
