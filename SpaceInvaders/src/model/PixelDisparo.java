package model;

public class PixelDisparo implements ComponenteDisparo{
	
	private int x;
	private int y;
	private boolean activo;
	
	public PixelDisparo(int x, int y) {
		this.x = x;
		this.y = y;
		this.activo = true;
	}

	@Override
	public void mover() {
		if (activo) {
			y--;
			if (y < 0) {
				activo = false;
			}
		}
	}

	@Override
	public boolean isActivo() {
		return activo;
	}

	@Override
	public void setActivo(boolean b) {
		this.activo = b;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}
	
	
}
