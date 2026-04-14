package model;

public interface StrategyDisparo {
	// Identificar tipo: "pixel", "flecha" o "rombo"
	String getTipo();
	
	// MuniciÃ³n restante. -1 significa infinita
	int getMunicion();
	
	// Consume una unidad de municiÃ³n. Devuelve false si no queda
	boolean gastar();
	
	// True si aÃºn se puede disparar con esta estrategia
	boolean tieneMunicion();

	/** Crea un disparo en la posición indicada si hay munición disponible.
	 * Consume munición automáticamente. Devuelve null si no hay munición. */
	Component crearDisparo(int x, int y);
}