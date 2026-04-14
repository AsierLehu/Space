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

	/** Forma del proyectil en el tablero (Composite o Pixel). */
	Component construirCuerpo(int x, int y);
}