package model;

import java.awt.Graphics;

public class Disparo {
	
	private int x;
	private int y;
	private boolean activo;
	private StrategyDisparo estrategia;
	
	// Array de estrategias del patrón Strategy
	private static final StrategyDisparo[] ESTRATEGIAS = {
		new DisparoPixel(),
		new DisparoFlecha(),
		new DisparoRombo()
	};
	private int indiceEstrategia = 0;
	
	// Constructor: inicializa con la primera estrategia
	public Disparo(int x, int y) {
		this.x = x;
		this.y = y;
		this.activo = false;
		this.estrategia = ESTRATEGIAS[0];
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean isActivo() {
		return activo;
	}
	
	public void setActivo(boolean b) {
		this.activo = b;
	}
	
	// El disparo sube 1 píxel cada llamada
	public void subir() {
		if (activo) {
			y--;
			// Si sale del tablero, se desactiva
			if (y<0) {
				activo = false;
			}
		}
	}

	// PATRÓN STRATEGY: Cambiar el tipo de disparo dinámicamente
	public void cambiarTipoDisparo() {
		indiceEstrategia = (indiceEstrategia + 1) % ESTRATEGIAS.length;
		estrategia = ESTRATEGIAS[indiceEstrategia];
	}

	// Disparar: delega a la estrategia actual
	public void disparar(Graphics g) {
    	if (activo && estrategia != null) {
        	estrategia.disparar(g, x, y);
    	}
	}
}
