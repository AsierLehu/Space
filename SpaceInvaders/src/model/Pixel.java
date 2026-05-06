package model;

/**
 * Hoja del Composite: una celda del tablero.
 * esProyectil == false: parte de una nave (sin notificación individual al mover).
 * esProyectil == true: parte de un disparo (notifica a Espacio).
 */
public class Pixel implements Component {

	private int x;
	private int y;
	private boolean activo = true;
	private boolean esProyectil;
	private int disparoId = -1;

	

	public Pixel(int x, int y, boolean esProyectil, int disparoId) {
		this.x = x;
		this.y = y;
		this.esProyectil = esProyectil;
		if (esProyectil) {
			this.disparoId = disparoId;
		} else {
			this.disparoId = -1;
		}
		System.out.println("Pixel creado CON id=" + disparoId + " proyectil=" + esProyectil + " en " + x + "," + y);
	}

	@Override
	public void mover(int dx, int dy, int tipoNave) {
		if (esProyectil) { // si es proyectil se implementa aquí la logica :)
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
			Espacio.getEspacio().notificarMovimientoDisparo(oldX, oldY, x, y, disparoId);
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
		if (esProyectil) {
			Espacio.getEspacio().notificarDisparoNuevo(getRefX(), getRefY(), disparoId);
		}
	}

	@Override
	public void registrarPosicionInicialJugador(int tipoNave) {
		if (esProyectil || tipoNave <= 0) {
			return;
		}
		Espacio.getEspacio().registrarCeldaJugadorInicialEnMatrizYVista(getRefX(), getRefY(), tipoNave);
	}

	@Override
	public void registrarEnemigoEnMatrizInicial(int idEnemigo) {
		if (esProyectil) {
			return;
		}
		Espacio.getEspacio().registrarCeldaEnemigoInicialEnMatrizYVista(getRefX(), getRefY(), idEnemigo);
	}
	
	@Override
	public int getDisparoId() {
	    return disparoId;
	}

}
