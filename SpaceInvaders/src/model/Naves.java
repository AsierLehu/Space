package model;


public abstract class Naves {
	
	protected int x;
	protected int y;
	protected int velocidad;
	protected boolean vivo;
	
	public Naves(int x, int y, int velocidad) {
		this.x = x;
		this.y = y;
		this.velocidad = velocidad;
		this.vivo = true;
	}

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean isVivo() {
		return vivo;
	}
	
	public void setVivo(boolean b) {
		this.vivo = b;
	}
	
	public abstract void mover();
}
