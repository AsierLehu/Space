package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Compuesto del patrón Composite: agrupa múltiples {@link ComponenteNave} 
 * para formar una nave completa.
 * 
 * Responsabilidades:
 * - Agregar y remover componentes individuales (PixelNave)
 * - Delegar el movimiento a todos sus componentes
 * - Mantener la referencia X/Y máxima como punto de referencia
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
    
    public List<ComponenteNave> getComponents() {
    	return components;
    }

    @Override
    public void mover(int dx, int dy) {
    	// Primero comprobar que todos los p�xeles pueden moverse
        for (ComponenteNave c : components) {
        	int newX = c.getRefX() + dx;
        	int newY = c.getRefY() + dy;
        	if (newX < 0 || newX >= 100 || newY < 0 || newY >= 60) {
        		return; // Alg�n p�xel saldr�a del tablero - cancelar movimiento
        	}
        }
        
        // Guardar posiciones antiguas antes de mover
        int[] oldPositionsX = new int[components.size()];
        int[] oldPositionsY = new int[components.size()];
        for (int i = 0; i < components.size(); i++) {
        	oldPositionsX[i] = components.get(i).getRefX();
        	oldPositionsY[i] = components.get(i).getRefY();
        }
        
        // Mover todos los píxeles
        for (ComponenteNave c : components) {
        	c.mover(dx, dy);
        }
        
        // Notificar todas las borraduras y pinturas de una sola vez
        Espacio espacio = Espacio.getEspacio();
        espacio.notificarMovimientoJugadorCompleto(oldPositionsX, oldPositionsY, components);
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
