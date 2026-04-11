package model;

import java.util.ArrayList;

public abstract class Naves {

	protected int x;
	protected int y;
	protected int velocidad;
	protected boolean vivo;

	protected Component nave;
	protected ArrayList<Disparo> disparos;
	private int indiceEstrategia = 0;

	public Naves(int x, int y, int velocidad) {
		this.x = x;
		this.y = y;
		this.velocidad = velocidad;
		this.vivo = true;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isVivo() {
		return vivo;
	}

	public void setVivo(boolean b) {
		this.vivo = b;
	}

	public abstract void construir();

	public abstract ArrayList<StrategyDisparo> getEstrategiasPermitidas();

	public abstract int[][] celdasOcupadas();

	protected int origenDisparoX() {
		return x;
	}

	protected int origenDisparoY() {
		return y - 3;
	}

	protected void inicializarNaveJugador() {
		this.nave = new Composite();
		construir();
		this.x = nave.getRefX();
		this.y = nave.getRefY();
		this.disparos = new ArrayList<>();
	}

	protected void anadirComponente(Component componente) {
		if (nave instanceof Composite raiz) {
			raiz.addComponent(componente);
		}
	}

	public boolean disparar() {
		ArrayList<StrategyDisparo> estrategias = getEstrategiasPermitidas();
		if (estrategias == null || estrategias.isEmpty()) {
			return false;
		}
		StrategyDisparo estrategiaActual = estrategias.get(indiceEstrategia);
		if (estrategiaActual.tieneMunicion()) {
			Disparo nuevoDisparo = new Disparo(origenDisparoX(), origenDisparoY(), estrategiaActual);
			if (nuevoDisparo.activar(origenDisparoX(), origenDisparoY())) {
				disparos.add(nuevoDisparo);
				return true;
			}
		}
		return false;
	}

	public void cambiarTipoDisparo() {
		ArrayList<StrategyDisparo> estrategias = getEstrategiasPermitidas();
		if (estrategias == null) {
			return;
		}
		int intentos = 0;
		do {
			indiceEstrategia = (indiceEstrategia + 1) % estrategias.size();
			intentos++;
		} while (!estrategias.get(indiceEstrategia).tieneMunicion()
				&& intentos < estrategias.size());
	}

	public ArrayList<Disparo> getDisparos() {
		return disparos;
	}

	public Component getComponente() {
		return nave;
	}

	public void mover(int dx, int dy) {
		int edx = dx * velocidad;
		int edy = dy * velocidad;
		if (nave != null) {
			nave.mover(edx, edy);
			x = nave.getRefX();
			y = nave.getRefY();
		} else {
			x += edx;
			y += edy;
		}
	}
}
