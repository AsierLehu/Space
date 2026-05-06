package model;

/**
 * Enemigo multipixel (Composite). Forma en rejilla 5×3 relativa al ancla (x,y):
 * fila superior (0,0)(1,0) · (3,0)(4,0), media (1,1)(2,1)(3,1), inferior (2,2).
 */
public class Enemigo extends Naves {
	
	private int id;
	
	//Posici�n aleatoria en la parte superior
	public Enemigo(int x, int y, int velocidad, int id) {
		super(x, y, velocidad);
		this.id = id;
		inicializarNaveJugador();
		registrarEnemigoEnMatrizInicial(id);
	}

	@Override
	public void construir() {

		anadirComponente(new Pixel(x, y, false, -1));
		anadirComponente(new Pixel(x + 1, y, false, -1));
		anadirComponente(new Pixel(x + 3, y, false, -1));
		anadirComponente(new Pixel(x + 4, y, false, -1));

		anadirComponente(new Pixel(x + 1, y + 1, false, -1));
		anadirComponente(new Pixel(x + 2, y + 1, false, -1));
		anadirComponente(new Pixel(x + 3, y + 1, false, -1));

		anadirComponente(new Pixel(x + 2, y + 2, false, -1));
	}

	
	public int getId() {
		return id;
	}

}