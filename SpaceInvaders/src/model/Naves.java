package model;

import java.util.ArrayList;

public abstract class Naves {

	protected int x;
	protected int y;
	protected int velocidad;
	protected boolean vivo;
	
	protected ComponenteNave nave;
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

	/** Inicializa la estructura interna de la nave (p. ej. componentes del patrón Composite). */
	public abstract void construir();
	
	/** Define que estrategias de disparo puede usar esta nave. Enemigo devuelve null. */
	public abstract ArrayList<StrategyDisparo> getEstrategiasPermitidas();
	
	/** Celdas del tablero que ocupa la nave. Las naves devolveran sus p�xeles del composite, el enemigo tiene forma fija de momento. */
	public abstract int[][] celdasOcupadas();
	
	// Origen X del disparo
	protected int origenDisparoX() {
		return x;
	}
	
	// Origen Y del disparo
	protected int origenDisparoY() {
		return y-3;
	}
	
	/** Inicializa el Composite y el disparo */
	protected void inicializarNaveJugador() {
		this.nave = new CompositeNave();
		construir();
		this.x = nave.getRefX();
		this.y = nave.getRefY();
		this.disparos = new ArrayList<>();
	}


	protected void anadirComponenteNave(ComponenteNave componente) {
		if (nave instanceof CompositeNave raiz) {
			raiz.addComponent(componente);
		}
	}
	
	/**
	 * Crea y añade un nuevo disparo a la lista de disparos activos.
	 * 
	 * Flujo:
	 * 1. Obtiene la estrategia activa
	 * 2. Si hay munición, crea un nuevo Disparo
	 * 3. Lo activa y lo añade a la lista de disparos
	 * 4. ComponenteDisparo notifica a {@link Espacio} mediante {@link ComponenteDisparo#notificarDisparoNuevo()}
	 * 5. Espacio actualiza el juego en el game loop ({@link Espacio#actualizarDisparo()})
	 */
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
	}
	
	public ArrayList<Disparo> getDisparos() {
		return disparos;
	}
	
	public ComponenteNave getComponenteNave() {
		return nave;
	}

	/**
	 * Si hay {@link #nave}, delega el movimiento en el componente y sincroniza {@code x}/{@code y};
	 * si no, desplaza solo las coordenadas escalares.
	 */
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
