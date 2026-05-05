package model;

import java.util.Observable;
import java.util.Observer;

/**
 * Jugador (singleton) que utiliza NaveFactory para obtener instancias de naves.
 */
@SuppressWarnings("deprecation")
public class JugadorBueno implements Observer {

	private static JugadorBueno miJugadorBueno;

	private String tipoNaveElegido;
	private int tipoNaveNumero;
	private Naves nave;

	private JugadorBueno() {}
	
	public static JugadorBueno getJugadorBueno() {
		if (miJugadorBueno == null) {
			miJugadorBueno = new JugadorBueno();
		}
		return miJugadorBueno;
	}

	// Selecci�n de nave
	/** Uno de: "Nave1", "Nave2", "Nave3". Inicializa el juego con la nave elegida. */
	public void inicializar(String tipo) {
		this.tipoNaveElegido = tipo;
		crearNaveParaPartida();
		if (nave != null) {
			nave.inicializar();
			this.tipoNaveNumero = tipoNave();

			notificarPosicionInicialAlEspacio();
		}
	}
	
	/**
	 * Crea la nave jugable para la partida vía NaveFactory (Factory + Singleton),
	 * usando tipoNaveElegido, y la guarda en nave.
	 */
	public boolean crearNaveParaPartida() {
		
		nave = NaveFactory.getNaveFactory().generate(tipoNaveElegido);
		return nave != null;
	}

	/** Pide el movimiento a la nave almacenada en nave. */
	public void mover(int dx, int dy) {
		if (nave != null && nave.isVivo() ) {
			nave.mover(dx, dy, tipoNaveNumero);
		}
	}
	
	/**
	 * Disparo del jugador: dispara siempre que la nave exista y esté viva.
	 * La notificación a observers se valida en Espacio.
	 */
	public void disparar() {
		if (nave != null && nave.isVivo()) {
			nave.disparar();
		}
	}
	
	public void cambiarTipoDisparo() {
		if (nave != null && nave.isVivo()) {
			nave.cambiarTipoDisparo();
		}
	}
	
	/**
	 * Actualiza todos los disparos de la nave: llamado por TimerDisparo.
	 */
	public void actualizarDisparos() {
		if (nave != null && nave.isVivo()) {
			nave.actualizarDisparos();
		}
	}

	/** Sincroniza matriz y vista inicial del jugador vía la nave y el árbol {@link Component}. */
	public void notificarPosicionInicialAlEspacio() {
			nave.registrarPosicionInicialEnEspacio(tipoNaveNumero);
	}

	/**
	 * Observer pattern: recibe notificaciones de Espacio sobre colisiones de disparos.
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (arg == null || !(arg instanceof int[]) || !(o instanceof Espacio)) {
			return;
		}
		
		int[] datos = (int[]) arg;
		
		// Eliminar proyectil por id: [MSG_ELIMINAR_DISPARO = 19, disparoId]
		if (datos.length >= 2 && datos[0] == 19) {
			int disparoId = datos[1];
			if (nave != null && nave.isVivo()) {
				nave.eliminarDisparoPorId(disparoId);
			}
		}
	}
	private int tipoNave() {
		switch (this.tipoNaveElegido) {
			case "Nave1":
				return 1;
			case "Nave2":
				return 2;
			case "Nave3":
				return 3;
			case "Nave4":
				return 4;
				
	}
		return 0;
}}
