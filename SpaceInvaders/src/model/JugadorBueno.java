package model;

/**
 * Jugador (singleton) que utiliza {@link NaveFactory} para obtener instancias de naves.
 */
public class JugadorBueno {

	private static JugadorBueno instancia;

	private String tipoNaveElegido = "Nave1";

	/** Nave jugable de la partida actual (creada con {@link #crearNaveParaPartida()}). */
	private Jugador nave;

	private JugadorBueno() {
	}

	public String getTipoNaveElegido() {
		return tipoNaveElegido;
	}

	/** Uno de: {@code "Nave1"}, {@code "Nave2"}, {@code "Nave3"}. */
	public void setTipoNaveElegido(String tipo) {
			this.tipoNaveElegido = tipo;
	}

	public Jugador getNave() {
		return nave;
	}

	/**
	 * Crea la nave jugable para la partida vía {@link NaveFactory} (patrón Factory +
	 * Singleton), usando {@link #tipoNaveElegido}, y la guarda en {@link #nave}.
	 */
	public Jugador crearNaveParaPartida() {
		nave = NaveFactory.getNaveFactory().generate(tipoNaveElegido);
		return nave;
	}

	public static JugadorBueno getJugadorBueno() {
		if (instancia == null) {
			instancia = new JugadorBueno();
		}
		return instancia;
	}

	/**
	 * Pide el movimiento a la nave almacenada en {@link #nave}.
	 */
	public void mover(int dx, int dy) {
		Espacio espacio = Espacio.getEspacio();
		if (nave != null && nave.isVivo() && !espacio.isGameOver() && !espacio.isGameWon()) {
			nave.mover(dx, dy);
		}
		espacio.trasIntentoMoverJugador();
	}
}
