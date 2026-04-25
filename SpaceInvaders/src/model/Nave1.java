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
		int bx = getX();
		int by = getY();
		anadirComponente(new Pixel(bx, by));
		anadirComponente(new Pixel(bx + 2, by));
		anadirComponente(new Pixel(bx, by + 1));
		anadirComponente(new Pixel(bx + 1, by + 1));
		anadirComponente(new Pixel(bx + 2, by + 1));
		anadirComponente(new Pixel(bx - 1, by + 2));
		anadirComponente(new Pixel(bx, by + 2));
		anadirComponente(new Pixel(bx + 1, by + 2));
		anadirComponente(new Pixel(bx + 2, by + 2));
		anadirComponente(new Pixel(bx + 3, by + 2));
	}

	@Override
	public int origenDisparoX() {
		return getX() + 2;
	}

	@Override
	public int origenDisparoY() {
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
			{ getX() - 1, getY() + 2 },
			{ getX(), getY() + 2 },
			{ getX() + 1, getY() + 2 },
			{ getX() + 2, getY() + 2 },
			{ getX() + 3, getY() + 2 },
		};
	}

	public int getTipoNave() {
		return 1; // Verde
	}

}
