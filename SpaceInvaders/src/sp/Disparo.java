package sp;

public class Disparo {
	
	private int x;
	private int y;
	private boolean activo;
	
	public Disparo(int x, int y) {
		this.x = x;
		this.y = y;
		this.activo = false;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean isActivo() {
		return activo;
	}
	
	public void setActivo(boolean b) {
		this.activo = b;
	}
	
	// El disparo sube 1 píxel cada lamada
	public void subir() {
		if (activo) {
			y--;
			// Si sale del tablero, se desactiva
			if (y<0) {
				activo = false;
			}
		}
	}

}
