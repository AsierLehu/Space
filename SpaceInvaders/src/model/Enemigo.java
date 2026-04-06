package model;

import java.util.ArrayList;

public class Enemigo extends Naves {
	
	//Posici�n aleatoria en la parte superior
	public Enemigo(int x, int y) {
		super(x, y);
	}

	@Override
	public void construir() {}

	@Override
	public ArrayList<StrategyDisparo> getEstrategiasPermitidas() {
		return null;
	}

	@Override
	public int[][] celdasOcupadas() {
		return new int[][] {
			{ x, y },
			{ x + 1, y },
			{ x, y + 1 },
			{ x + 1, y + 1 }
		};
	}

}