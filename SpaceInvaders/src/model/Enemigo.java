package model;

public class Enemigo extends Naves {
	
	//Posici�n aleatoria en la parte superior
	public Enemigo(int x, int y) {
		super(x, y);
	}
	
	@Override
	public void mover() {
		//El enemigo baja 1 pixel cada 200ms seg�n requisitos
		y++;
	}

}