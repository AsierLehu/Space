package model;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 * Gestiona la flota de enemigos del juego.
 * Observa a {@link Espacio}: ante un impacto disparo-enemigo (detección por matriz en Espacio),
 * elimina de la lista al enemigo afectado.
 */
@SuppressWarnings("deprecation")
public class FlotaEnemigos implements Observer {

    private static FlotaEnemigos miFlotaEnemigos;
    private ArrayList<Enemigo> enemigos;
    private static int siguienteId = 11;

    private FlotaEnemigos() {
        this.enemigos = new ArrayList<>();
    }

    public static FlotaEnemigos getFlotaEnemigos() {
        if (miFlotaEnemigos == null) {
            miFlotaEnemigos = new FlotaEnemigos();
        }
        return miFlotaEnemigos;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg == null || !(arg instanceof int[])) {
            return;
        }
        
        int[] datos = (int[]) arg;
        
        
        if (datos.length >= 2) {
            // Nuevo formato: [MSG_ELIMINAR_ENEMIGO, enemigoId, ...]
            int enemigoId = datos[1];
            // Solo eliminar el enemigo de la lista, NO hacer notificaciones visuales
            eliminarEnemigoPorId(enemigoId);
        }
    }

    /** Quita de la lista al enemigo vivo que ocupa la celda (x,y), si existe. */
    public void eliminarEnemigoQueContieneCelda(int x, int y) {
        enemigos.removeIf(e -> {
            if (!e.isVivo()) {
                return false;
            }
            for (int[] c : e.celdasOcupadas()) {
                if (c[0] == x && c[1] == y) {
                    return true;
                }
            }
            return false;
        });
    }

    /** Encuentra un enemigo por su ID. */
    public Enemigo encontrarEnemigoPorId(int id) {
        for (Enemigo e : enemigos) {
            if (e.isVivo() && e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    /** Elimina enemigo por ID de la lista. */
    public void eliminarEnemigoPorId(int id) {
        Enemigo enemigo = encontrarEnemigoPorId(id);
        if (enemigo != null) {
            enemigos.remove(enemigo);
        }
    }

    

    // ─── Inicialización ───────────────────────────────────────────────────────

    // Limpia la flota y añade 4-8 enemigos en posiciones aleatorias de la fila superior sin tocarse
    public void inicializar(int anchura) {
        enemigos.clear();
        siguienteId = 11; // Reiniciar contador de IDs
        Random rand = new Random();
        int n_enemigos = rand.nextInt(5) + 4;
        ArrayList<Integer> xOcupadas = new ArrayList<>();

        int intentos = 0;
        while (enemigos.size() < n_enemigos && intentos < 100) {
            int ex = rand.nextInt(anchura - 4);
            int ey = rand.nextInt(4);

            boolean huecoLibre = true;
            for (int dx = 0; dx <= 4 && huecoLibre; dx++) {
                if (xOcupadas.contains(ex + dx)) {
                    huecoLibre = false;
                }
            }
            if (huecoLibre) {
                enemigos.add(new Enemigo(ex, ey, 1, siguienteId++));
                for (int dx = 0; dx <= 4; dx++) {
                    xOcupadas.add(ex + dx);
                }
            }
            intentos++;
        }
    }

    // ─── Consultas de estado ──────────────────────────────────────────────────

    public ArrayList<Enemigo> getEnemigos() {
        return enemigos;
    }

    public boolean algunoLlegoAbajo(int altura) {
        for (Enemigo e : enemigos) {
            if (e.isVivo()) {
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

    /** Victoria: flota vacía (todos eliminados) o todos los restantes están muertos. */
    public boolean todosDestruidos() {
        if (enemigos.isEmpty()) {
            return true;
        }
        for (Enemigo e : enemigos) {
            if (e.isVivo()) {
                return false;
            }
        }
        return true;
    }
}
