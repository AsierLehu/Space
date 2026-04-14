package model;

import java.util.ArrayList;

public class Nave3 extends Naves {

	public Nave3(int x, int y, int velocidad) {
		super(x, y, velocidad);
		ArrayList<StrategyDisparo> estrategias = new ArrayList<>();
		estrategias.add(new DisparoPixel());
		estrategias.add(new DisparoFlecha());
		estrategias.add(new DisparoRombo());
		gestorDisparos = new Disparo(estrategias);
		inicializarNaveJugador();
	}

	@Override
	public void construir() {
		int bx = x;
		int by = y;
		anadirComponente(new Pixel(bx + 1, by));
		anadirComponente(new Pixel(bx, by + 1));
		anadirComponente(new Pixel(bx + 1, by + 1));
		anadirComponente(new Pixel(bx + 2, by + 1));
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
			{ x + 1, y },
			{ x, y + 1 },
			{ x + 1, y + 1 },
			{ x + 2, y + 1 }
		};
	}

}
