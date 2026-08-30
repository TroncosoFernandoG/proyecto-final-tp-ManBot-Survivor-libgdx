package elementos;

import com.badlogic.gdx.InputAdapter;

public class ControladorEntrada extends InputAdapter {

	private boolean arriba;
	private boolean abajo;
	private boolean izquierda;
	private boolean derecha;
	
	@Override
	public boolean keyDown(int tecla) {
		
		switch (tecla) {
        case com.badlogic.gdx.Input.Keys.W:
            arriba = true;
            break;
        case com.badlogic.gdx.Input.Keys.S:
            abajo = true;
            break;
        case com.badlogic.gdx.Input.Keys.A:
            izquierda = true;
            break;
        case com.badlogic.gdx.Input.Keys.D:
            derecha = true;
            break;
    }

    return true;
    
	}
	
	@Override
	public boolean keyUp(int tecla) {

	    switch (tecla) {
	        case com.badlogic.gdx.Input.Keys.W:
	            arriba = false;
	            break;
	        case com.badlogic.gdx.Input.Keys.S:
	            abajo = false;
	            break;
	        case com.badlogic.gdx.Input.Keys.A:
	            izquierda = false;
	            break;
	        case com.badlogic.gdx.Input.Keys.D:
	            derecha = false;
	            break;
	    }

	return true;
	}
	
	public boolean estaArriba() {
	    return arriba;
	}

	public boolean estaAbajo() {
	    return abajo;
	}

	public boolean estaIzquierda() {
	    return izquierda;
	}

	public boolean estaDerecha() {
	    return derecha;
	}
	
}