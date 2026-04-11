package model;

import java.util.ArrayList;

/**
 * Nave en forma de T invertida (tetromino): 4 celdas según diseño,
 * <p>
 * Coordenadas relativas al ancla (x,y) (esquina superior izquierda del
 * rectángulo que envuelve la figura, s = 1 celda):
 * <ul>
 *   <li>Fila superior: un píxel en (x+1, y)</li>
 *   <li>Fila inferior: tres píxel en (x, y+1), (x+1, y+1), (x+2, y+1)</li>
 * </ul>
 */
public class Nave1 extends Naves {

	private DisparoPixel estrategiaPixel = new DisparoPixel();
	private DisparoFlecha estrategiaFlecha = new DisparoFlecha();
	
	public Nave1(int x, int y, int velocidad) {
		super(x, y, velocidad);
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
		anadirComponente(new Pixel(bx - 1, by + 2));
		anadirComponente(new Pixel(bx, by + 2));
		anadirComponente(new Pixel(bx + 1, by + 2));
		anadirComponente(new Pixel(bx + 2, by + 2));
		anadirComponente(new Pixel(bx + 3, by + 2));
	}

	@Override
	protected int origenDisparoX() {
		return x + 2;
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
			{ x - 1, y + 2 },
			{ x, y + 2 },
			{ x + 1, y + 2 },
			{ x + 2, y + 2 },
			{ x + 3, y + 2 },
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
