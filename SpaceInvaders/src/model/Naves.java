package model;

import java.util.ArrayList;

public abstract class Naves {

	protected int x;
	protected int y;
	protected int velocidad;
	protected boolean vivo;

	protected Component ComponenteNave;
	protected Disparo gestorDisparos;

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

	public void morirComoJugador() {
		if (!vivo) {
			return;
		}
		vivo = false;
		ComponenteNave.notificarMuerteJugador();
	}

	public abstract void construir();

	public ArrayList<StrategyDisparo> getEstrategiasPermitidas() {
		if (gestorDisparos != null) {
			return gestorDisparos.getEstrategias();
		}
		return new ArrayList<>();
	}

	public String getTipoDisparoActual() {
		if (gestorDisparos != null) {
			return gestorDisparos.getTipoActual();
		}
		return "ninguno";
	}

	public int getMunicionDisparoActual() {
		if (gestorDisparos != null) {
			return gestorDisparos.getMunicionActual();
		}
		return 0;
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
	}

	protected void anadirComponente(Component componente) {
		if (ComponenteNave instanceof Composite raiz) {
			raiz.addComponent(componente);
		}
	}

	public boolean disparar() {
		if (gestorDisparos == null) return false;
		
		return gestorDisparos.disparar(origenDisparoX(), origenDisparoY());
	}

	/** Pasa al siguiente tipo permitido; si el actual no tiene munición, sigue hasta dar la vuelta o encontrar una con munición. */
	public void cambiarTipoDisparo() {
		if (gestorDisparos != null) {
			gestorDisparos.cambiarTipoDisparo();
		}
	}

	public ArrayList<Component> getDisparos() {
		if (gestorDisparos != null) {
			return gestorDisparos.getDisparosActivos();
		}
		return new ArrayList<>();
	}

	/** Actualiza todos los disparos de la nave (movimiento y eliminación de inactivos) */
	public void actualizarDisparos() {
		gestorDisparos.actualizarDisparos();
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
