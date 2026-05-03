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
        
        // Inicializar flota con enemigos aleatorios
        if (datos.length >= 2 && datos[0] == 20) {
            inicializar(100, datos[1]);
        }
        
        // Solo procesar mensajes de eliminación de enemigos (MSG_ELIMINAR_ENEMIGO = 18)
        if (datos.length >= 2 && datos[0] == 18) {
            int enemigoId = datos[1];
            eliminarEnemigoPorId(enemigoId);
        }
    }

    /** Quita de la lista al enemigo vivo que ocupa la celda (x,y), si existe. */
    public void eliminarEnemigoQueContieneCelda(int x, int y) {
        for (int i = 0; i < enemigos.size(); i++) {
            Enemigo enemigo = enemigos.get(i);
            if (enemigo.isVivo()) {
                int[][] celdas = enemigo.celdasOcupadas();
                for (int[] celda : celdas) {
                    if (celda[0] == x && celda[1] == y) {
                        enemigos.remove(i);
                        return; // Solo eliminar el primero que coincida
                    }
                }
            }
        }
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

    // Limpia la flota y añade n_enemigos enemigos en posiciones aleatorias de la fila superior sin tocarse
    public void inicializar(int anchura, int n_enemigos) {
        enemigos.clear();
        siguienteId = 11; // Reiniciar contador de IDs
        Random rand = new Random();
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

    /**
     * Método llamado por TimerEnemigo para mover todos los enemigos.
     * Cada enemigo llama a su método mover() que notificará a Espacio.
     */
    public void moverEnemigos() {
        // Crear una copia para evitar problemas de concurrencia durante eliminaciones
        ArrayList<Enemigo> enemigosCopia = new ArrayList<>(enemigos);
        
        for (Enemigo enemigo : enemigosCopia) {
            if (enemigo.isVivo()) {
                // Cada enemigo se mueve y notifica a Espacio a través del Component interface
                enemigo.mover(0, 1, 0);
                
                // Si el juego terminó durante el movimiento, salir del bucle
                if (Espacio.getEspacio().isGameOver() || Espacio.getEspacio().isGameWon()) {
                    break;
                }
            }
        }
    }
}
