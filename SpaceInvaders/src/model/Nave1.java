package model;

import java.util.ArrayList;


public class Nave1 extends Naves {

	public Nave1(int x, int y, int velocidad) {
		super(x, y, velocidad);
		ArrayList<StrategyDisparo> estrategias = new ArrayList<>();
		estrategias.add(new DisparoPixel());
		estrategias.add(new DisparoFlecha());
		setGestorDisparos(new Disparo(estrategias));
		inicializarNaveJugador();
	}

	@Override
	public void construir() {
		anadirComponente(new Pixel(x, y));
		anadirComponente(new Pixel(x + 2, y));
		anadirComponente(new Pixel(x, y + 1));
		anadirComponente(new Pixel(x + 1, y + 1));
		anadirComponente(new Pixel(x + 2, y + 1));
		anadirComponente(new Pixel(x - 1, y + 2));
		anadirComponente(new Pixel(x, y + 2));
		anadirComponente(new Pixel(x + 1, y + 2));
		anadirComponente(new Pixel(x + 2, y + 2));
		anadirComponente(new Pixel(x + 3, y + 2));
	}

	@Override
	public int origenDisparoX() {
		return x + 2;
	}

	@Override
	public int origenDisparoY() {
		return y - 3;
	}

	@Override
	public int[][] celdasOcupadas() {
		return new int[][] {
			{ x, y },
			{ x + 2, y },
			{ x, y + 1 },
			{ x + 1, y + 1 },
			{ x + 2, y + 1 },
			{ x - 1, y + 2 },
			{ x, y + 2 },
			{ x + 1, y + 2 },
			{ x + 2, y + 2 },
			{ x + 3, y + 2 },
		};
	}

	
}
