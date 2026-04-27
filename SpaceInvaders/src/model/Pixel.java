package model;

/**
 * Hoja del Composite: una celda del tablero.
 * proyectil == false: parte de una nave (sin notificación individual al mover).
 * proyectil == true: parte de un disparo (notifica a Espacio).
 */
public class Pixel implements Component {

	private int x;
	private int y;
	private boolean activo = true;
	private boolean proyectil;

	public Pixel(int x, int y) {
		this(x, y, false);
	}

	public Pixel(int x, int y, boolean proyectil) {
		this.x = x;
		this.y = y;
		this.proyectil = proyectil;
	}

	@Override
	public void mover(int dx, int dy, int tipoNave) {
		if (proyectil) {
			if (!activo) {
				return;
			}
			int oldX = x;
			int oldY = y;
			x += dx;
			y += dy;
			if (y < 0) {
				activo = false;
			}
			notificarMovimientoDisparo(oldX, oldY, x, y);
		} else {
			int newX = x + dx;
	        int newY = y + dy;
	        if (newX >= 0 && newX < 100 && newY >= 0 && newY < 60) {
	            x = newX;
	            y = newY;
	        }
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

	@Override
	public boolean isActivo() {
		return activo;
	}

	@Override
	public void setActivo(boolean b) {
		this.activo = b;
	}

	@Override
	public void notificarDisparoNuevo() {
		if (proyectil) {
			Espacio.getEspacio().notificarDisparoNuevo(getRefX(), getRefY());
		}
	}
}
