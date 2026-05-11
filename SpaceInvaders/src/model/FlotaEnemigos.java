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
    private int siguienteId = 11;

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

        if (datos.length >= 2 && datos[0] == 20) {
            inicializar(100, datos[1]);
        }

        if (datos.length >= 2 && datos[0] == 18) {
            int enemigoId = datos[1];
            eliminarEnemigoPorId(enemigoId);
        }
    }

    /** Limpia la flota y añade enemigos en posiciones aleatorias de la fila superior sin solaparse. */
    public void inicializar(int anchura, int nEnemigos) {
        enemigos.clear();
        siguienteId = 11;
        Random rand = new Random();
        ArrayList<Integer> xOcupadas = new ArrayList<>();

        int intentos = 0;
        while (enemigos.size() < nEnemigos && intentos < 100) {
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
        iniciarTimerMovimiento();
    }

    /**
     * Método llamado por TimerEnemigo para mover todos los enemigos.
     * Cada enemigo llama a su método mover() que notificará a Espacio.
     */
    // APUNTE: la copia es ** superficial**: son las mismas instancias de Enemigo; solo se duplica la estructura de la lista, no los objetos
    public void moverEnemigos() {
        ArrayList<Enemigo> enemigosCopia = new ArrayList<>(enemigos);
        enemigosCopia.forEach(enemigo -> {
            if (enemigo.isVivo()) {
                enemigo.mover(0, 1, 0);
            }
        });
    }

    /** Busca un enemigo vivo por su id en la flota. */
    public Enemigo encontrarEnemigoPorId(int id) {
        return enemigos.stream()
            .filter(e -> e.isVivo() && e.getId() == id)
            .findFirst()
            .orElse(null);
    }

    /** Quita de la lista al enemigo con el id dado. */
    public void eliminarEnemigoPorId(int id) {
        Enemigo enemigo = encontrarEnemigoPorId(id);
        if (enemigo != null) {
            enemigos.remove(enemigo);
        }
    }

    /** Arranca el ciclo periódico de movimiento de la flota ({@link TimerEnemigo}). */
    private void iniciarTimerMovimiento() {
        TimerEnemigo.getInstancia().iniciar();
    }
}
