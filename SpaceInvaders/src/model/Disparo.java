package model;

import java.util.ArrayList;

/**
 * Gestor del disparo del jugador: estrategias disponibles y lista de disparos activos.
 * Context del patrón Strategy: almacena las estrategias disponibles y gestiona la estrategia actual.
 */
public class Disparo {

	private ArrayList<Component> disparosActivos;
	private ArrayList<StrategyDisparo> estrategias;
	private int indiceEstrategiaActual;

	/**
	 * Crea el gestor de disparos con las estrategias disponibles.
	 */
	public Disparo(ArrayList<StrategyDisparo> estrategiasDisponibles) {
		this.estrategias = new ArrayList<>(estrategiasDisponibles);
		this.indiceEstrategiaActual = 0;
		this.disparosActivos = new ArrayList<>();
	}

	public String getTipoActual() {
		return estrategias.get(indiceEstrategiaActual).getTipo();
	}

	public int getMunicionActual() {
		return estrategias.get(indiceEstrategiaActual).getMunicion();
	}

	public ArrayList<StrategyDisparo> getEstrategias() {
		return estrategias;
	}

	/** Cambia al siguiente tipo de disparo disponible con munición */
	public void cambiarTipoDisparo() {
		if (estrategias.isEmpty()) return;
		
		int n = estrategias.size();
		for (int i = 0; i < n; i++) {
			indiceEstrategiaActual = (indiceEstrategiaActual + 1) % n;
			if (estrategias.get(indiceEstrategiaActual).tieneMunicion()) {
				break;
			}
		}
	}

	/** Crea un nuevo disparo usando la estrategia actual y lo añade a la lista de disparos activos */
	public boolean disparar(int origenX, int origenY) {
		if (estrategias.isEmpty()) return false;
		
		StrategyDisparo estrategiaActual = estrategias.get(indiceEstrategiaActual);
		Component nuevoDisparo = estrategiaActual.crearDisparo(origenX, origenY);
		
		if (nuevoDisparo != null) {
			disparosActivos.add(nuevoDisparo);
			nuevoDisparo.notificarDisparoNuevo();
			return true;
		}
		return false;
	}

	/** Actualiza todos los disparos activos (movimiento) y elimina los inactivos */
	public void actualizarDisparos() {
		for (int i = disparosActivos.size() - 1; i >= 0; i--) {
			Component disparo = disparosActivos.get(i);
			disparo.mover(0, -1, 0);
			if (!disparo.isActivo()) {
				disparosActivos.remove(i);
			}
		}
	}

	/** Devuelve la lista de disparos activos como Components */
	public ArrayList<Component> getDisparosActivos() {
		return disparosActivos;
	}

	/** Devuelve todas las celdas ocupadas por todos los disparos activos */
	public java.util.List<int[][]> getCeldasOcupadasTodosDisparos() {
		java.util.List<int[][]> todasLasCeldas = new ArrayList<>();
		
		for (Component disparo : disparosActivos) {
			if (disparo instanceof Composite comp) {
				java.util.List<int[]> lista = comp.celdasOcupadasActivas();
				todasLasCeldas.add(lista.toArray(new int[0][]));
			} else {
				todasLasCeldas.add(new int[][] { { disparo.getRefX(), disparo.getRefY() } });
			}
		}
		return todasLasCeldas;
	}

	/** Desactiva todos los disparos */
	public void desactivarTodosLosDisparos() {
		for (Component disparo : disparosActivos) {
			disparo.setActivo(false);
		}
		disparosActivos.clear();
	}

	public int[][] celdasOcupadas() {
		java.util.List<int[]> todasLasCeldas = new ArrayList<>();
		
		for (Component disparo : disparosActivos) {
			if (disparo instanceof Composite comp) {
				java.util.List<int[]> lista = comp.celdasOcupadasActivas();
				todasLasCeldas.addAll(lista);
			} else {
				todasLasCeldas.add(new int[] { disparo.getRefX(), disparo.getRefY() });
			}
		}
		return todasLasCeldas.toArray(new int[0][]);
	}
}
