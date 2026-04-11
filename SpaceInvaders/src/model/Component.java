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

	default void notificarMovimientoJugador(int oldX, int oldY, int newX, int newY) {
		Espacio.getEspacio().notificarMovimientoJugador(oldX, oldY, newX, newY);
	}

	default void notificarMovimientoDisparo(int oldX, int oldY, int newX, int newY) {
		Espacio.getEspacio().notificarMovimientoDisparo(oldX, oldY, newX, newY);
	}

	/** Vacío por defecto; Pixel y Composite en modo proyectil lo sustituyen. */
	default void notificarDisparoNuevo() {
	}
}
