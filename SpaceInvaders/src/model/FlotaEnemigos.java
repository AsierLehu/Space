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

    private ArrayList<Enemigo> enemigos;

    public FlotaEnemigos() {
        this.enemigos = new ArrayList<>();
    }

    // ─── Inicialización ───────────────────────────────────────────────────────

    // Limpia la flota y añade un enemigo en posición aleatoria de la fila superior
    public void inicializar(int anchura) {
        enemigos.clear();
        Random rand = new Random();
        int ex = rand.nextInt(anchura);
        int ey = rand.nextInt(5);
        enemigos.add(new Enemigo(ex, ey));
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

    // ─── Movimiento ───────────────────────────────────────────────────────────

    // Mueve todos los enemigos vivos en la dirección indicada
     public void moverTodos(int dx, int dy) {
        for (Enemigo e : enemigos) {
            if (e.isVivo()) {
                e.mover(dx, dy);
            }
        }
    }

    // ─── Colisiones ───────────────────────────────────────────────────────────

    // Comprueba colisión del disparo con la flota. Devuelve el enemigo golpeado o null
    public Enemigo comprobarColision(Disparo d) {
        for (Enemigo e : enemigos) {
            // Ventana de 2 píxeles para evitar que el disparo "pase de largo"
            if (e.isVivo()
                    && d.getX() == e.getX()
                    && d.getY() <= e.getY()
                    && d.getY() >= e.getY() - 1) {
                e.setVivo(false);
                d.setActivo(false);
                return e;  // devolvemos el enemigo golpeado para que Espacio notifique
            }
        }
        return null;
    }
}
