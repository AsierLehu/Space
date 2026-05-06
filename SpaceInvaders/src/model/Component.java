package model;

/**
 * Contrato común del patrón Composite para piezas de nave y de disparo.
 * Las hojas son Pixel; los compuestos Composite.
 */
public interface Component {

	void mover(int dx, int dy, int tipoNave);

	int getRefX();

	int getRefY();

	boolean isActivo();

	void setActivo(boolean b);
	// TODO; MIRAR ESTO DE LOS DEFAULT Y LAS HERENCIAS
	/**
	 * Id del proyectil del jugador ({@code >= 21} en partida) compartido por todos los píxeles
	 * de un mismo disparo; −1 si no es proyectil.
	 */
	default int getDisparoId() {
		return -1;
	}
	
	default void notificarMuerteJugador() {
		Espacio.getEspacio().notificarMuerteJugador(new int[][] {
			{ getRefX(), getRefY() }
		});
	}

	/** Vacío por defecto; Pixel y Composite en modo esProyectil lo sustituyen. */
	default void notificarDisparoNuevo() {
	}

	/** Inicia el juego llamando a cambiarAMain en Espacio */
	default void inicializar() {
		Espacio.getEspacio().cambiarAMain();
	}

	/**
	 * Sincroniza una nave jugadora ({@code tipoNave} 1–3) en la matriz y vista inicial.
	 * Vacío por defecto; {@link Pixel} y {@link Composite} lo amplían (disparos no aplican).
	 */
	default void registrarPosicionInicialJugador(int tipoNave) {
	}

	/** Enemigos: pintan su id en el espejo al crearse (no aplica a proyectiles). El {@link Composite} raíz también cuenta el enemigo en {@link Espacio}. */
	default void registrarEnemigoEnMatrizInicial(int idEnemigo) {
	}
}
