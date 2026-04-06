package model;

import java.util.ArrayList;

public abstract class Naves {

	protected int x;
	protected int y;
	protected int velocidad;
	protected boolean vivo;
	private static final int VELOCIDAD_DEFAULT = 1;
	
	protected CompositeNave nave;
	protected Disparo disparo;
	private int indiceEstrategia = 0;

	// Constructor con velocidad por defecto - no estÃ¡ implementado que influya en el movimiento del enemigo
	public Naves(int x, int y) {
		this(x, y, VELOCIDAD_DEFAULT);
	}

	// Constructor con velocidad personalizada
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

	/** Inicializa la estructura interna de la nave (p. ej. componentes del patrÃ³n Composite). */
	public abstract void construir();
	
	/** Define que estrategias de disparo puede usar esta nave. Enemigo devuelve null. */
	public abstract ArrayList<StrategyDisparo> getEstrategiasPermitidas();
	
	/** Celdas del tablero que ocupa la nave. Las naves devolveran sus píxeles del composite, el enemigo tiene forma fija de momento. */
	public abstract int[][] celdasOcupadas();
	
	// Origen X del disparo
	protected int origenDisparoX() {
		return x;
	}
	
	// Origen Y del disparo
	protected int origenDisparoY() {
		return y-1;
	}
	
	/** Inicializa el Composite y el disparo */
	protected void inicializarNaveJugador() {
		this.nave = new CompositeNave();
		construir();
		this.x = nave.getRefX();
		this.y = nave.getRefY();
		ArrayList<StrategyDisparo> estrategias = getEstrategiasPermitidas();
		this.disparo = new Disparo(origenDisparoX(), origenDisparoY(), estrategias.get(0));
	}
	
	/** Intenta disparar con la estrategia activa. Devuelve false si no hay munición */
	public boolean disparar() {
		if (disparo == null) {
			return false;
		}
		return disparo.activar(origenDisparoX(), origenDisparoY());
	}
	
	/** Cambia el tipo de disparo permitida para esta nave */
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
		disparo.setEstrategia(estrategias.get(indiceEstrategia));
	}
	
	public Disparo getDisparo() {
		return disparo;
	}
}
