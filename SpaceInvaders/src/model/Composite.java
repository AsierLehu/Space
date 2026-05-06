package model;

import java.util.ArrayList;

/**
 * Compuesto del patrón Composite: agrupa Component (normalmente Pixel).
 * esProyectil == false: raíz de nave; comprueba límites y notifica movimiento del jugador en bloque.
 * esProyectil == true: cuerpo de disparo compuesto.
 */
public class Composite implements Component {

	private ArrayList<Component> components = new ArrayList<>();
	private boolean esProyectil;
	private int disparoId = -1;

	public Composite() { //TODO ES RARO ESTO
		this(false, -1);
	}

	public Composite(boolean esProyectil, int disparoId) {
		this.esProyectil = esProyectil;
		if (esProyectil) {
			this.disparoId = disparoId;
		} else {
			this.disparoId = -1;
		}
	}

	public void addComponent(Component c) {
		components.add(c);
	}

	public void removeComponent(Component c) {
		components.remove(c);
	}


	/**
	 * Celdas ocupadas por pÃ­xeles activos (disparos); recursivo por si hubiera anidaciÃ³n.
	 */
	public ArrayList<int[]> celdasOcupadasActivas() {
		ArrayList<int[]> celdas = new ArrayList<>();
		for (Component c : components) {
			if (c instanceof Composite comp) {
				celdas.addAll(comp.celdasOcupadasActivas());
			} else if (c.isActivo()) {
				celdas.add(new int[] { c.getRefX(), c.getRefY() });
			}
		}
		return celdas;
	}

	@Override
	public void mover(int dx, int dy, int tipoNave) {
		if (esProyectil) {
			for (Component c : components) {
				if (!isActivo()) break;
				c.mover(dx, dy, 0);
			}
			return;
		}
		
		int n = components.size();
		int[] oldPositionsX = new int[n];
		int[] oldPositionsY = new int[n];
		for (int i = 0; i < n; i++) {
			Component c = components.get(i);
			oldPositionsX[i] = c.getRefX();
			oldPositionsY[i] = c.getRefY();
		}
		// APUNTE: si no es posible, no se mueve
		if (!Espacio.getEspacio().puedeAplicarMovimientoNave(oldPositionsX, oldPositionsY, dx, dy, tipoNave)) {
			return;
		}
		aplicarMovimientoFisico(dx, dy, tipoNave);
		Espacio.getEspacio().realizarMovimientoNaveConMatriz(oldPositionsX, oldPositionsY, dx, dy, tipoNave);
	}

	// APUNTE: llama a mover() de cada componente, como son pixeles, unicamente registran el cambio en la posicion
	public void aplicarMovimientoFisico(int dx, int dy, int tipoNave) {
		components.forEach(c -> c.mover(dx, dy, tipoNave));
	}
	// toma la celda mas a la izquierda de los componentes activos
	@Override
	public int getRefX() {
		if (components.isEmpty()) return 0;
		int min = components.get(0).getRefX();
		for (Component c : components) {
			min = Math.min(min, c.getRefX());
		}
		return min;
	}

	@Override
	public int getRefY() {
		if (components.isEmpty()) return 0;
		int min = components.get(0).getRefY();
		for (Component c : components) {
			min = Math.min(min, c.getRefY());
		}
		return min;
	}

	@Override
	public boolean isActivo() {
		if (esProyectil) {
			for (Component c : components) {
				if (c.isActivo()) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	@Override
	public void setActivo(boolean b) {
		if (esProyectil) {
			components.forEach(c -> c.setActivo(b));
		}
	}

	@Override
	public int getDisparoId() {
		if (esProyectil) {
			return disparoId;
		}
		return -1;
	}

	@Override
	public void notificarDisparoNuevo() {
		if (esProyectil) {
			components.forEach(Component::notificarDisparoNuevo);
		}
	}

	@Override
	public void notificarMuerteJugador() {
		int[][] posiciones = new int[components.size()][2];
		for (int i = 0; i < components.size(); i++) {
			Component componente = components.get(i);
			posiciones[i][0] = componente.getRefX();
			posiciones[i][1] = componente.getRefY();
		}
		Espacio.getEspacio().notificarMuerteJugador(posiciones);
	}

	@Override
	public void registrarPosicionInicialJugador(int tipoNave) {
		if (esProyectil || tipoNave <= 0) return;
		components.forEach(c -> c.registrarPosicionInicialJugador(tipoNave));
	}

	@Override
	public void registrarEnemigoEnMatrizInicial(int idEnemigo) {
		if (esProyectil) return;
		Espacio.getEspacio().registrarEnemigoCreadoEnConteo();
		components.forEach(c -> c.registrarEnemigoEnMatrizInicial(idEnemigo));
	}
}
