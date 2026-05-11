package model;

import java.util.Observable;
import java.util.Observer;

/**
 * Jugador (singleton) que utiliza NaveFactory para obtener instancias de naves.
 */
@SuppressWarnings("deprecation")
public class JugadorBueno implements Observer {

    private static JugadorBueno miJugadorBueno;

    private String tipoNaveElegido;
    private int tipoNaveNumero;
    private Naves nave;

    private JugadorBueno() {
    }

    public static JugadorBueno getJugadorBueno() {
        if (miJugadorBueno == null) {
            miJugadorBueno = new JugadorBueno();
        }
        return miJugadorBueno;
    }

    /** Uno de: "Nave1", "Nave2", "Nave3", "Nave4". Inicializa el juego con la nave elegida. */
    public void inicializar(String tipo) {
        this.tipoNaveElegido = tipo;
        crearNaveParaPartida();
        if (nave != null) {
            nave.inicializar();
            this.tipoNaveNumero = tipoNave();

            notificarPosicionInicialAlEspacio();
            iniciarTimerDisparos();
        }
    }

    /**
     * Crea la nave jugable para la partida vía NaveFactory (Factory + Singleton),
     * usando tipoNaveElegido, y la guarda en nave.
     */
    public boolean crearNaveParaPartida() {

        nave = NaveFactory.getNaveFactory().generate(tipoNaveElegido);
        return nave != null;
    }

    /** Pide a la nave almacenada el desplazamiento relativo al número de tipo actual. */
    public void mover(int dx, int dy) {
        if (nave != null && nave.isVivo()) {
            nave.mover(dx, dy, tipoNaveNumero);
        }
    }

    /**
     * Disparo del jugador: dispara siempre que la nave exista y esté viva.
     * La notificación a observers se valida en Espacio.
     */
    public void disparar() {
        if (nave != null && nave.isVivo()) {
            nave.disparar();
        }
    }

    /** Cambia la estrategia de disparo activa en la nave. */
    public void cambiarTipoDisparo() {
        if (nave != null && nave.isVivo()) {
            nave.cambiarTipoDisparo();
        }
    }

    /**
     * Actualiza todos los disparos de la nave: llamado por TimerDisparo.
     */
    public void actualizarDisparos() {
        if (nave != null && nave.isVivo()) {
            nave.actualizarDisparos();
        }
    }

    /** Sincroniza matriz y vista inicial del jugador vía la nave y el árbol {@link Component}. */
    public void notificarPosicionInicialAlEspacio() {
        nave.registrarPosicionInicialEnEspacio(tipoNaveNumero);
    }

    /**
     * Observer: recibe de {@link Espacio} la orden de eliminar un proyectil por id.
     */
    @Override
    public void update(Observable o, Object arg) {
        if (arg == null || !(arg instanceof int[]) || !(o instanceof Espacio)) {
            return;
        }

        int[] datos = (int[]) arg;

        if (datos.length >= 2 && datos[0] == 19) {
            int disparoId = datos[1];
            if (nave != null && nave.isVivo()) {
                nave.eliminarDisparoPorId(disparoId);
            }
        }
    }

    /** Arranca el ciclo periódico de actualización de disparos ({@link TimerDisparo}). */
    private void iniciarTimerDisparos() {
        TimerDisparo.getInstancia().iniciar();
    }

    /** Mapea el nombre de nave elegido al código numérico usado por movimiento y celda (1–4). */
    private int tipoNave() {
        switch (this.tipoNaveElegido) {
            case "Nave1":
                return 1;
            case "Nave2":
                return 2;
            case "Nave3":
                return 3;
            case "Nave4":
                return 4;
            default:
                return 0;
        }
    }
}
