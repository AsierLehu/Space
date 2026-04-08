package model;

import java.util.ArrayList;

public class Nave2 extends Naves {
	private DisparoPixel estrategiaPixel = new DisparoPixel();
	private DisparoRombo estrategiaRombo = new DisparoRombo();
	
	public Nave2(int x, int y) {
		super(x, y);
		inicializarNaveJugador();
	}

	@Override
	public void construir() {
		int bx = x;
		int by = y;
		nave.addComponent(new PixelNave(bx, by));
		nave.addComponent(new PixelNave(bx + 2, by));
		nave.addComponent(new PixelNave(bx, by + 1));
		nave.addComponent(new PixelNave(bx + 1, by + 1));
		nave.addComponent(new PixelNave(bx + 2, by + 1));
		nave.addComponent(new PixelNave(bx, by + 2));
		nave.addComponent(new PixelNave(bx + 1, by + 2));
		nave.addComponent(new PixelNave(bx + 2, by + 2));
	}

	@Override
	protected int origenDisparoX() {
		return x + 1;
	}

	@Override
	protected int origenDisparoY() {
		return y - 1;
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

	@Override
	public ArrayList<StrategyDisparo> getEstrategiasPermitidas() {
		ArrayList<StrategyDisparo> estrategias = new ArrayList<StrategyDisparo>();
		estrategias.add(estrategiaPixel);
		estrategias.add(estrategiaRombo);
		return estrategias;
	}
}
