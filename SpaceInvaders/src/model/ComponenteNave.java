package model;

/**
 * Participante del patrón Composite: contrato común para hojas ({@link PixelNave})
 * y compuestos ({@link CompositeNave}).
 * 
 * Flujo de movimiento de la nave:
 * 1. Controller → Espacio.moverJugador(dx, dy)
 * 2. Espacio delega a JugadorBueno.mover(dx, dy)
 * 3. JugadorBueno delega a Naves.mover(dx, dy)
 * 4. Naves delega a CompositeNave.mover(dx, dy)
 * 5. CompositeNave delega a PixelNave.mover(dx, dy) para cada píxel
 * 6. PixelNave comprueba límites del tablero
 * 7. PixelNave.notificarMovimiento() notifica a Espacio
 * 
 * IMPORTANTE: SOLO ComponenteNave accede a Espacio, no sus implementadores.
 */
public interface ComponenteNave {

    void mover(int dx, int dy);

    /** Punto de referencia de la nave en el tablero (p. ej. esquina o ancla). */
    int getRefX();

    int getRefY();

    // ─── Notificación a Espacio (SOLO ComponenteNave accede) ──────────────────
    /** Notifica a Espacio sobre el cambio de posición de la nave */
    default void notificarMovimiento(int oldX, int oldY, int newX, int newY) {
        Espacio espacio = Espacio.getEspacio();
        espacio.notificarMovimientoJugador(oldX, oldY, newX, newY);
    }
}
