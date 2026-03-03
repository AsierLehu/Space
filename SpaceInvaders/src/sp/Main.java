package sp;

import sp.StartFrame;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // Lanzar la interfaz gr�fica en el hilo de eventos de Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new StartFrame();
            }
        });
    }
}