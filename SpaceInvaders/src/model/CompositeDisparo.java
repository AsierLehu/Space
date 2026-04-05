package model;

import java.util.ArrayList;
import java.util.List;

public class CompositeDisparo implements ComponenteDisparo{

	private List<ComponenteDisparo> components = new ArrayList<>();

    public CompositeDisparo() {}

    public void addComponent(ComponenteDisparo c) {
        components.add(c);
    }
    
    public void removeComponent (ComponenteDisparo c) {
    	components.remove(c);
    }

    // Mueve los píxeles hacia arriba
	@Override
	public void mover() {
		for (ComponenteDisparo c : components) {
			c.mover();
		}
	}

	// El disparo esta activo si al menos un píxel esta activo
	@Override
	public boolean isActivo() {
		for (ComponenteDisparo c : components) {
			if (c.isActivo()) {
				return true;
			}
		}
		return false;
	}
	
	// Desactivar todos los píxeles del disparo
	@Override
	public void setActivo(boolean b) {
		for (ComponenteDisparo c : components) {
			c.setActivo(b);
		}
	}
	
	// Posición X de referencia: el píxel más a la izquierda
	@Override
	public int getX() {
		int min = Integer.MAX_VALUE;
        for (ComponenteDisparo c : components) {
            min = Math.min(min, c.getX());
            }
        return min == Integer.MAX_VALUE ? 0 : min;
	}
	
	// Posición Y de referencia: el píxel más a la derecha
	@Override
	public int getY() {
		int min = Integer.MAX_VALUE;
		for (ComponenteDisparo c : components) {
			min = Math.min(min, c.getY());
			}
		return min == Integer.MAX_VALUE ? 0 : min;
	}
	
	public List<int[]> celdasOcupadas() {
		List<int[]> celdas = new ArrayList<>();
		for (ComponenteDisparo c : components) {
			if (c.isActivo()) {
				celdas.add(new int[] {c.getX(), c.getY()});
			}
		}
		return celdas;
	}
}
