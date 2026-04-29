package model;

import java.util.ArrayList;

/**
 * Gestor del disparo del jugador: estrategias disponibles y lista de disparos activos.
 * Context del patrón Strategy: almacena las estrategias disponibles y gestiona la estrategia actual.
 */
public class Disparo {

	private static int PRIMER_ID_DISPARO = 21;

	private static int siguienteIdDisparo = PRIMER_ID_DISPARO;

	/** Reinicia la secuencia 21 … 29, 211, 212 … al iniciar una partida nueva. */
	public static void reiniciarContadorIdsDisparo() {
		siguienteIdDisparo = PRIMER_ID_DISPARO;
	}

	/**
	 * Siguiente id único por proyectil/compuesto completo (mismo número para todas las piezas).
	 */
	public static int tomarSiguienteIdDisparo() {
		int id = siguienteIdDisparo;
		if (siguienteIdDisparo < 29) {
			siguienteIdDisparo++;
		} else if (siguienteIdDisparo == 29) {
			siguienteIdDisparo = 211;
		} else {
			siguienteIdDisparo++;
		}
		return id;
	}

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
	// TODO: REVISARSE TODA ESTA LÓGICA
	public void actualizarDisparos() {
		if (disparosActivos.isEmpty()) return; // Evitar operaciones en lista vacía

		// Orden crítico: proyectiles con menor Y (más arriba en pantalla) deben moverse primero.
		// Si el de abajo mueve antes, su notificación tipo 2 borra la celda "old" donde otro
		// proyectil acaba de pintarse en el mismo tick (misma columna, celdas adyacentes).
		ordenarDisparosActivosPorPosicion(disparosActivos);

		int i = 0;
		while (i < disparosActivos.size()) {
			Component disparo = disparosActivos.get(i);
			disparo.mover(0, -1, 0);
			if (!disparo.isActivo()) {
				disparosActivos.remove(disparo);
			} else {
				i++;
			}
		}
	}

	/** Proyectiles con menor Y y, a igualdad, menor X van primero. */
	private static void ordenarDisparosActivosPorPosicion(ArrayList<Component> lista) {
		int n = lista.size();
		for (int i = 1; i < n; i++) {
			Component insertion = lista.get(i);
			int j = i;
			while (j > 0 && proyectilVaAntesEnOrden(insertion, lista.get(j - 1))) {
				lista.set(j, lista.get(j - 1));
				j--;
			}
			lista.set(j, insertion);
		}
	}

	/** Devuelve true si componente a debe actualizarse antes que componente b. */
	private static boolean proyectilVaAntesEnOrden(Component a, Component b) {
		int ya = menorYProyectil(a);
		int yb = menorYProyectil(b);
		if (ya < yb) {
			return true;
		}
		if (ya > yb) {
			return false;
		}
		return menorXProyectil(a) < menorXProyectil(b);
	}

	/** Menor coordenada Y ocupada por el proyectil (parte más alta del disparo). */
	private static int menorYProyectil(Component c) {
		int minY = Integer.MAX_VALUE;
		if (c instanceof Composite comp) {
			for (int[] cel : comp.celdasOcupadasActivas()) {
				if (cel[1] < minY) {
					minY = cel[1];
				}
			}
		} else {
			minY = c.getRefY();
		}
		return minY == Integer.MAX_VALUE ? 0 : minY;
	}

	private static int menorXProyectil(Component c) {
		int minX = Integer.MAX_VALUE;
		if (c instanceof Composite comp) {
			for (int[] cel : comp.celdasOcupadasActivas()) {
				if (cel[0] < minX) {
					minX = cel[0];
				}
			}
		} else {
			minX = c.getRefX();
		}
		return minX == Integer.MAX_VALUE ? 0 : minX;
	}

	/** Devuelve la lista de disparos activos como Components */
	public ArrayList<Component> getDisparosActivos() {
		return disparosActivos;
	}


	public int[][] celdasOcupadas() {
		ArrayList<int[]> todasLasCeldas = new ArrayList<>();
		
		for (Component disparo : disparosActivos) {
			if (disparo instanceof Composite comp) {
				todasLasCeldas.addAll(comp.celdasOcupadasActivas());
			} else {
				todasLasCeldas.add(new int[] { disparo.getRefX(), disparo.getRefY() });
			}
		}
		return todasLasCeldas.toArray(new int[0][]);
	}
	
	/** Elimina de la lista el proyectil con el id indicado (compuesto completo mismo id). */
	public void eliminarDisparoPorId(int disparoId) {
		for (int i = disparosActivos.size() - 1; i >= 0; i--) {
			Component disparo = disparosActivos.get(i);
			if (disparo.getDisparoId() == disparoId) {
				disparo.setActivo(false);
				disparosActivos.remove(i);
				return;
			}
		}
	}
}
