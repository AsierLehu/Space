package model;

/** Disparo en forma de rombo; munición limitada. */
public class DisparoRombo implements StrategyDisparo {

    private int municion = 20;

    @Override
    public Component crearDisparo(int x, int y) {
        if (gastar()) {
            int idDisparo = Disparo.tomarSiguienteIdDisparo();
            Composite comp = new Composite(true, idDisparo);
            comp.addComponent(new Pixel(x, y, true, idDisparo));
            comp.addComponent(new Pixel(x - 1, y + 1, true, idDisparo));
            comp.addComponent(new Pixel(x, y + 1, true, idDisparo));
            comp.addComponent(new Pixel(x + 1, y + 1, true, idDisparo));
            comp.addComponent(new Pixel(x, y + 2, true, idDisparo));
            return comp;
        }
        return null;
    }

    @Override
    public boolean gastar() {
        if (municion > 0) {
            municion--;
            return true;
        }
        return false;
    }

    @Override
    public String getTipo() {
        return "rombo";
    }

    @Override
    public int getMunicion() {
        return municion;
    }

    @Override
    public boolean tieneMunicion() {
        return municion > 0;
    }
}
