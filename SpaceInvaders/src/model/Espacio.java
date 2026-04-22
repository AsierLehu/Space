package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import javax.swing.Timer;

@SuppressWarnings("deprecation")
public class Espacio extends Observable {

    /** Espejo del tablero (pintado vía notificaciones). Lectura para colisión disparo-enemigo. Una celda, un valor; último cambio gana. */
    public static final int CELDA_VACIO = 0;
    public static final int CELDA_DISPARO = 1;
    public static final int CELDA_ENEMIGO = 2;
    public static final int CELDA_JUGADOR_NAVE1 = 3;
    public static final int CELDA_JUGADOR_NAVE2 = 4;
    public static final int CELDA_JUGADOR_NAVE3 = 5;

    /** Notificación a observadores: eliminar de la flota al enemigo que contiene la celda (x,y). */
    public static final int MSG_ELIMINAR_ENEMIGO = 18;

    // ─── Singleton ────────────────────────────────────────────────────────────
    private static Espacio miEspacio;

    // ─── Estado del mundo ─────────────────────────────────────────────────────
    // La nave del jugador está en JugadorBueno#getNave()
    private static int anchura = 100;
    private static int altura  = 60;

    /** [x][y]: copia del estado visual según las mismas notificaciones que la vista. */
    private int[][] tablero;

    // Timer del juego
    private Timer gameTimer;
    private int frameCount;
    private boolean gameOver;

    /**
     * Durante {@link #notificarMovimientoDisparo}: la celda destino tenía enemigo en el espejo
     * (disparo sube hacia enemigo). Se consume en {@link #actualizarDisparo()}.
     */
    private boolean colisionDetectadaEnDisparoSubir;
    
    /** ID del enemigo detectado durante la colisión en movimiento de disparo. */
    private int enemigoIdColision;

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

    private Naves getNaveJugador() {
        return JugadorBueno.getJugadorBueno().getNave();
    }


    public int getAnchura() { return anchura; }
    public int getAltura()  { return altura;  }

    /** Lectura del espejo; fuera de rango o antes de la primera partida devuelve {@link #CELDA_VACIO}. */
    public int getCelda(int x, int y) {
        if (tablero == null || !esValidoCelda(x, y)) {
            return CELDA_VACIO;
        }
        return tablero[x][y];
    }

