package model;

import java.util.ArrayList;

public class Nave2 extends Naves {

	public Nave2(int x, int y, int velocidad) {
		super(x, y, velocidad);
		ArrayList<StrategyDisparo> estrategias = new ArrayList<>();
		estrategias.add(new DisparoPixel());
		estrategias.add(new DisparoRombo());
		setGestorDisparos(new Disparo(estrategias));
		inicializarNaveJugador();
	}

	@Override
	public void construir() {
		int bx = getX();
		int by = getY();
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
		return getX() + 1;
	}

	@Override
	protected int origenDisparoY() {
		return getY() - 3;
	}

	@Override
	public int[][] celdasOcupadas() {
		return new int[][] {
			{ getX(), getY() },
			{ getX() + 2, getY() },
			{ getX(), getY() + 1 },
			{ getX() + 1, getY() + 1 },
			{ getX() + 2, getY() + 1 },
			{ getX(), getY() + 2 },
			{ getX() + 1, getY() + 2 },
			{ getX() + 2, getY() + 2 }
		};
	}

	@Override
	public int getTipoNave() {
		return 2; // Azul
	}

}
