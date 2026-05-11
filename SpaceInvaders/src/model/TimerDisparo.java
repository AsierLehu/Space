package model;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Timer independiente para manejar el movimiento y actualización de disparos.
 * Usa patrón Singleton y notifica a Espacio cada 50ms para procesar disparos.
 */
public class TimerDisparo {

    private static TimerDisparo instancia;
    private Timer timer;
    private boolean iniciado;

    private TimerDisparo() {
        iniciado = false;
        timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JugadorBueno.getJugadorBueno().actualizarDisparos();
            }
        });
    }

    /** Devuelve la instancia única del timer de disparos del jugador. */
    public static TimerDisparo getInstancia() {
        if (instancia == null) {
            instancia = new TimerDisparo();
        }
        return instancia;
    }

    /** Inicia el temporizador de actualización de proyectiles si aún no estaba activo. */
    public void iniciar() {
        if (!iniciado && timer != null) {
            timer.start();
            iniciado = true;
        }
    }
}
