package sp;

public class Enemigo implements Naves {
	
	//Posición aleatoria en la parte superior
	public Enemigo(int x, int y) {
		super(x, y, 1);
	}
	
	@Override
	public void mover() {
		//El enemigo baja 1 pixel cada 200ms según requisitos
		y++;
	}

}
