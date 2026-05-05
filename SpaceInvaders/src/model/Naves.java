package model;

import java.util.ArrayList;

public abstract class Naves {

	protected int x;
	protected int y;
	private int velocidad;
	private boolean vivo;

	private Component ComponenteNave;
	private Disparo gestorDisparos;

	protected Naves(int x, int y, int velocidad) {
		this.x = x;
		this.y = y;
		this.velocidad = velocidad;
		this.vivo = true;
	}

	public boolean isVivo() {
		return vivo;
	}

	public void setGestorDisparos(Disparo gestor) {
		this.gestorDisparos = gestor;
	}

	public void morirComoJugador() {
		if (!vivo) {
			return;
		}
		vivo = false;
		ComponenteNave.notificarMuerteJugador();
	}

	public abstract void construir();

	public abstract int[][] celdasOcupadas();


	public int origenDisparoX() {
		return x;
	}

	public int origenDisparoY() {
		return y - 3;
	}

	public void inicializarNaveJugador() {
		this.ComponenteNave = new Composite();
		construir();
		this.x = ComponenteNave.getRefX();
		this.y = ComponenteNave.getRefY();
	}

	protected void anadirComponente(Component componente) {
		if (ComponenteNave instanceof Composite raiz) {
			raiz.addComponent(componente);
		}
	}

	public boolean disparar() {
		if (gestorDisparos == null) return false;
		
		return gestorDisparos.disparar(origenDisparoX(), origenDisparoY());
	}

	/** Pasa al siguiente tipo permitido; si el actual no tiene munición, sigue hasta dar la vuelta o encontrar una con munición. */
	public void cambiarTipoDisparo() {
		if (gestorDisparos != null) {
			gestorDisparos.cambiarTipoDisparo();
		}
	}

	/** Actualiza todos los disparos de la nave (movimiento y eliminación de inactivos) */
	public void actualizarDisparos() {
		gestorDisparos.actualizarDisparos();
	}
	
	/** Quita de la lista el proyectil con el id asignado al disparar. */
	public void eliminarDisparoPorId(int disparoId) {
		if (gestorDisparos != null) {
			gestorDisparos.eliminarDisparoPorId(disparoId);
		}
	}

	public void mover(int dx, int dy, int tipoNave) {
		int edx = dx * velocidad;
		int edy = dy * velocidad;
		if (ComponenteNave != null) {
			ComponenteNave.mover(edx, edy, tipoNave);
			x = ComponenteNave.getRefX();
			y = ComponenteNave.getRefY();
		} else {
			x += edx;
			y += edy;
		}
	}

	/** Inicia el juego delegando a Component */
	public void inicializar() {
		if (ComponenteNave != null) {
			ComponenteNave.inicializar();
		}
	}

	public void registrarPosicionInicialEnEspacio(int tipoNave) {
		ComponenteNave.registrarPosicionInicialJugador(tipoNave);
	}

	public void registrarEnemigoEnMatrizInicial(int idEnemigo) {
		if (ComponenteNave != null) {
			ComponenteNave.registrarEnemigoEnMatrizInicial(idEnemigo);
		}
	}
}
