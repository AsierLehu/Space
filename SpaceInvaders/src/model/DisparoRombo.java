package model;

public class DisparoRombo implements StrategyDisparo {

	private int municion = 20;
	
	@Override
	public String getTipo() {
		return "rombo";
	}

	@Override
	public int getMunicion() {
		return municion;
	}

	@Override
	public boolean gastar() {
		if (municion > 0) {
			municion--;
			return true;
		}
		return false;
	}

	@Override
	public boolean tieneMunicion() {
		return municion > 0;
	}

}