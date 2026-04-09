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

    // Limpia la flota y añade 4-8 enemigos en posiciones aleatorias de la fila superior sin tocarse
    public void inicializar(int anchura) {
        enemigos.clear();
        Random rand = new Random();
        int n_enemigos = rand.nextInt(5)+4;
        ArrayList<Integer> xOcupadas = new ArrayList<>();
        
        // Crear 4-8 enemigos en posiciones aleatorias, sin que se toquen
        int intentos = 0;
        while (enemigos.size() < n_enemigos && intentos < 100) {
            int ex = rand.nextInt(anchura-4);
            int ey = rand.nextInt(4);
            
            // Verificar que la posición X no está ocupada (para evitar que se toquen horizontalmente)
            if (!xOcupadas.contains(ex) && !xOcupadas.contains(ex+1) && !xOcupadas.contains(ex+2)) {
                enemigos.add(new Enemigo(ex, ey));
                xOcupadas.add(ex);
                xOcupadas.add(ex+1);
                xOcupadas.add(ex+2);
            }
            intentos++;
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
            if (e.isVivo()) {
                // Verificar todos los píxeles del enemigo, no solo la referencia
                int[][] celdas = e.celdasOcupadas();
                for (int[] celda : celdas) {
                    if (celda[1] >= altura - 1) {
                        return true;
                    }
                }
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
        				return e;
        			}
        		}
        	}
        }
        return null;
    }
}
