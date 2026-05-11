package model;

/**
 * Estrategia concreta de disparo (Strategy): tipo y munición y construcción del {@link Component} proyectil.
 */
public interface StrategyDisparo {

    /** Identificador legible del tipo: "pixel", "flecha", "rombo", etc. */
    String getTipo();

    /** Munición restante; {@code -1} significa infinita. */
    int getMunicion();

    /** Consume una unidad de munición si hay; devuelve false si no queda. */
    boolean gastar();

    /** Indica si aún se puede disparar con esta estrategia. */
    boolean tieneMunicion();

    /** Construye el árbol Composite/Pixel del proyectil en la posición de origen. */
    Component crearDisparo(int x, int y);
}
