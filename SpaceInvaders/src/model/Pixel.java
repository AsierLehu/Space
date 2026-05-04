package model;

/**
 * Hoja del Composite: una celda del tablero.
 * proyectilIndividual == false: parte de una nave (sin notificación individual al mover).
 * proyectilIndividual == true: parte de un disparo (notifica a Espacio).
 */
public class Pixel implements Component {

	private int x;
	private int y;
	private boolean activo = true;
	private boolean proyectilIndividual;
	private int disparoId = -1;

	public Pixel(int x, int y) {
		this(x, y, false, -1);
		System.out.println("Pixel creado SIN id: " + x + "," + y);
	}

	public Pixel(int x, int y, boolean proyectilIndividual, int disparoId) {
		this.x = x;
		this.y = y;
		this.proyectilIndividual = proyectilIndividual;
		if (proyectilIndividual) {
			this.disparoId = disparoId;
		} else {
			this.disparoId = -1;
		}
		System.out.println("Pixel creado CON id=" + disparoId + " proyectil=" + proyectilIndividual + " en " + x + "," + y);
	}

	@Override
	public void mover(int dx, int dy, int tipoNave) {
		if (proyectilIndividual) { // si es un unico pixel, solo se mueve el mismo, no es un conjunto :)
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
		if (proyectilIndividual) {
			Espacio.getEspacio().notificarDisparoNuevo(getRefX(), getRefY(), disparoId);
		}
	}

	@Override
	public void registrarPosicionInicialJugador(int tipoNave) {
		if (proyectilIndividual || tipoNave <= 0) {
			return;
		}
		Espacio.getEspacio().registrarCeldaJugadorInicialEnMatrizYVista(getRefX(), getRefY(), tipoNave);
	}

	@Override
	public void registrarEnemigoEnMatrizInicial(int idEnemigo) {
		if (proyectilIndividual) {
			return;
		}
		Espacio.getEspacio().registrarCeldaEnemigoInicialEnMatrizYVista(getRefX(), getRefY(), idEnemigo);
	}
	
	@Override
	public int getDisparoId() {
	    return disparoId;
	}

}
