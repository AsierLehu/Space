package model;

import java.util.ArrayList;

/** Tercera nave jugable: tres estrategias (pixel, flecha, rombo). */
public class Nave3 extends Naves {

    public Nave3(int x, int y, int velocidad) {
        super(x, y, velocidad);
        ArrayList<StrategyDisparo> estrategias = new ArrayList<>();
        estrategias.add(new DisparoPixel());
        estrategias.add(new DisparoFlecha());
        estrategias.add(new DisparoRombo());
        setGestorDisparos(new Disparo(estrategias));
        inicializarNaveJugador();
    }

    @Override
    public void construir() {
        anadirComponente(new Pixel(x + 1, y, false, -1));
        anadirComponente(new Pixel(x, y + 1, false, -1));
        anadirComponente(new Pixel(x + 1, y + 1, false, -1));
        anadirComponente(new Pixel(x + 2, y + 1, false, -1));
    }

    @Override
    public int origenDisparoX() {
        return x + 1;
    }

    @Override
    public int origenDisparoY() {
        return y - 3;
    }
}
