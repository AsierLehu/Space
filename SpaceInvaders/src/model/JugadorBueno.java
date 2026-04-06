package model;

/**
 * Jugador (singleton) que utiliza {@link NaveFactory} para obtener instancias de naves.
 */
public class JugadorBueno {

	private static JugadorBueno miJugadorBueno;

	private String tipoNaveElegido;

	/** Nave jugable de la partida actual (creada con {@link #crearNaveParaPartida()}). */
	private Naves nave;

	private JugadorBueno() {}
	
	public static JugadorBueno getJugadorBueno() {
		if (miJugadorBueno == null) {
			miJugadorBueno = new JugadorBueno();
		}
		return miJugadorBueno;
	}

	// Selecci�n de nave
	public String getTipoNaveElegido() {
		return tipoNaveElegido;
	}

	/** Uno de: {@code "Nave1"}, {@code "Nave2"}, {@code "Nave3"}. */
	public void setTipoNaveElegido(String tipo) {
			this.tipoNaveElegido = tipo;
	}

	public boolean haElegidoNave() {
		return tipoNaveElegido != null;
	}
	
	public Naves getNave() {
		return nave;
	}

	/**
	 * Crea la nave jugable para la partida vía {@link NaveFactory} (patrón Factory +
	 * Singleton), usando {@link #tipoNaveElegido}, y la guarda en {@link #nave}.
	 */
	public boolean crearNaveParaPartida() {
		if (!haElegidoNave()) {
			return false;
		}
		nave = NaveFactory.getNaveFactory().generate(tipoNaveElegido);
		return nave != null;
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
	
	/**
	 * Disparo del jugador:
	 * 1. Valida que el jugador pueda disparar
	 * 2. Delega a la nave para que dispare
	 * 3. La notificación es manejada por {@link ComponenteDisparo#notificarDisparoNuevo()}
	 */
	public void disparar() {
		Espacio espacio = Espacio.getEspacio();
		if (nave != null && nave.isVivo()
				&& !espacio.isGameOver() && !espacio.isGameWon()) {
			nave.disparar();
		}
	}
	
	public void cambiarTipoDisparo() {
		if (nave != null && nave.isVivo()) {
			nave.cambiarTipoDisparo();
		}
	}
}
