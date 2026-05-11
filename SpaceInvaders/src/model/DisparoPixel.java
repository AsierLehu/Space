package model;

/** Disparo simple de un solo píxel con munición infinita. */
public class DisparoPixel implements StrategyDisparo {

    @Override
    public Component crearDisparo(int x, int y) {
        if (gastar()) {
            int idDisparo = Disparo.tomarSiguienteIdDisparo();
            System.out.println("Creando disparo pixel con id: " + idDisparo);
            return new Pixel(x, y, true, idDisparo);
        }
        return null;
    }

    @Override
    public boolean gastar() {
        return true;
    }

    @Override
    public String getTipo() {
        return "pixel";
    }

    @Override
    public int getMunicion() {
        return -1;
    }

    @Override
    public boolean tieneMunicion() {
        return true;
    }
}
