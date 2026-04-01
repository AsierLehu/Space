package model;

public class Enemigo extends Naves {
	
	//Posici�n aleatoria en la parte superior
	public Enemigo(int x, int y) {
		super(x, y);
	}
	
	@Override
	public void mover(int dx, int dy) {
		x += dx;
		y += dy;
	}

	@Override
	public void construir() {
	}

}