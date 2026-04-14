package model;

/**
 * Contrato común del patrón Composite para piezas de nave y de disparo.
 * Las hojas son Pixel; los compuestos Composite.
 */
public interface Component {

	void mover(int dx, int dy);

	int getRefX();

	int getRefY();

	boolean isActivo();

	void setActivo(boolean b);

	public void notificarMovimientoJugador(int oldX, int oldY, int newX, int newY) {
		Espacio.getEspacio().notificarMovimientoJugador(oldX, oldY, newX, newY);
	}

	public void notificarMovimientoDisparo(int oldX, int oldY, int newX, int newY) {
		Espacio.getEspacio().notificarMovimientoDisparo(oldX, oldY, newX, newY);
	}

	public void notificarMuerteJugador() {
		Espacio.getEspacio().notificarMuerteJugador(new int[][] {
			{ getRefX(), getRefY() }
		});
	}

	/** Vacío por defecto; Pixel y Composite en modo proyectil lo sustituyen. */
	public void notificarDisparoNuevo() {
	}
}
