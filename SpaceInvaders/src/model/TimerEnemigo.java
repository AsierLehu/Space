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
        // Timer de 200ms (misma frecuencia que antes: 4 ticks de 50ms)
        timer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Verificar que el juego sigue activo antes de mover enemigos
                if (!Espacio.getEspacio().isGameOver() && !Espacio.getEspacio().isGameWon()) {
                    FlotaEnemigos.getFlotaEnemigos().moverEnemigos();
                }
            }
        });
    }
    
    /**
     * Obtiene la instancia única del TimerEnemigo (Singleton).
     */
    public static TimerEnemigo getInstancia() {
        if (instancia == null) {
            instancia = new TimerEnemigo();
        }
        return instancia;
    }
    
    /**
     * Inicia el timer de enemigos.
     */
    public void iniciar() {
        if (!iniciado && timer != null) {
            timer.start();
            iniciado = true;
        }
    }
    
    }
