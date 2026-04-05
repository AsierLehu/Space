package model;

/**
 * Hoja del patrón Composite: un píxel de la nave con posición absoluta en el tablero.
 */
public class PixelNave implements ComponenteNave {
    private int x;
    private int y;

    public PixelNave(int x, int y) {
    	this.x = x;
    	this.y = y;
    }
    
    @Override
    public void mover(int dx, int dy, Espacio espacio) {
    	int newX = x + dx;
    	int newY = y + dy;
    	// L�mites del tablero
    	if (newX >= 0 && newX < 100 && newY >= 0 && newY < 60) {
    		espacio.notificarMovimientoJugador(x, y, newX, newY);
    		x = newX;
    		y = newY;
    	}
    }

	@Override
	public int getRefX() {
		// TODO Auto-generated method stub
		return x;
	}

	@Override
	public int getRefY() {
		// TODO Auto-generated method stub
		return y;
	}
}
