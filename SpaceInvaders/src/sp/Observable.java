package sp;

import java.util.ArrayList;

public class Observable {
	private ArrayList<Observador> observadores = new ArrayList<>();

    public void addObserver(Observador o) {
        observadores.add(o);
    }

    public void removeObserver(Observador o) {
        observadores.remove(o);
    }

    protected void notificarObservadores() {
        for (Observador o : observadores) {
            o.actualizar();
        }
    }
}
