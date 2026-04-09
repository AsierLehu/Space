package model;

/**
 * Gestor del disparo del jugador.
 * 
 * Responsabilidades:
 * - Mantener la estrategia de disparo activa (rombo, flecha, pixel)
 * - Controlar la activación y desactivación del disparo
 * - Delegar el movimiento al {@link ComponenteDisparo} (patrón Composite)
 * 
 * Nota: La clase Disparo crea el cuerpo (ComponenteDisparo) inactivo en el constructor.
 * Se activa cuando {@link #activar(int, int)} es llamado.
 */
public class Disparo {
	
	private ComponenteDisparo cuerpo;
	private boolean activo;
	
	// Estrategia activa
	private StrategyDisparo estrategia;
		
	// Constructor: inicializa con la primera estrategia
	public Disparo(int x, int y, StrategyDisparo estrategia) {
		this.cuerpo = construirCuerpo(x, y, estrategia.getTipo());
		this.activo = false;
		this.estrategia = estrategia;
	}
	
	/**
	 * Construye el cuerpo del disparo según la estrategia actual.
	 * Utiliza el patrón Composite con {@link ComponenteDisparo}:
	 * 
	 * - "flecha": {@link CompositeDisparo} con 3 píxeles en forma de flecha
	 * - "rombo": {@link CompositeDisparo} con 3 píxeles en forma de rombo
	 * - "pixel": {@link PixelDisparo} simple
	 * 
	 * @param x posición X del origen del disparo
	 * @param y posición Y del origen del disparo
	 * @param tipo la estrategia de disparo activa
	 * @return el {@link ComponenteDisparo} creado
	 */
	private ComponenteDisparo construirCuerpo(int x, int y, String tipo) {
		switch (tipo) {
		case "flecha": {
			CompositeDisparo comp = new CompositeDisparo();
			comp.addComponent(new PixelDisparo(x, y));
			comp.addComponent(new PixelDisparo(x-1, y+1));
			comp.addComponent(new PixelDisparo(x+1, y+1));
			return comp;
		}
		case "rombo": {
			CompositeDisparo comp = new CompositeDisparo();
			comp.addComponent(new PixelDisparo(x, y));
			comp.addComponent(new PixelDisparo(x-1, y+1));
			comp.addComponent(new PixelDisparo(x+1, y+1));
			comp.addComponent(new PixelDisparo(x, y+2));
			return comp;
		}
		default: // p�xel
			return new PixelDisparo(x,y);
		}
	}
	
	// Identficador del tipo actual
	public String getTipoActual() {
		return estrategia.getTipo();
	}
	
	// Munici�n restante. -1 infinita
	public int getMunicionActual() {
		return estrategia.getMunicion();
	}
	
	// Cambiar la estrategia activa
	public void setEstrategia(StrategyDisparo nueva) {
		this.estrategia = nueva;
	}
	
	/**
	 * Intenta activar el disparo con la estrategia actual si queda munición.
	 * 
	 * Flujo:
	 * 1. Valida que el disparo no esté activo y haya munición
	 * 2. Gasta munición de la estrategia
	 * 3. Crea el cuerpo del disparo ({@link ComponenteDisparo}) según el tipo de estrategia:
	 *    - "flecha": {@link CompositeDisparo} con forma de flecha
	 *    - "rombo": {@link CompositeDisparo} con forma de rombo
	 *    - "pixel": {@link PixelDisparo} simple
	 * 4. Llama a {@link ComponenteDisparo#notificarDisparoNuevo()} para notificar a {@link Espacio}
	 * 
	 * @return true si el disparo fue activado exitosamente
	 */
	public boolean activar(int origenX, int origenY) {
		if (!activo && estrategia.tieneMunicion()) {
			estrategia.gastar();
			cuerpo = construirCuerpo(origenX, origenY, estrategia.getTipo());
			this.activo = true;
			// Notificar a ComponenteDisparo que hay un nuevo disparo
			cuerpo.notificarDisparoNuevo();
			return true;
		}
		return false;
	}
	
	/**
	 * Mueve el disparo un píxel hacia arriba cada tick del game loop.
	 * 
	 * El movimiento notifica automáticamente a {@link Espacio} a través de 
	 * {@link ComponenteDisparo#notificarMovimiento(int, int, int, int)}.
	 */
	public void subir() {
		if (activo) {
			cuerpo.mover();
			if (!cuerpo.isActivo()) {
				activo = false;
			}
		}
	}
	
	public int getX() {
		return cuerpo.getX();
	}
	
	public int getY() {
		return cuerpo.getY();
	}
	
	public boolean isActivo() {
		return activo;
	}
	
	public void setActivo(boolean b) {
        this.activo = b;
        cuerpo.setActivo(b);
    }
	
	// Celdas ocupadas por el disparo
	public int[][] celdasOcupadas(){
		if (cuerpo instanceof CompositeDisparo) {
			java.util.List<int[]> lista = ((CompositeDisparo) cuerpo).celdasOcupadas();
            return lista.toArray(new int[0][]);
		}
		return new int[][] {{cuerpo.getX(), cuerpo.getY() }};
	}
}
