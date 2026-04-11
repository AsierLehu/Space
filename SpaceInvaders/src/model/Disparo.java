package model;

/**
 * Gestor del disparo del jugador: estrategia y cuerpo como Component (Composite o Pixel proyectil).
 */
public class Disparo {

	private Component cuerpo;
	private boolean activo;
	private StrategyDisparo estrategia;

	public Disparo(int x, int y, StrategyDisparo estrategia) {
		this.cuerpo = construirCuerpo(x, y, estrategia.getTipo());
		this.activo = false;
		this.estrategia = estrategia;
	}

	private Component construirCuerpo(int x, int y, String tipo) {
		switch (tipo) {
		case "flecha": {
			Composite comp = new Composite(true);
			comp.addComponent(new Pixel(x, y, true));
			comp.addComponent(new Pixel(x - 1, y + 1, true));
			comp.addComponent(new Pixel(x + 1, y + 1, true));
			return comp;
		}
		case "rombo": {
			Composite comp = new Composite(true);
			comp.addComponent(new Pixel(x, y, true));
			comp.addComponent(new Pixel(x - 1, y + 1, true));
			comp.addComponent(new Pixel(x, y + 1, true));
			comp.addComponent(new Pixel(x + 1, y + 1, true));
			comp.addComponent(new Pixel(x, y + 2, true));
			return comp;
		}
		default:
			return new Pixel(x, y, true);
		}
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
			cuerpo = construirCuerpo(origenX, origenY, estrategia.getTipo());
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
		cuerpo.setActivo(b);
	}

	public int[][] celdasOcupadas() {
		if (cuerpo instanceof Composite comp) {
			java.util.List<int[]> lista = comp.celdasOcupadasActivas();
			return lista.toArray(new int[0][]);
		}
		return new int[][] { { cuerpo.getRefX(), cuerpo.getRefY() } };
	}
}
