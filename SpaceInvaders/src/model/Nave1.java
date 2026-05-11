package model;

import java.util.ArrayList;

/** Primera nave jugable: forma ancha y disparos pixel + flecha. */
public class Nave1 extends Naves {

    /** Construye la nave en (x,y), registra estrategias de disparo y monta el composite. */
    public Nave1(int x, int y, int velocidad) {
        super(x, y, velocidad);
        ArrayList<StrategyDisparo> estrategias = new ArrayList<>();
        estrategias.add(new DisparoPixel());
        estrategias.add(new DisparoFlecha());
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
        anadirComponente(new Pixel(x - 1, y + 2, false, -1));
        anadirComponente(new Pixel(x, y + 2, false, -1));
        anadirComponente(new Pixel(x + 1, y + 2, false, -1));
        anadirComponente(new Pixel(x + 2, y + 2, false, -1));
        anadirComponente(new Pixel(x + 3, y + 2, false, -1));
    }

    @Override
    public int origenDisparoX() {
        return x + 2;
    }

    @Override
    public int origenDisparoY() {
        return y - 3;
    }
}
