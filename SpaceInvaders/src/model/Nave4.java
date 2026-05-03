package model;
 
import java.util.ArrayList;
 
/**
 * Nave4: nave con forma de diamante y velocidad 2.
 * Estrategias: pixel (infinito) y doble (25 disparos).
 *
 *   Forma (ancla en bx, by):
 *       X        <- (bx+1, by)
 *      XXX       <- (bx, by+1) (bx+1, by+1) (bx+2, by+1)
 *       X        <- (bx+1, by+2)
 */
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
		int bx = getX();
		int by = getY();
		anadirComponente(new Pixel(bx, by));
		anadirComponente(new Pixel(bx,     by + 1));
		anadirComponente(new Pixel(bx + 1, by + 1));
		anadirComponente(new Pixel(bx + 2, by));
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
			{ getX(), getY()     },
			{ getX(),     getY() + 1 },
			{ getX() + 1, getY() + 1 },
			{ getX() + 2, getY()},
			{ getX() + 2, getY() + 1},
		};
	}
 
	@Override
	public int getTipoNave() {
		return 4; // Nuevo tipo
	}
}