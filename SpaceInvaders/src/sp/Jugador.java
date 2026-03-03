package sp;

public class Jugador extends Naves {
	
	private Disparo disparo;
	
	// Posici�n inicial: (50, 55) seg�n requisitos
	public Jugador() {
		super(50, 55, 1);
		this.disparo = new Disparo(x, y);
	}
	
	@Override
	public void mover() {}
	
	public void mover(int dx, int dy) {
		x += dx * velocidad;
		y += dy * velocidad;
		
		// L�mites del tablero (0-99 ancho, 0-59 alto)
		if (x < 0) {x = 0;}
		if (x > 99) {x = 99;}
		if (y < 0) {y = 0;}
		if (y > 99) {y = 99;}
}
	
	public void disparar() {
		if (!disparo.isActivo()) {
			disparo = new Disparo(x, y-1);
			disparo.setActivo(true);
		}
	}
	
	public Disparo getDisparo() {
		return disparo;
	}


	
}