    /** Crea el espejo con el tamaño exacto del tablero de juego y lo deja vacío. */
    private void inicializarTablero() {
        int ancho = getAnchura();
        int alto = getAltura();
        tablero = new int[ancho][alto];
        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {
                tablero[x][y] = CELDA_VACIO;
            }
        }
    }

    private boolean esValidoCelda(int x, int y) {
        return x >= 0 && x < getAnchura() && y >= 0 && y < getAltura();
    }

    /** Verifica si un valor representa un ID de enemigo (>= 11). */
    private boolean esEnemigoId(int valor) {
        return valor >= 11;
    }

    /** Método público para que FlotaEnemigos pueda limpiar celdas. */
    public void limpiarCelda(int x, int y) {
        setCeldaEspejo(x, y, CELDA_VACIO);
    }

    /** Método público para que FlotaEnemigos pueda hacer notificaciones. */
    public void notificarCambio(int[] mensaje) {
        setChanged();
        notifyObservers(mensaje);
    }

    private void setCeldaEspejo(int x, int y, int tipo) {
        if (esValidoCelda(x, y)) {
            tablero[x][y] = tipo;
        }
    }

    private static int tipoNaveACeldaJugador(int tipoNave) {
        switch (tipoNave) {
            case 1: return CELDA_JUGADOR_NAVE1;
            case 2: return CELDA_JUGADOR_NAVE2;
            case 3: return CELDA_JUGADOR_NAVE3;
            default: return CELDA_JUGADOR_NAVE1;
        }
    }

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


 

    // ─── Actualización del disparo ────────────────────────────────────────────

    /**
     * Llamado cada 50 ms: mueve el disparo. Colisión disparo-enemigo solo vía el espejo del tablero
     */
    public void actualizarDisparo() {
        if (getNaveJugador() == null) return;

        ArrayList<Component> disparos = getNaveJugador().getDisparos();
        ArrayList<Component> disparosParaMantener = new ArrayList<>();

        for (Component d : disparos) {
            colisionDetectadaEnDisparoSubir = false;
            enemigoIdColision = 0;

            if (detectarColisionMatrizAntesMover(d)) {
                resolverImpactoDisparoConEnemigoSuperposicion(d);
                if (isGameWon()) {
                    notificarVictoria();
                }
                continue;
            }

            d.mover(0, -1, 0);

            if (colisionDetectadaEnDisparoSubir) {
                resolverImpactoDisparoConEnemigoMovimiento(d);
                if (isGameWon()) {
                    notificarVictoria();
                }
                continue;
            }

            if (!d.isActivo()) {
                int[][] celdasDisparo = getCeldasOcupadas(d);
                for (int[] celda : celdasDisparo) {
                    setCeldaEspejo(celda[0], celda[1], CELDA_VACIO);
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
    private boolean detectarColisionMatrizAntesMover(Component d) {
        for (int[] celda : getCeldasOcupadas(d)) {
            if (esEnemigoId(getCelda(celda[0], celda[1]))) {
                return true;
            }
        }
        return false;
    }

    private void resolverImpactoDisparoConEnemigoSuperposicion(Component d) {
        int[][] celdasDisparo = getCeldasOcupadas(d);
        d.setActivo(false);
        // Borrar visual del disparo y notificar eliminación de enemigos en cada celda con colisión
        for (int[] celda : celdasDisparo) {
            if (esEnemigoId(getCelda(celda[0], celda[1]))) {
                // Primero notificar eliminación del enemigo (antes de limpiar la celda)
                notificarFlotaEliminarEnemigo(celda[0], celda[1]);
                // Luego limpiar el disparo
                setCeldaEspejo(celda[0], celda[1], CELDA_VACIO);
                setChanged();
                notifyObservers(new int[] {3, celda[0], celda[1]});  // borrar disparo
            } else {
                setCeldaEspejo(celda[0], celda[1], CELDA_VACIO);
                setChanged();
                notifyObservers(new int[] {3, celda[0], celda[1]});
            }
        }
    }

    private void resolverImpactoDisparoConEnemigoMovimiento(Component d) {
        int[][] celdasDisparo = getCeldasOcupadas(d);
        d.setActivo(false);
        
        // Usar el ID del enemigo guardado durante la detección de colisión
        if (esEnemigoId(enemigoIdColision)) {
            notificarFlotaEliminarEnemigoConId(enemigoIdColision);
            enemigoIdColision = 0; // Resetear para evitar reutilización
        }
        
        // Luego borrar visual del disparo
        for (int[] celda : celdasDisparo) {
            setCeldaEspejo(celda[0], celda[1], CELDA_VACIO);
            setChanged();
            notifyObservers(new int[] {3, celda[0], celda[1]});
        }
    }

    /** Elimina un enemigo usando directamente su ID. */
    private void notificarFlotaEliminarEnemigoConId(int enemigoId) {
        // Buscar todas las celdas en la matriz que tengan este mismo ID
        java.util.List<int[]> celdasDelEnemigo = new java.util.ArrayList<>();
        for (int i = 0; i < getAnchura(); i++) {
            for (int j = 0; j < getAltura(); j++) {
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
            setCeldaEspejo(celda[0], celda[1], CELDA_VACIO);
            setChanged();
            notifyObservers(new int[] {12, celda[0], celda[1]});
        }
    }
    
    /** Elimina un enemigo: busca todas las celdas con su ID en la matriz y las borra. */
    private void notificarFlotaEliminarEnemigo(int x, int y) {
        int enemigoId = getCelda(x, y); // Obtener el ID del enemigo desde la matriz
        if (esEnemigoId(enemigoId)) {
            // Buscar todas las celdas en la matriz que tengan este mismo ID
            java.util.List<int[]> celdasDelEnemigo = new java.util.ArrayList<>();
            for (int i = 0; i < getAnchura(); i++) {
                for (int j = 0; j < getAltura(); j++) {
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
                setCeldaEspejo(celda[0], celda[1], CELDA_VACIO);
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
                    setCeldaEspejo(cel[0], cel[1], CELDA_VACIO);
                    setChanged();
                    notifyObservers(new int[] {3, cel[0], cel[1]});
                }
            }
        }
        // Identificar enemigos únicos impactados
        java.util.Set<Integer> enemigosImpactados = new java.util.HashSet<>();
        for (int[] hit : celdasDisparoImpactadas) {
            int enemigoId = getCelda(hit[0], hit[1]);
            if (esEnemigoId(enemigoId)) {
                enemigosImpactados.add(enemigoId);
            }
        }
        
        // Para cada enemigo impactado, buscar todas sus celdas en la matriz y eliminarlas
        for (Integer enemigoId : enemigosImpactados) {
            // Buscar todas las celdas en la matriz que tengan este mismo ID
            java.util.List<int[]> celdasDelEnemigo = new java.util.ArrayList<>();
            for (int i = 0; i < getAnchura(); i++) {
                for (int j = 0; j < getAltura(); j++) {
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
                setCeldaEspejo(celda[0], celda[1], CELDA_VACIO);
                setChanged();
                notifyObservers(new int[] {12, celda[0], celda[1]});
            }
        }
        
        // Luego limpiar todas las celdas de disparo impactadas
        for (int[] hit : celdasDisparoImpactadas) {
            setCeldaEspejo(hit[0], hit[1], CELDA_VACIO);
            setChanged();
            notifyObservers(new int[] {3, hit[0], hit[1]}); // borrar disparo
        }
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
        
        // Procesar cada enemigo usando iteración por índice hacia atrás para eliminación segura
        for (int i = enemigos.size() - 1; i >= 0; i--) {
            // Verificar que el índice aún sea válido (por si se eliminó un enemigo)
            if (i >= enemigos.size()) continue;
            
            Enemigo enemigo = enemigos.get(i);
            if (!enemigo.isVivo()) continue;
            
            int enemigoId = enemigo.getId();
            int[][] celdasActuales = enemigo.celdasOcupadas();
            
            // Calcular nuevas posiciones (mover hacia abajo)
            java.util.List<int[]> nuevasPosiciones = new java.util.ArrayList<>();
            boolean llegaAlFondo = false;
            
            for (int[] celda : celdasActuales) {
                int nuevaY = celda[1] + 1;
                if (nuevaY >= getAltura()) {
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
            java.util.List<int[]> colisionesConDisparos = new java.util.ArrayList<>();
            for (int[] nuevaPos : nuevasPosiciones) {
                if (getCelda(nuevaPos[0], nuevaPos[1]) == CELDA_DISPARO) {
                    colisionesConDisparos.add(nuevaPos);
                }
            }
            
            if (!colisionesConDisparos.isEmpty()) {
                // Hay colisión con disparo - eliminar enemigo
                java.util.List<int[]> celdasActualesList = java.util.Arrays.asList(celdasActuales);
                eliminarEnemigoPorColisionEnMovimiento(enemigoId, celdasActualesList, colisionesConDisparos);
            } else {
                // Mover enemigo físicamente (actualiza su posición interna)
                enemigo.mover(0, 1, 0); // Mover 1 píxel hacia abajo
                
                // Actualizar matriz visual
                java.util.List<int[]> celdasActualesList = java.util.Arrays.asList(celdasActuales);
                moverEnemigoEnMatriz(enemigoId, celdasActualesList, nuevasPosiciones);
            }
        }

        if (isGameOver()) {
            notificarGameOver();
        }
    }
    
    /** Elimina un enemigo que colisiona con disparo durante su movimiento. */
    private void eliminarEnemigoPorColisionEnMovimiento(int enemigoId, java.util.List<int[]> celdasActuales, java.util.List<int[]> colisionesConDisparos) {
        // Limpiar todas las celdas actuales del enemigo
        for (int[] celda : celdasActuales) {
            setCeldaEspejo(celda[0], celda[1], CELDA_VACIO);
            setChanged();
            notifyObservers(new int[]{12, celda[0], celda[1]}); // borrar enemigo
        }
        
        // Limpiar disparos que colisionaron
        for (int[] colision : colisionesConDisparos) {
            setCeldaEspejo(colision[0], colision[1], CELDA_VACIO);
            setChanged();
            notifyObservers(new int[]{3, colision[0], colision[1]}); // borrar disparo
        }
        
        // Notificar a FlotaEnemigos para eliminar de la lista
        setChanged();
        notifyObservers(new int[]{MSG_ELIMINAR_ENEMIGO, enemigoId, -1, -1});
    }
    
    /** Mueve un enemigo en la matriz de una posición a otra. */
    private void moverEnemigoEnMatriz(int enemigoId, java.util.List<int[]> celdasActuales, java.util.List<int[]> nuevasPosiciones) {
        // Limpiar posiciones anteriores
        for (int[] celda : celdasActuales) {
            setCeldaEspejo(celda[0], celda[1], CELDA_VACIO);
            setChanged();
            notifyObservers(new int[]{12, celda[0], celda[1]}); // borrar píxel anterior
        }
        
        // Establecer nuevas posiciones con el ID del enemigo
        for (int[] nuevaPos : nuevasPosiciones) {
            setCeldaEspejo(nuevaPos[0], nuevaPos[1], enemigoId);
            setChanged();
            notifyObservers(new int[]{14, nuevaPos[0], nuevaPos[1]}); // pintar enemigo
        }
    }
    

    // ─── Notificaciones ───────────────────────────────────────────────────────

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
                setCeldaEspejo(c.getRefX(), c.getRefY(), celdaJugador);
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
                        setCeldaEspejo(c.getRefX(), c.getRefY(), enemigoId);
                        setChanged();
                        notifyObservers(new int[] {14, c.getRefX(), c.getRefY()});
                    }
                }
                else {
                    setCeldaEspejo(componenteEnemigo.getRefX(), componenteEnemigo.getRefY(), enemigoId);
                    setChanged();
                    notifyObservers(new int[] {14, componenteEnemigo.getRefX(), componenteEnemigo.getRefY()});
                }
            }
        }
    }

    public void notificarMovimientoJugadorCompleto(int[] oldX, int[] oldY, java.util.List<Component> componentes) {
        // Método de compatibilidad - obtiene el tipo de nave consultando a JugadorBueno
        Naves naveJugador = JugadorBueno.getJugadorBueno().getNave();
        int tipoNave = (naveJugador != null) ? naveJugador.getTipoNave() : 0;
        notificarMovimientoJugadorCompleto(oldX, oldY, componentes, tipoNave);
    }
    
    public void notificarMovimientoJugadorCompleto(int[] oldX, int[] oldY, java.util.List<Component> componentes, int tipoNave) {
        // Primero borra todas las celdas antiguas
        if (!this.isGameOver() && !this.isGameWon()) {
            int celdaJugador = tipoNaveACeldaJugador(tipoNave);
            for (int i = 0; i < oldX.length; i++) {
                setCeldaEspejo(oldX[i], oldY[i], CELDA_VACIO);
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
                setCeldaEspejo(c.getRefX(), c.getRefY(), celdaJugador);
                setChanged();
                notifyObservers(new int[] {tipoMensaje, c.getRefX(), c.getRefY()});
            }
        }
    }

    public void notificarMuerteJugador(int[][] posiciones) {
        if (gameOver) return;

        gameOver = true;
        for (int[] posicion : posiciones) {
            setCeldaEspejo(posicion[0], posicion[1], CELDA_VACIO);
            setChanged();
            notifyObservers(new int[] {10, posicion[0], posicion[1]});
        }
        notificarGameOver();
    }

    /** Invocado desde el modelo al cambiar de celda del jugador; dispara el Observer de la vista. */
    public void notificarMovimientoJugador(int oldX, int oldY, int newX, int newY) {
        Naves n = getNaveJugador();
        int celdaJ = (n != null) ? tipoNaveACeldaJugador(n.getTipoNave()) : CELDA_JUGADOR_NAVE1;
        setCeldaEspejo(oldX, oldY, CELDA_VACIO);
        setCeldaEspejo(newX, newY, celdaJ);
        setChanged();
        notifyObservers(new int[] {0, oldX, oldY, newX, newY});
    }

    /** Invocado cuando se crea un proyectil en pantalla. */
    public void notificarDisparoNuevo(int x, int y) {
        if (!isGameOver() && !isGameWon()) {
            setCeldaEspejo(x, y, CELDA_DISPARO);
            setChanged();
            notifyObservers(new int[] {1, x, y});
        }
    }

    /** Invocado cuando el proyectil se mueve. */
    public void notificarMovimientoDisparo(int oldX, int oldY, int newX, int newY) {
        if (!isGameOver() && !isGameWon()) {
            setCeldaEspejo(oldX, oldY, CELDA_VACIO);
            if (esValidoCelda(newX, newY)) {
                int valorCelda = getCelda(newX, newY);
                if (esEnemigoId(valorCelda)) {
                    if (!colisionDetectadaEnDisparoSubir) {
                        colisionDetectadaEnDisparoSubir = true;
                        enemigoIdColision = valorCelda; // Guardar el ID antes de sobrescribir
                    }
                }
                setCeldaEspejo(newX, newY, CELDA_DISPARO);
            }
            setChanged();
            notifyObservers(new int[] {2, oldX, oldY, newX, newY});
        }
    }

    /** Cuando el disparo sale del tablero (durante actualizarDisparo). */
    private void notificarDisparoFueraDeTablero(int oldX, int oldY) {
        setCeldaEspejo(oldX, oldY, CELDA_VACIO);
        setChanged();
        notifyObservers(new int[] {3, oldX, oldY});
    }

    /** Cuando un enemigo se mueve: borra píxeles viejos; si el nuevo trazo choca con disparo en el espejo, impacto. */
    private void notificarMovimientoEnemigo(Enemigo e, ArrayList<int[]> oldPixels, ArrayList<int[]> newPixels) {
        for (int[] pixel : oldPixels) {
            setCeldaEspejo(pixel[0], pixel[1], CELDA_VACIO);
            setChanged();
            notifyObservers(new int[] {12, pixel[0], pixel[1]});
        }
        ArrayList<int[]> celdasDisparoImpactadas = new ArrayList<>();
        for (int[] np : newPixels) {
            if (getCelda(np[0], np[1]) == CELDA_DISPARO) {
                celdasDisparoImpactadas.add(np);
            }
        }
        if (!celdasDisparoImpactadas.isEmpty()) {
            resolverImpactoEnemigoConDisparo(celdasDisparoImpactadas);
            if (isGameWon()) {
                notificarVictoria();
            }
            return;
        }
        for (int[] pixel : newPixels) {
            setCeldaEspejo(pixel[0], pixel[1], e.getId());
            setChanged();
            notifyObservers(new int[] {14, pixel[0], pixel[1]});
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
