package com.manbotsurvivor.game.elementos;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Gdx;

public class ControladorEntrada extends InputAdapter {

    private final boolean segundoJugador;

    private boolean arriba;
    private boolean abajo;
    private boolean izquierda;
    private boolean derecha;

    private boolean pulsacionArriba;
    private boolean pulsacionAbajo;
    private boolean pulsacionIzquierda;
    private boolean pulsacionDerecha;
    private boolean pulsacionEscape;
    private boolean pulsacionM;
    private boolean pulsacionEnter;

    public ControladorEntrada() {
        this(false);
    }

    public ControladorEntrada(boolean segundoJugador) {
        this.segundoJugador = segundoJugador;
    }

    @Override
    public boolean keyDown(int tecla) {

        if (segundoJugador) {
            switch (tecla) {
                case Input.Keys.I:
                    arriba = true;
                    return true;
                case Input.Keys.K:
                    abajo = true;
                    return true;
                case Input.Keys.J:
                    izquierda = true;
                    return true;
                case Input.Keys.L:
                    derecha = true;
                    return true;
                default:
                    return false;
            }
        }

        switch (tecla) {
            case Input.Keys.W:
                arriba = true;
                pulsacionArriba = true;
                break;
            case Input.Keys.UP:
                pulsacionArriba = true;
                break;
            case Input.Keys.S:
                abajo = true;
                pulsacionAbajo = true;
                break;
            case Input.Keys.DOWN:
                pulsacionAbajo = true;
                break;
            case Input.Keys.A:
                izquierda = true;
                pulsacionIzquierda = true;
                break;
            case Input.Keys.D:
                derecha = true;
                pulsacionDerecha = true;
                break;
            case Input.Keys.ESCAPE:
                pulsacionEscape = true;
                break;
            case Input.Keys.M:
                pulsacionM = true;
                break;
            case Input.Keys.ENTER:
                pulsacionEnter = true;
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    public boolean keyUp(int tecla) {

        if (segundoJugador) {
            switch (tecla) {
                case Input.Keys.I:
                    arriba = false;
                    return true;
                case Input.Keys.K:
                    abajo = false;
                    return true;
                case Input.Keys.J:
                    izquierda = false;
                    return true;
                case Input.Keys.L:
                    derecha = false;
                    return true;
                default:
                    return false;
            }
        }

        switch (tecla) {
            case Input.Keys.W:
                arriba = false;
                break;
            case Input.Keys.S:
                abajo = false;
                break;
            case Input.Keys.A:
                izquierda = false;
                break;
            case Input.Keys.D:
                derecha = false;
                break;
            default:
                return false;
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

    public boolean consumirPulsacionEscape() {
        if (pulsacionEscape) {
            pulsacionEscape = false;
            return true;
        }
        return false;
    }

    public boolean consumirPulsacionM() {
        if (pulsacionM) {
            pulsacionM = false;
            return true;
        }
        return false;
    }

    public boolean consumirPulsacionEnter() {
        if (pulsacionEnter) {
            pulsacionEnter = false;
            return true;
        }
        return false;
    }

    public int consumirSeleccionMejora() {

        int tecla1 = segundoJugador ? Input.Keys.NUM_7 : Input.Keys.NUM_1;
        int tecla2 = segundoJugador ? Input.Keys.NUM_8 : Input.Keys.NUM_2;
        int tecla3 = segundoJugador ? Input.Keys.NUM_9 : Input.Keys.NUM_3;

        if (Gdx.input.isKeyJustPressed(tecla1)) return 1;
        if (Gdx.input.isKeyJustPressed(tecla2)) return 2;
        if (Gdx.input.isKeyJustPressed(tecla3)) return 3;

        return 0;
    }
}