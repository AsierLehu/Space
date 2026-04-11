package model;

/**
 * Fábrica singleton para crear naves según un identificador de tipo.
 */
public class NaveFactory {

	private static NaveFactory myFactory;

	private NaveFactory() {}

	public static NaveFactory getNaveFactory() {
		if (myFactory == null) {
			myFactory = new NaveFactory();
		}
		return myFactory;
	}

	/**
	 * Crea la nave jugable según el identificador. Punto único de instanciación
	 * (patrón Factory); las clases concretas son {@link Nave1}, {@link Nave2}, {@link Nave3}.
	 */
	public Naves generate(String pNave) {
		int x = 50;
		int y = 55;
		int velocidad = 1;
		if (pNave == null) {
			return new Nave1(x, y, velocidad);
		}
		switch (pNave) {
			case "Nave1":
				return new Nave1(x, y, velocidad);
			case "Nave2":
				return new Nave2(x, y, velocidad);
			case "Nave3":
				return new Nave3(x, y, velocidad);
			default:
				return null;
		}
	}
}
