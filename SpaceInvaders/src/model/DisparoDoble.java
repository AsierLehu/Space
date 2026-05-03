package model;
 
/**
 * Estrategia de disparo doble: lanza dos pixeles en paralelo simultaneamente.
 * Municion limitada a 25 disparos.
 */
public class DisparoDoble implements StrategyDisparo {
 
	private int municion = 25;
 
	@Override
	public String getTipo() {
		return "doble";
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
 
	@Override
	public Component crearDisparo(int x, int y) {
		if (gastar()) {
			int idDisparo = Disparo.tomarSiguienteIdDisparo();
			Composite comp = new Composite(true, idDisparo);
			// Dos pixeles en paralelo, separados 2 celdas
			comp.addComponent(new Pixel(x - 1, y, true, idDisparo));
			comp.addComponent(new Pixel(x + 1, y, true, idDisparo));
			return comp;
		}
		return null;
	}
}
