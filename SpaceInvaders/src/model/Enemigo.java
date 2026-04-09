package model;

import java.util.ArrayList;

/**
 * Enemigo Multipixel usando patrón Composite.
 * Forma: flecha de 3 píxeles hacia abajo
 * <p>
 * Coordenadas relativas al ancla (x,y):
 * <ul>
 *   <li>Píxel superior (punta):  (x+1, y)</li>
 *   <li>Píxeles inferiores (base):  (x, y+1), (x+1, y+1)</li>
 * </ul>
 */
public class Enemigo extends Naves {
	
	//Posici�n aleatoria en la parte superior
	public Enemigo(int x, int y) {
		super(x, y);
		inicializarNaveJugador();
	}

	@Override
	public void construir() {
		int bx = x;
		int by = y;
		
		// Píxel superior (punta de la flecha)
		nave.addComponent(new PixelNave(bx + 1, by));
		
		// Píxeles inferiores (base de la flecha)
		nave.addComponent(new PixelNave(bx, by + 1));
		nave.addComponent(new PixelNave(bx + 1, by + 1));
	}

	@Override
	public ArrayList<StrategyDisparo> getEstrategiasPermitidas() {
		return null;
	}

	@Override
	public int[][] celdasOcupadas() {
		return new int[][] {
			{ x + 1, y },
			{ x, y + 1 },
			{ x + 1, y + 1 }
		};
	}

}