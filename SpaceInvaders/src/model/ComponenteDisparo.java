package model;

public interface ComponenteDisparo {
	void mover();
	boolean isActivo();
	void setActivo(boolean b);
	int getX();
	int getY();
}
