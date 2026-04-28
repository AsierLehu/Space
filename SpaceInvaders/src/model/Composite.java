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

	public Composite() {
		this(false);
	}

	public Composite(boolean proyectilIndividual) {
		this.proyectilIndividual = proyectilIndividual;
	}

	public void addComponent(Component c) {
		components.add(c);
	}

	public void removeComponent(Component c) {
		components.remove(c);
	}

	public ArrayList<Component> getComponents() {
		return components;
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
				c.mover(dx, dy, 0); // Disparos no tienen tipo de nave
			}
			return;
		}
		
		// Para el jugador (tipoNave > 0): bloquear en los 4 bordes.
		// Para los enemigos (tipoNave == 0): NO bloquear aquí; si llegan al
		// borde inferior, Espacio.isGameOver() lo detecta en el tablero espejo.
		if (tipoNave > 0) {
			for (Component c : components) {
				int newX = c.getRefX() + dx;
				int newY = c.getRefY() + dy;
				if (newX < 0 || newX >= 100 || newY < 0 || newY >= 60) {
					System.out.println("LIMITE alcanzado: pixel en " + c.getRefX() + "," + c.getRefY() + " intentaba ir a " + newX + "," + newY);
					return;
				}
			}
		}

		int[] oldPositionsX = new int[components.size()];
		int[] oldPositionsY = new int[components.size()];
		for (int i = 0; i < components.size(); i++) {
			oldPositionsX[i] = components.get(i).getRefX();
			oldPositionsY[i] = components.get(i).getRefY();
		}
		
		// Para enemigos: verificar que todos los pixeles pueden moverse
		// Si alguno sale del tablero, cancelar el movimiento completo
		if (tipoNave == 0) {
			boolean todoPuedeMoverse = true;
			for (Component c : components) {
				int newX = c.getRefX() + dx;
				int newY = c.getRefY() + dy;
				if (!Espacio.getEspacio().esValidoCelda(newX, newY)) {
					todoPuedeMoverse = false;
				    break;
				}
			}
			if (!todoPuedeMoverse) return;
		}

		for (Component c : components) {
			c.mover(dx, dy, tipoNave); // Propaga el tipo de nave
		}

		Espacio espacio = Espacio.getEspacio();
		// ESTO ES USADO POR NAVES ENEMIGAS Y EL JUGADOR
		espacio.notificarMovimientoJugadorCompleto(oldPositionsX, oldPositionsY, components, tipoNave); // TODO: components deberian ser tambien coordenadas solo
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
			for (Component c : components) {
				c.setActivo(b);
			}
		}
	}

	@Override
	public void notificarDisparoNuevo() {
		if (proyectilIndividual) {
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
