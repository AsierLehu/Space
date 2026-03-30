package model;

import java.awt.Color;
import java.awt.Graphics;

public class DisparoPixel implements StrategyDisparo {

    @Override
    public void disparar(Graphics g, int x, int y) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, 1, 1);
    }
}