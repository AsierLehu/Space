package model;

import java.util.ArrayList;

/**
 * Enemigo multipixel (Composite). Forma en rejilla 5×3 relativa al ancla {@code (x,y)}:
 * fila superior {@code (0,0)(1,0) · (3,0)(4,0)}, media {@code (1,1)(2,1)(3,1)}, inferior {@code (2,2)}.
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

		anadirComponenteNave(new PixelNave(bx, by));
		anadirComponenteNave(new PixelNave(bx + 1, by));
		anadirComponenteNave(new PixelNave(bx + 3, by));
		anadirComponenteNave(new PixelNave(bx + 4, by));

		anadirComponenteNave(new PixelNave(bx + 1, by + 1));
		anadirComponenteNave(new PixelNave(bx + 2, by + 1));
		anadirComponenteNave(new PixelNave(bx + 3, by + 1));

		anadirComponenteNave(new PixelNave(bx + 2, by + 2));
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