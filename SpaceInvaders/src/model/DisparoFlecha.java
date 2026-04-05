package model;

public class DisparoFlecha implements StrategyDisparo {

	private int municion = 30;
	
	@Override
	public String getTipo() {
		return "flecha";
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