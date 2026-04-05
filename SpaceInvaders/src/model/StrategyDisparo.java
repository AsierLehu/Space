package model;

import java.awt.Graphics;

public interface StrategyDisparo {
    // Identificar tipo: "píxel", "flecha" o "rombo"
	String getTipo();
	
	// Munición restante. -1 significa infinita
	int getMunicion();
	
	// Consume una unidad de munición. Devuelve false si no queda
	boolean gastar();
	
	// True si aún se puede disparar con esta estrategia
	boolean tieneMunicion();
}