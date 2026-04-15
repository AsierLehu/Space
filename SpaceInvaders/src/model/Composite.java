package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Compuesto del patrón Composite: agrupa Component (normalmente Pixel).
 * proyectil == false: raíz de nave; comprueba límites y notifica movimiento del jugador en bloque.
 * proyectil == true: cuerpo de disparo compuesto.
 */
public class Composite implements Component {

	private final List<Component> components = new ArrayList<>();
	private final boolean proyectil;

	public Composite() {
		this(false);
	}

	public Composite(boolean proyectil) {
		this.proyectil = proyectil;
	}

	public void addComponent(Component c) {
		components.add(c);
	}

	public void removeComponent(Component c) {
		components.remove(c);
	}

	public List<Component> getComponents() {
		return components;
	}

	/**
	 * Celdas ocupadas por píxeles activos (disparos); recursivo por si hubiera anidación.
	 */
	public List<int[]> celdasOcupadasActivas() {
		List<int[]> celdas = new ArrayList<>();
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
		if (proyectil) {
			for (Component c : components) {
				c.mover(dx, dy, 0); // Disparos no tienen tipo de nave
			}
			return;
		}

		for (Component c : components) {
			int newX = c.getRefX() + dx;
			int newY = c.getRefY() + dy;
			if (newX < 0 || newX >= 100 || newY < 0 || newY >= 60) {
				return;
			}
		}

		int[] oldPositionsX = new int[components.size()];
		int[] oldPositionsY = new int[components.size()];
		for (int i = 0; i < components.size(); i++) {
			oldPositionsX[i] = components.get(i).getRefX();
			oldPositionsY[i] = components.get(i).getRefY();
		}

		for (Component c : components) {
			c.mover(dx, dy, tipoNave); // Propaga el tipo de nave
		}

		Espacio espacio = Espacio.getEspacio();
		espacio.notificarMovimientoJugadorCompleto(oldPositionsX, oldPositionsY, components, tipoNave);
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
		if (proyectil) {
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
		if (proyectil) {
			for (Component c : components) {
				c.setActivo(b);
			}
		}
	}

	@Override
	public void notificarDisparoNuevo() {
		if (proyectil) {
			for (Component c : components) {
				c.notificarDisparoNuevo();
			}
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
}
