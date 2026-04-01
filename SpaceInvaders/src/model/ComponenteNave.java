package model;

/**
 * Participante del patrón Composite: contrato común para hojas ({@link PixelNave})
 * y compuestos ({@link CompositeNave}).
 */
public interface ComponenteNave {

    void mover(int dx, int dy, Espacio espacio);

    /** Punto de referencia de la nave en el tablero (p. ej. esquina o ancla). */
    int getRefX();

    int getRefY();
}
