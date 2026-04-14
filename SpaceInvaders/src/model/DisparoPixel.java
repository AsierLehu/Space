package model;

public class DisparoPixel implements StrategyDisparo {

	@Override
	public String getTipo() {
		return "pixel";
	}

	@Override
	public int getMunicion() {
		return -1;
	}

	@Override
	public boolean gastar() {
		return true; // No se agota
	}

	@Override
	public boolean tieneMunicion() {
		return true;
	}

	@Override
	public Component crearDisparo(int x, int y) {
		if (gastar()) {
			return new Pixel(x, y, true);
		}
		return null;
	}
}