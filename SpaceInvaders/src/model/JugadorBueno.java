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
		Espacio espacio = Espacio.getEspacio();
		if (nave != null && nave.isVivo() && !espacio.isGameOver() && !espacio.isGameWon()) {
			nave.mover(dx, dy);
		}
		espacio.trasIntentoMoverJugador();
	}
	
	/**
	 * Disparo del jugador: valida, delega en la nave; el proyectil notifica al crearse y al moverse.
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
