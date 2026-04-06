package model;

/**
 * Hoja del patrón Composite: un píxel individual del disparo.
 * 
 * Responsabilidades:
 * - Mantener su posición absoluta en el tablero
 * - Moverse hacia arriba (y--) cada llamada a mover()
 * - Notificar a {@link Espacio} cuando cambia de posición
 * 
 * Nota: Usa el método default de {@link ComponenteDisparo#notificarMovimiento(int, int, int, int)}
 * para notificar sin tener acceso directo a Espacio.
 */
public class PixelDisparo implements ComponenteDisparo{
	
	private int x;
	private int y;
	private boolean activo;
	
	public PixelDisparo(int x, int y) {
		this.x = x;
		this.y = y;
		this.activo = true;
	}

	@Override
	public void mover() {
		if (activo) {
			int oldY = y;
			y--;
			if (y < 0) {
				activo = false;
			}
			// Notificar al Espacio
			notificarMovimiento(x, oldY, x, y);
		}
	}

	@Override
	public boolean isActivo() {
		return activo;
	}

	@Override
	public void setActivo(boolean b) {
		this.activo = b;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}
	
	
}
