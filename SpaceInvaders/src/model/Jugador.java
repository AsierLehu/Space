package model;

public abstract class Jugador extends Naves {

	private Disparo disparo;
	protected final CompositeNave nave;
	private final String tipoNave;

	protected Jugador(int x, int y, String tipoNave) {
		super(x, y);
		this.tipoNave = tipoNave != null ? tipoNave : "Nave1";
		this.nave = new CompositeNave();
		construir();
		this.x = nave.getRefX();
		this.y = nave.getRefY();
		this.disparo = new Disparo(origenDisparoX(), origenDisparoY());
	}

	public String getTipoNave() {
		return tipoNave;
	}

	/** Origen X del disparo (puede sobrescribirse si la forma no usa la esquina de referencia). */
	protected int origenDisparoX() {
		return x;
	}

	/** Origen Y del disparo (por encima de la referencia de la nave). */
	protected int origenDisparoY() {
		return y - 1;
	}

	@Override
	public void mover(int dx, int dy) {
		Espacio espacio = Espacio.getEspacio();
		int edx = dx * velocidad;
		int edy = dy * velocidad;
		nave.mover(edx, edy, espacio);
		x = nave.getRefX();
		y = nave.getRefY();
	}

	public void disparar() {
		if (!disparo.isActivo()) {
			disparo = new Disparo(origenDisparoX(), origenDisparoY());
			disparo.setActivo(true);
		}
	}

	public Disparo getDisparo() {
		return disparo;
	}

	public void cambiarTipoDisparo() {
		disparo.cambiarTipoDisparo();
	}

	/** Celdas del tablero ocupadas por la forma de la nave (referencia + píxeles del composite). */
	public int[][] celdasOcupadas() {
		return new int[][] { { x, y } };
	}
}
