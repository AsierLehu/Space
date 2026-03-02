package sp;

import sp.StartFrame;

public class Main {
	public static void main(String[] args) {
        // Lanzar la interfaz gráfica en el hilo de eventos de Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new StartFrame();
            }
        });
    }
}
