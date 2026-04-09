package model;

import java.util.ArrayList;
import java.util.Random;

/**
 * Gestiona la flota de enemigos del juego.
 * Se encarga de: crear, mover, y consultar el estado de los enemigos,
 * así como de comprobar colisiones con un disparo dado.
 *
 * Esta clase es un módulo independiente del modelo: no conoce ni a Espacio
 * ni a la vista, lo que permite que el código sea más descentralizado.
 */
public class FlotaEnemigos {

    private static FlotaEnemigos miFlotaEnemigos;
    private ArrayList<Enemigo> enemigos;

    private FlotaEnemigos() {
        this.enemigos = new ArrayList<>();
    }

    public static FlotaEnemigos getFlotaEnemigos() {
        if (miFlotaEnemigos == null) {
            miFlotaEnemigos = new FlotaEnemigos();
        }
        return miFlotaEnemigos;
    }

    // ─── Inicialización ───────────────────────────────────────────────────────

    // Limpia la flota y añade 4 enemigos en posiciones aleatorias de la fila superior
    public void inicializar(int anchura) {
        enemigos.clear();
        Random rand = new Random();
        int n_enemigos = rand.nextInt(4)+1;
        // Crear 4 enemigos en posiciones aleatorias, dejando margen para que los enemigos ahora multiplexados no salgan del espacio
        for (int i = 0; i < n_enemigos; i++) {
            int ex = rand.nextInt(anchura-2);
            int ey = rand.nextInt(4);
            enemigos.add(new Enemigo(ex, ey));
        }
    }

    // ─── Consultas de estado ──────────────────────────────────────────────────

    // Devuelve la lista completa de enemigos (vivos y muertos)
    public ArrayList<Enemigo> getEnemigos() {
        return enemigos;
    }

    // Devuelve true si algún enemigo vivo llegó al límite inferior del tablero
    public boolean algunoLlegoAbajo(int altura) {
        for (Enemigo e : enemigos) {
            if (e.isVivo() && e.getY() >= altura - 1) {
                return true;
            }
        }
        return false;
    }

    // Devuelve true si la flota no está vacía y todos los enemigos están muertos (victoria)
    public boolean todosDestruidos() {
        if (enemigos.isEmpty()) return false;
        for (Enemigo e : enemigos) {
            if (e.isVivo()) return false;
        }
        return true;
    }

    // ─── Colisiones ───────────────────────────────────────────────────────────

    // Comprueba colisión del disparo con la flota. Devuelve el enemigo golpeado o null
    public Enemigo comprobarColision(Disparo d) {
        int[][] celdasDisparo = d.celdasOcupadas();
        for (Enemigo e : enemigos) {
        	if (!e.isVivo()) continue;
        	for (int[] celdaDisparo : celdasDisparo) {
        		for (int[] celdaEnemigo : e.celdasOcupadas()) {
        			if (celdaDisparo[0] == celdaEnemigo[0]
        					&& celdaDisparo[1] == celdaEnemigo[1]) {
        				e.setVivo(false);
        				d.setActivo(false);
        				return e;
        			}
        		}
        	}
        }
        return null;
    }
}
