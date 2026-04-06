package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Compuesto del patrón Composite: agrupa múltiples {@link ComponenteDisparo} 
 * para formar disparos con formas (flecha, rombo).
 * 
 * Responsabilidades:
 * - Agregar y remover componentes individuales (PixelDisparo)
 * - Delegar el movimiento a todos sus componentes
 * - Notificar a {@link Espacio} como un todo cuando se mueve
 * 
 * Uso:
 * - "flecha": 3 píxeles en forma triangular
 * - "rombo": 3 píxeles en forma de rombo
 * 
 * Nota: Usa el método default de {@link ComponenteDisparo#notificarMovimiento(int, int, int, int)}
 * para notificar sin tener acceso directo a Espacio.
 */
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
		int oldX = getX();
		int oldY = getY();
		
		for (ComponenteDisparo c : components) {
			c.mover();
		}
		
		// Notificar el cambio del composite como un todo
		notificarMovimiento(oldX, oldY, getX(), getY());
	}

	// El disparo esta activo si al menos un p�xel esta activo
	@Override
	public boolean isActivo() {
		for (ComponenteDisparo c : components) {
			if (c.isActivo()) {
				return true;
			}
		}
		return false;
	}
	
	// Desactivar todos los p�xeles del disparo
	@Override
	public void setActivo(boolean b) {
		for (ComponenteDisparo c : components) {
			c.setActivo(b);
		}
	}
	
	// Posici�n X de referencia: el p�xel m�s a la izquierda
	@Override
	public int getX() {
		int min = Integer.MAX_VALUE;
        for (ComponenteDisparo c : components) {
            min = Math.min(min, c.getX());
            }
        return min == Integer.MAX_VALUE ? 0 : min;
	}
	
	// Posici�n Y de referencia: el p�xel m�s a la derecha
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
