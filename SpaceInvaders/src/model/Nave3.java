package model;

import java.util.ArrayList;

public class Nave3 extends Naves {
	private DisparoPixel estrategiaPixel = new DisparoPixel();
	private DisparoFlecha estrategiaFlecha = new DisparoFlecha();
	private DisparoRombo estrategiaRombo = new DisparoRombo();
	
	public Nave3(int x, int y, int velocidad) {
		super(x, y, velocidad);
		inicializarNaveJugador();
	}

	@Override
	public void construir() {
		int bx = x;
		int by = y;
		anadirComponenteNave(new PixelNave(bx + 1, by));
		anadirComponenteNave(new PixelNave(bx, by + 1));
		anadirComponenteNave(new PixelNave(bx + 1, by + 1));
		anadirComponenteNave(new PixelNave(bx + 2, by + 1));
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

	@Override
	public ArrayList<StrategyDisparo> getEstrategiasPermitidas() {
		ArrayList<StrategyDisparo> estrategias = new ArrayList<StrategyDisparo>();
		estrategias.add(estrategiaPixel);
		estrategias.add(estrategiaFlecha);
		estrategias.add(estrategiaRombo);
		return estrategias;
	}
}
