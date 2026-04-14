package model;

/**
 * Jugador (singleton) que utiliza NaveFactory para obtener instancias de naves.
 */
public class JugadorBueno {

	private static JugadorBueno miJugadorBueno;

	private String tipoNaveElegido;

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

	/** Uno de: "Nave1", "Nave2", "Nave3". */
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
	 * Crea la nave jugable para la partida vía NaveFactory (Factory + Singleton),
	 * usando tipoNaveElegido, y la guarda en nave.
	 */
	public boolean crearNaveParaPartida() {
		
		nave = NaveFactory.getNaveFactory().generate(tipoNaveElegido);
		return nave != null;
	}

	/** Pide el movimiento a la nave almacenada en nave. */
	public void mover(int dx, int dy) {
		if (nave != null && nave.isVivo() ) {
			nave.mover(dx, dy, nave.getTipoNave());
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
	
	public void cambiarTipoDisparo() {
		if (nave != null && nave.isVivo()) {
			nave.cambiarTipoDisparo();
		}
	}
}
