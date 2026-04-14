package model;

import java.util.ArrayList;

public class Nave2 extends Naves {

	public Nave2(int x, int y, int velocidad) {
		super(x, y, velocidad);
		ArrayList<StrategyDisparo> estrategias = new ArrayList<>();
		estrategias.add(new DisparoPixel());
		estrategias.add(new DisparoRombo());
		gestorDisparos = new Disparo(estrategias);
		inicializarNaveJugador();
	}

	@Override
	public void construir() {
		int bx = x;
		int by = y;
		anadirComponente(new Pixel(bx, by));
		anadirComponente(new Pixel(bx + 2, by));
		anadirComponente(new Pixel(bx, by + 1));
		anadirComponente(new Pixel(bx + 1, by + 1));
		anadirComponente(new Pixel(bx + 2, by + 1));
		anadirComponente(new Pixel(bx, by + 2));
		anadirComponente(new Pixel(bx + 1, by + 2));
		anadirComponente(new Pixel(bx + 2, by + 2));
	}

	@Override
	protected int origenDisparoX() {
		return x + 1;
	}

	@Override
	protected int origenDisparoY() {
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
			{ x, y + 2 },
			{ x + 1, y + 2 },
			{ x + 2, y + 2 }
		};
	}

}
