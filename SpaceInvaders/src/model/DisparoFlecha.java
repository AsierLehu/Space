package model;

import java.awt.Color;
import java.awt.Graphics;

public class DisparoFlecha implements StrategyDisparo {

    @Override
    public void disparar(Graphics g, int x, int y) {
        g.setColor(Color.CYAN);
        g.fillRect(x, y, 1, 1);
    }
}