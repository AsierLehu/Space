package model;

import java.util.ArrayList;

/** Segunda nave jugable: disparos pixel + rombo. */
public class Nave2 extends Naves {

    public Nave2(int x, int y, int velocidad) {
        super(x, y, velocidad);
        ArrayList<StrategyDisparo> estrategias = new ArrayList<>();
        estrategias.add(new DisparoPixel());
        estrategias.add(new DisparoRombo());
        setGestorDisparos(new Disparo(estrategias));
        inicializarNaveJugador();
    }

    @Override
    public void construir() {
        anadirComponente(new Pixel(x, y, false, -1));
        anadirComponente(new Pixel(x + 2, y, false, -1));
        anadirComponente(new Pixel(x, y + 1, false, -1));
        anadirComponente(new Pixel(x + 1, y + 1, false, -1));
        anadirComponente(new Pixel(x + 2, y + 1, false, -1));
        anadirComponente(new Pixel(x, y + 2, false, -1));
        anadirComponente(new Pixel(x + 1, y + 2, false, -1));
        anadirComponente(new Pixel(x + 2, y + 2, false, -1));
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
