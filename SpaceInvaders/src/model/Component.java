package model;

/**
 * Contrato común del patrón Composite para piezas de nave y de disparo.
 * Las hojas son Pixel; los compuestos Composite.
 */
public interface Component {

	void mover(int dx, int dy, int tipoNave);

	int getRefX();

	int getRefY();

	boolean isActivo();

	void setActivo(boolean b);

	/**
	 * Id del proyectil del jugador ({@code >= 21} en partida) compartido por todos los píxeles
	 * de un mismo disparo; −1 si no es proyectil.
	 */
	default int getDisparoId() {
		return -1;
	}
	// TODO: QUITAR LO DE DEFAULT
	default void notificarMovimientoJugador(int oldX, int oldY, int newX, int newY) {
		Espacio.getEspacio().notificarMovimientoJugador(oldX, oldY, newX, newY);
	}


	default void notificarMuerteJugador() {
		Espacio.getEspacio().notificarMuerteJugador(new int[][] {
			{ getRefX(), getRefY() }
		});
	}

	/** Vacío por defecto; Pixel y Composite en modo proyectilIndividual lo sustituyen. */
	default void notificarDisparoNuevo() {
	}

	/** Inicia el juego llamando a cambiarAMain en Espacio */
	default void inicializar() {
		Espacio.getEspacio().cambiarAMain();
	}
}
