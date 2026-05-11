package model;

import java.util.ArrayList;

/**
 * Base de todas las naves: posición, {@link Composite} visual, {@link Disparo} y ciclo de vida.
 */
public abstract class Naves {

    protected int x;
    protected int y;
    private int velocidad;
    private boolean vivo;

    private Component componenteNave;
    private Disparo gestorDisparos;

    protected Naves(int x, int y, int velocidad) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
        this.vivo = true;
    }

    /** Construye la forma de la nave añadiendo píxeles al composite raíz. */
    public abstract void construir();

    /** Crea el composite sin proyectil, llama a {@link #construir()} y sincroniza x,y con el ancla. */
    public void inicializarNaveJugador() {
        this.componenteNave = new Composite(false, -1);
        construir();
        this.x = componenteNave.getRefX();
        this.y = componenteNave.getRefY();
    }

    /** Lanza un disparo desde el punto de origen apropiado para esta nave. */
    public boolean disparar() {
        if (gestorDisparos == null) return false;

        return gestorDisparos.disparar(origenDisparoX(), origenDisparoY());
    }

    /** Pasa al siguiente tipo permitido; si el actual no tiene munición, sigue hasta dar la vuelta o encontrar una con munición. */
    public void cambiarTipoDisparo() {
        if (gestorDisparos != null) {
            gestorDisparos.cambiarTipoDisparo();
        }
    }

    /** Actualiza todos los disparos de la nave (movimiento y eliminación de inactivos). */
    public void actualizarDisparos() {
        gestorDisparos.actualizarDisparos();
    }

    /** Quita de la lista el proyectil con el id asignado al disparar. */
    public void eliminarDisparoPorId(int disparoId) {
        if (gestorDisparos != null) {
            gestorDisparos.eliminarDisparoPorId(disparoId);
        }
    }

    /** Desplaza la nave aplicando velocidad y delegando en el composite cuando existe. */
    public void mover(int dx, int dy, int tipoNave) {
        int edx = dx * velocidad;
        int edy = dy * velocidad;
        if (componenteNave != null) {
            componenteNave.mover(edx, edy, tipoNave);
            x = componenteNave.getRefX();
            y = componenteNave.getRefY();
        } else {
            x += edx;
            y += edy;
        }
    }

    /** Inicia la partida llamando a {@link Component#inicializar()} sobre el composite. */
    public void inicializar() {
        if (componenteNave != null) {
            componenteNave.inicializar();
        }
    }

    /** Propaga el registro inicial del jugador en el espejo a cada píxel de la nave. */
    public void registrarPosicionInicialEnEspacio(int tipoNave) {
        componenteNave.registrarPosicionInicialJugador(tipoNave);
    }

    /** Pinta el enemigo en el espejo en la creación (delegación al composite). */
    public void registrarEnemigoEnMatrizInicial(int idEnemigo) {
        if (componenteNave != null) {
            componenteNave.registrarEnemigoEnMatrizInicial(idEnemigo);
        }
    }

    /** Añade un componente hijo al composite raíz (solo si raíz es Composite). */
    protected void anadirComponente(Component componente) {
        if (componenteNave instanceof Composite raiz) {
            raiz.addComponent(componente);
        }
    }

    public boolean isVivo() {
        return vivo;
    }

    /** Coordenada X desde la que sale el disparo (por defecto la ancla x). */
    public int origenDisparoX() {
        return x;
    }

    /** Coordenada Y desde la que sale el disparo (por defecto tres filas por encima del ancla). */
    public int origenDisparoY() {
        return y - 3;
    }

    /** Asocia el gestor de {@link Disparo} con sus estrategias a esta nave. */
    public void setGestorDisparos(Disparo gestor) {
        this.gestorDisparos = gestor;
    }
}
