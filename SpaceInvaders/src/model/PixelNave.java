package model;

/**
 * Hoja del patrón Composite: un píxel individual de la nave.
 * 
 * Responsabilidades:
 * - Mantener su posición absoluta en el tablero
 * - Comprobar límites del tablero antes de moverse
 * - Notificar a {@link Espacio} cuando cambia de posición
 * 
 * Nota: Usa el método default de {@link ComponenteNave#notificarMovimiento(int, int, int, int)}
 * para notificar sin tener acceso directo a Espacio.
 */
public class PixelNave implements ComponenteNave {
    private int x;
    private int y;

    public PixelNave(int x, int y) {
    	this.x = x;
    	this.y = y;
    }
    
    @Override
    public void mover(int dx, int dy) {
    	int oldX = x;
    	int oldY = y;
    	int newX = x + dx;
    	int newY = y + dy;
    	// Límites del tablero
    	if (newX >= 0 && newX < 100 && newY >= 0 && newY < 60) {
    		x = newX;
    		y = newY;
    		// Notificar al Espacio
    		notificarMovimiento(oldX, oldY, newX, newY);
    	}
    }

	@Override
	public int getRefX() {
		return x;
	}

	@Override
	public int getRefY() {
		return y;
	}
}
