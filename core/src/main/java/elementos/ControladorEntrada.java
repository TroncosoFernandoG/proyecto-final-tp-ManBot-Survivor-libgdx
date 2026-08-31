package elementos;

import com.badlogic.gdx.InputAdapter;

public class ControladorEntrada extends InputAdapter {

    private boolean arriba;
    private boolean abajo;
    private boolean izquierda;
    private boolean derecha;

    private boolean pulsacionArriba;
    private boolean pulsacionAbajo;
    private boolean pulsacionIzquierda;
    private boolean pulsacionDerecha;

    @Override
    public boolean keyDown(int tecla) {

        switch (tecla) {

        case com.badlogic.gdx.Input.Keys.W:
            arriba = true;
            pulsacionArriba = true;
            break;

        case com.badlogic.gdx.Input.Keys.S:
            abajo = true;
            pulsacionAbajo = true;
            break;

        case com.badlogic.gdx.Input.Keys.A:
            izquierda = true;
            pulsacionIzquierda = true;
            break;

        case com.badlogic.gdx.Input.Keys.D:
            derecha = true;
            pulsacionDerecha = true;
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

    public boolean consumirPulsacionArriba() {

        if (pulsacionArriba) {
            pulsacionArriba = false;
            return true;
        }

        return false;
    }

    public boolean consumirPulsacionAbajo() {

        if (pulsacionAbajo) {
            pulsacionAbajo = false;
            return true;
        }

        return false;
    }

    public boolean consumirPulsacionIzquierda() {

        if (pulsacionIzquierda) {
            pulsacionIzquierda = false;
            return true;
        }

        return false;
    }

    public boolean consumirPulsacionDerecha() {

        if (pulsacionDerecha) {
            pulsacionDerecha = false;
            return true;
        }

        return false;
    }
}