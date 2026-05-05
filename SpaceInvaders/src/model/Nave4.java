package model;
 
import java.util.ArrayList;
 

public class Nave4 extends Naves {
 
	public Nave4(int x, int y, int velocidad) {
		super(x, y, velocidad);
		ArrayList<StrategyDisparo> estrategias = new ArrayList<>();
		estrategias.add(new DisparoPixel());
		estrategias.add(new DisparoDoble());
		setGestorDisparos(new Disparo(estrategias));
		inicializarNaveJugador();
	}
 
	@Override
	public void construir() {
		anadirComponente(new Pixel(x, y));
		anadirComponente(new Pixel(x,     y + 1));
		anadirComponente(new Pixel(x + 1, y + 1));
		anadirComponente(new Pixel(x + 2, y));
		anadirComponente(new Pixel(x + 2, y + 1));
	}
 
	@Override
	public int origenDisparoX() {
		return x + 1;
	}
 
	@Override
	public int origenDisparoY() {
		return y - 3;
	}
 
	@Override
	public int[][] celdasOcupadas() {
		return new int[][] {
			{ x, y     },
			{ x,     y + 1 },
			{ x + 1, y + 1 },
			{ x + 2, y},
			{ x + 2, y + 1},
		};
	}
 
	
}