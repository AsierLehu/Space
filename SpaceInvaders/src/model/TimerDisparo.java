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
        // Timer de 50ms (misma frecuencia que el gameTimer anterior)
        timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Verificar que el juego sigue activo antes de actualizar disparos
                boolean gameOver = Espacio.getEspacio().isGameOver();
                boolean gameWon = Espacio.getEspacio().isGameWon();
                
                JugadorBueno.getJugadorBueno().actualizarDisparos();
                
            }
        });
    }
    
    /**
     * Obtiene la instancia única del TimerDisparo (Singleton).
     */
    public static TimerDisparo getInstancia() {
        if (instancia == null) {
            instancia = new TimerDisparo();
        }
        return instancia;
    }
    
    /**
     * Inicia el timer de disparos.
     */
    public void iniciar() {
        if (!iniciado && timer != null) {
            timer.start();
            iniciado = true;
        }
    }
    

}