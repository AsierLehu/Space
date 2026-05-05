package model;

import java.util.ArrayList;

/**
 * Compuesto del patrón Composite: agrupa Component (normalmente Pixel).
 * proyectilIndividual == false: raíz de nave; comprueba límites y notifica movimiento del jugador en bloque.
 * proyectilIndividual == true: cuerpo de disparo compuesto.
 */
public class Composite implements Component {

	private ArrayList<Component> components = new ArrayList<>();
	private boolean proyectilIndividual;
	private int disparoId = -1;

	public Composite() {
		this(false, -1);
	}

	public Composite(boolean proyectilIndividual, int disparoId) {
		this.proyectilIndividual = proyectilIndividual;
		if (proyectilIndividual) {
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
		if (proyectilIndividual) {
			for (Component c : components) {
				if (!isActivo()) break;
				c.mover(dx, dy, 0);
			}
			return;
		}
		
		int[] oldPositionsX = components.stream().mapToInt(Component::getRefX).toArray();
		int[] oldPositionsY = components.stream().mapToInt(Component::getRefY).toArray();
		
		Espacio.getEspacio().intentarMovimientoNave(
			oldPositionsX, oldPositionsY, dx, dy, tipoNave, 
			() -> components.forEach(c -> c.mover(dx, dy, tipoNave))
		);
	}

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
		if (proyectilIndividual) {
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
		if (proyectilIndividual) {
			components.forEach(c -> c.setActivo(b));
		}
	}

	@Override
	public int getDisparoId() {
		if (proyectilIndividual) {
			return disparoId;
		}
		return -1;
	}

	@Override
	public void notificarDisparoNuevo() {
		if (proyectilIndividual) {
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
		if (proyectilIndividual || tipoNave <= 0) return;
		components.forEach(c -> c.registrarPosicionInicialJugador(tipoNave));
	}

	@Override
	public void registrarEnemigoEnMatrizInicial(int idEnemigo) {
		if (proyectilIndividual) return;
		Espacio.getEspacio().registrarEnemigoCreadoEnConteo();
		components.forEach(c -> c.registrarEnemigoEnMatrizInicial(idEnemigo));
	}
}
