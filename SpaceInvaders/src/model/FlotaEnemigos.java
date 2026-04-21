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
        
        if (datos[0] != Espacio.MSG_ELIMINAR_ENEMIGO) {
            return;
        }
        eliminarEnemigoQueContieneCelda(datos[1], datos[2]);
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

    

    // ─── Inicialización ───────────────────────────────────────────────────────

    // Limpia la flota y añade 4-8 enemigos en posiciones aleatorias de la fila superior sin tocarse
    public void inicializar(int anchura) {
        enemigos.clear();
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
                enemigos.add(new Enemigo(ex, ey, 1));
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
