package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Compuesto del patrón Composite: agrupa otros {@link ComponenteNave} y delega el movimiento.
 */
public class CompositeNave implements ComponenteNave {

    private List<ComponenteNave> components = new ArrayList<>();

    public CompositeNave() {}

    public void addComponent(ComponenteNave c) {
        components.add(c);
    }
    
    public void removeComponent (ComponenteNave c) {
    	components.remove(c);
    }

    @Override
    public void mover(int dx, int dy, Espacio espacio) {
        for (ComponenteNave c : components) {
            c.mover(dx, dy, espacio);
        }
    }

    @Override
    public int getRefX() {
        int min = Integer.MAX_VALUE;
        for (ComponenteNave c : components) {
            min = Math.min(min, c.getRefX());
        }
        return min == Integer.MAX_VALUE ? 0 : min;
    }

    @Override
    public int getRefY() {
        int min = Integer.MAX_VALUE;
        for (ComponenteNave c : components) {
            min = Math.min(min, c.getRefY());
        }
        return min == Integer.MAX_VALUE ? 0 : min;
    }
}
