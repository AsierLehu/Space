package model;

import java.awt.Color;
import java.awt.Graphics;

public class DisparoRombo implements StrategyDisparo {

    @Override
    public void disparar(Graphics g, int x, int y) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, 1, 1);
    }
}