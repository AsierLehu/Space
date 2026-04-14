package model;

/**
 * Gestor del disparo del jugador: estrategia y cuerpo como Component (Composite o Pixel proyectil).
 * Context del patrón Strategy: referencia a {@link StrategyDisparo} y cambio de estrategia.
 */
public class Disparo {

	private Component cuerpo;
	private boolean activo;
	private StrategyDisparo estrategia;

	/**
	 * Crea el proyectil sin cuerpo en tablero hasta {@link #activar(int, int)}: así solo se construye la
	 * geometría una vez, al disparar (munición y posición de salida).
	 */
	public Disparo(StrategyDisparo estrategia) {
		this.estrategia = estrategia;
		this.activo = false;
	}

	public String getTipoActual() {
		return estrategia.getTipo();
	}

	public int getMunicionActual() {
		return estrategia.getMunicion();
	}

	public void setEstrategia(StrategyDisparo nueva) {
		this.estrategia = nueva;
	}

	public boolean activar(int origenX, int origenY) {
		if (estrategia.tieneMunicion()) {
			estrategia.gastar();
			cuerpo = estrategia.construirCuerpo(origenX, origenY);
			this.activo = true;
			cuerpo.notificarDisparoNuevo();
			return true;
		}
		return false;
	}

	public void subir() {
		if (activo) {
			cuerpo.mover(0, -1);
			if (!cuerpo.isActivo()) {
				activo = false;
			}
		}
	}

	public int getX() {
		return cuerpo.getRefX();
	}

	public int getY() {
		return cuerpo.getRefY();
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean b) {
		this.activo = b;
		if (cuerpo != null) {
			cuerpo.setActivo(b);
		}
	}

	public int[][] celdasOcupadas() {
		if (cuerpo instanceof Composite comp) {
			java.util.List<int[]> lista = comp.celdasOcupadasActivas();
			return lista.toArray(new int[0][]);
		}
		return new int[][] { { cuerpo.getRefX(), cuerpo.getRefY() } };
	}
}
