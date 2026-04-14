package model;

import java.util.ArrayList;

public abstract class Naves {

	protected int x;
	protected int y;
	protected int velocidad;
	protected boolean vivo;

	protected Component ComponenteNave;
	protected ArrayList<Disparo> disparos;
	
	protected ArrayList<StrategyDisparo> estrategiasPermitidas;
	private int indiceTipoDisparo = 0;

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

	public ArrayList<StrategyDisparo> getEstrategiasPermitidas() {
		return estrategiasPermitidas;
	}

	public abstract int[][] celdasOcupadas();

	protected int origenDisparoX() {
		return x;
	}

	protected int origenDisparoY() {
		return y - 3;
	}

	protected void inicializarNaveJugador() {
		this.ComponenteNave = new Composite();
		construir();
		this.x = ComponenteNave.getRefX();
		this.y = ComponenteNave.getRefY();
		this.disparos = new ArrayList<>();
	}

	protected void anadirComponente(Component componente) {
		if (ComponenteNave instanceof Composite raiz) {
			raiz.addComponent(componente);
		}
	}

	public boolean disparar() {
		ArrayList<StrategyDisparo> estrategias = estrategiasPermitidas;
		
		StrategyDisparo estrategiaActual = estrategias.get(indiceTipoDisparo);
		if (estrategiaActual.tieneMunicion()) {
			Disparo nuevoDisparo = new Disparo(estrategiaActual);
			if (nuevoDisparo.activar(origenDisparoX(), origenDisparoY())) {
				disparos.add(nuevoDisparo);
				return true;
			}
		}
		return false;
	}

	/** Pasa al siguiente tipo permitido; si el actual no tiene munición, sigue hasta dar la vuelta o encontrar una con munición. */
	public void cambiarTipoDisparo() {
		if (estrategiasPermitidas == null || estrategiasPermitidas.isEmpty()) {
			return;
		}
		int n = estrategiasPermitidas.size();
		for (int i = 0; i < n; i++) {
			indiceTipoDisparo = indiceTipoDisparo + 1;
			if (indiceTipoDisparo >= n) {
				indiceTipoDisparo = 0;
			}
			if (estrategiasPermitidas.get(indiceTipoDisparo).tieneMunicion()) {
				break;
			}
		}
	}

	public ArrayList<Disparo> getDisparos() {
		return disparos;
	}

	public Component getComponente() {
		return ComponenteNave;
	}

	public void mover(int dx, int dy) {
		int edx = dx * velocidad;
		int edy = dy * velocidad;
		if (ComponenteNave != null) {
			ComponenteNave.mover(edx, edy);
			x = ComponenteNave.getRefX();
			y = ComponenteNave.getRefY();
		} else {
			x += edx;
			y += edy;
		}
	}
}
