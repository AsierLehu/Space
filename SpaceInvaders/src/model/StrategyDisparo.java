package model;

public interface StrategyDisparo {
    // Identificar tipo: "pixel", "flecha" o "rombo"
	String getTipo();
	
	// Munición restante. -1 significa infinita
	int getMunicion();
	
	// Consume una unidad de munición. Devuelve false si no queda
	boolean gastar();
	
	// True si aún se puede disparar con esta estrategia
	boolean tieneMunicion();
}