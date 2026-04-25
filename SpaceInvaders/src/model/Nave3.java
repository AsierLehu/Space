package model;

import java.util.ArrayList;

public class Nave3 extends Naves {

	public Nave3(int x, int y, int velocidad) {
		super(x, y, velocidad);
		ArrayList<StrategyDisparo> estrategias = new ArrayList<>();
		estrategias.add(new DisparoPixel());
		estrategias.add(new DisparoFlecha());
		estrategias.add(new DisparoRombo());
		setGestorDisparos(new Disparo(estrategias));
		inicializarNaveJugador();
	}

	@Override
	public void construir() {
		int bx = getX();
		int by = getY();
		anadirComponente(new Pixel(bx + 1, by));
		anadirComponente(new Pixel(bx, by + 1));
		anadirComponente(new Pixel(bx + 1, by + 1));
		anadirComponente(new Pixel(bx + 2, by + 1));
	}

	@Override
	public int origenDisparoX() {
		return getX() + 1;
	}

	@Override
	public int origenDisparoY() {
		return getY() - 3;
	}

	@Override
	public int[][] celdasOcupadas() {
		return new int[][] {
			{ getX() + 1, getY() },
			{ getX(), getY() + 1 },
			{ getX() + 1, getY() + 1 },
			{ getX() + 2, getY() + 1 }
		};
	}

	public int getTipoNave() {
		return 3; // Morado
	}

}
