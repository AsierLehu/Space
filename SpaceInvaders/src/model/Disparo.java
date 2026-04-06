package model;

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
			return comp;
		}
		default: // píxel
			return new PixelDisparo(x,y);
		}
	}
	
	// Identficador del tipo actual
	public String getTipoActual() {
		return estrategia.getTipo();
	}
	
	// Munición restante. -1 infinita
	public int getMunicionActual() {
		return estrategia.getMunicion();
	}
	
	// Cambiar la estrategia activa
	public void setEstrategia(StrategyDisparo nueva) {
		this.estrategia = nueva;
	}
	
	// Itenta activar el disparo de la estratega actual si queda munición
	public boolean activar(int origenX, int origenY) {
		if (!activo && estrategia.tieneMunicion()) {
			estrategia.gastar();
			cuerpo = construirCuerpo(origenX, origenY, estrategia.getTipo());
			this.activo = true;
			return true;
		}
		return false;
	}
	
	// El disparo sube 1 pÃ­xel cada llamada
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
	
	// Celdas ocupadas por el disparo -- FALTA POR IMPLEMENTAR
	public int[][] celdasOcupadas(){return null;}

}
