package spmain;

import viewController.StartFrame;

import javax.swing.SwingUtilities;

/** Punto de entrada: arranca la UI de Space Invaders en el EDT de Swing. */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new StartFrame();
            }
        });
    }
}
