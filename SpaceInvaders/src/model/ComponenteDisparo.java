package model;

/**
 * Interfaz del patrón Composite para los disparos.
 * 
 * Flujo de disparo completo:
 * 1. Controller → JugadorBueno.disparar()
 * 2. JugadorBueno delega a Naves.disparar()
 * 3. Naves delega a Disparo.activar(x, y)
 * 4. Disparo.activar() comprueba la estrategia y crea el cuerpo (ComponenteDisparo)
 *    - CompositeDisparo: para formas como flecha o rombo
 *    - PixelDisparo: para disparo simple
 * 5. ComponenteDisparo.notificarDisparoNuevo() notifica a Espacio
 * 6. Espacio.actualizarDisparo() (cada 50ms) hace:
 *    - Llama a Disparo.subir() que mueve ComponenteDisparo
 *    - ComponenteDisparo.notificarMovimiento() notifica el movimiento a Espacio
 *    - Comprueba colisiones y notifica si sale del tablero
 * 
 * IMPORTANTE: SOLO ComponenteDisparo accede a Espacio, no sus implementadores.
 */
public interface ComponenteDisparo {
	void mover();
	boolean isActivo();
	void setActivo(boolean b);
	int getX();
	int getY();
	
	// ─── Notificación a Espacio (SOLO ComponenteDisparo accede) ───────────────
	/** Notifica a Espacio sobre el cambio de posición del disparo */
	default void notificarMovimiento(int oldX, int oldY, int newX, int newY) {
		Espacio espacio = Espacio.getEspacio();
		espacio.notificarMovimientoDisparo(oldX, oldY, newX, newY);
	}
	
	/** Notifica a Espacio cuando se dispara un nuevo proyectil */
	default void notificarDisparoNuevo() {
		Espacio espacio = Espacio.getEspacio();
		espacio.notificarDisparoNuevo(getX(), getY());
	}
}
