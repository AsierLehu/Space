package model;

import java.util.ArrayList;

/**
 * Enemigo multipixel (Composite). Forma en rejilla 5×3 relativa al ancla (x,y):
 * fila superior (0,0)(1,0) · (3,0)(4,0), media (1,1)(2,1)(3,1), inferior (2,2).
 */
public class Enemigo extends Naves {
	
	//Posici�n aleatoria en la parte superior
	public Enemigo(int x, int y, int velocidad) {
		super(x, y, velocidad);
		inicializarNaveJugador();
	}

	@Override
	public void construir() {
		int bx = x;
		int by = y;

		anadirComponente(new Pixel(bx, by));
		anadirComponente(new Pixel(bx + 1, by));
		anadirComponente(new Pixel(bx + 3, by));
		anadirComponente(new Pixel(bx + 4, by));

		anadirComponente(new Pixel(bx + 1, by + 1));
		anadirComponente(new Pixel(bx + 2, by + 1));
		anadirComponente(new Pixel(bx + 3, by + 1));

		anadirComponente(new Pixel(bx + 2, by + 2));
	}

	@Override
	public ArrayList<StrategyDisparo> getEstrategiasPermitidas() {
		return null;
	}

	@Override
	public int[][] celdasOcupadas() {
		return new int[][] {
			{ x, y },
			{ x + 1, y },
			{ x + 3, y },
			{ x + 4, y },
			{ x + 1, y + 1 },
			{ x + 2, y + 1 },
			{ x + 3, y + 1 },
			{ x + 2, y + 2 }
		};
	}

}