package sp;

import java.util.ArrayList;
import java.util.Observable;

public class Espacio extends Observable {
	private static Espacio miEspacio;
	private int anchura;
	private int altura;
	private ArrayList<Naves> naves;
	
	 public Espacio(int anchura, int altura) {
	        this.anchura = anchura;
	        this.altura  = altura;
	        this.naves   = new ArrayList<>();
	    }
}
