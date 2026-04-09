package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Compuesto del patrÃ³n Composite: agrupa mÃºltiples {@link ComponenteNave} 
 * para formar una nave completa.
 * 
 * Responsabilidades:
 * - Agregar y remover componentes individuales (PixelNave)
 * - Delegar el movimiento a todos sus componentes
 * - Mantener la referencia X/Y mÃ¡xima como punto de referencia
 * 
 * Nota: Cada PixelNave notifica independientemente a Espacio cuando se mueve,
 * usando {@link ComponenteNave#notificarMovimiento(int, int, int, int)}.
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
    public void mover(int dx, int dy) {
    	// Primero comprobar que todos los píxeles pueden moverse
        for (ComponenteNave c : components) {
        	int newX = c.getRefX() + dx;
        	int newY = c.getRefY() + dy;
        	if (newX < 0 || newX >= 100 || newY < 0 || newY >= 60) {
        		return; // Algún píxel saldría del tablero - cancelar movimiento
        	}
        }
        
        // Todos pueden moverse - mover y notificar movimiento
        for (ComponenteNave c : components) {
        	int oldX = c.getRefX();
        	int oldy = c.getRefY();
        	c.mover(dx, dy);
        	c.notificarMovimiento(oldX, oldy, c.getRefX(), c.getRefY());
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
