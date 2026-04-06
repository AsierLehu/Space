package model;

import java.util.ArrayList;

/**
 * Nave en forma de T invertida (tetromino): 4 celdas según diseño,
 * construidas con {@link CompositeNave} y {@link PixelNave}.
 * <p>
 * Coordenadas relativas al ancla {@code (x,y)} (esquina superior izquierda del
 * rectángulo que envuelve la figura, s = 1 celda):
 * <ul>
 *   <li>Fila superior: un píxel en {@code (x+1, y)}</li>
 *   <li>Fila inferior: tres píxel en {@code (x, y+1)}, {@code (x+1, y+1)}, {@code (x+2, y+1)}</li>
 * </ul>
 */
public class Nave1 extends Naves {

	private DisparoPixel estrategiaPixel = new DisparoPixel();
	private DisparoFlecha estrategiaFlecha = new DisparoFlecha();
	
	public Nave1(int x, int y) {
		super(x, y);
		inicializarNaveJugador();
	}

	@Override
	public void construir() {
		int bx = x;
		int by = y;
		nave.addComponent(new PixelNave(bx + 1, by));
		nave.addComponent(new PixelNave(bx, by + 1));
		nave.addComponent(new PixelNave(bx + 1, by + 1));
		nave.addComponent(new PixelNave(bx + 2, by + 1));
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
		return estrategias;
	}
}
