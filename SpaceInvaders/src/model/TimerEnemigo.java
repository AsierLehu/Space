package model;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Timer independiente para manejar el movimiento de enemigos.
 * Usa patrón Singleton y notifica a FlotaEnemigos cada 200ms.
 */
public class TimerEnemigo {

    private static TimerEnemigo instancia;
    private Timer timer;
    private boolean iniciado;

    private TimerEnemigo() {
        iniciado = false;
        timer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FlotaEnemigos.getFlotaEnemigos().moverEnemigos();
            }
        });
    }

    /** Devuelve la instancia única del timer de enemigos. */
    public static TimerEnemigo getInstancia() {
        if (instancia == null) {
            instancia = new TimerEnemigo();
        }
        return instancia;
    }

    /** Inicia el temporizador de movimiento de la flota si aún no estaba activo. */
    public void iniciar() {
        if (!iniciado && timer != null) {
            timer.start();
            iniciado = true;
        }
    }
}
