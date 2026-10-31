package com.manbotsurvivor.game.elementos;

public abstract class Arma {

    protected int nivel;
    protected int daño;
    protected float intervaloAtaque;

    public Arma(int dañoInicial, float intervaloInicial) {
        nivel = 1;
        daño = dañoInicial;
        intervaloAtaque = intervaloInicial;
    }

    public int obtenerNivel() {
        return nivel;
    }

    public int obtenerDaño() {
        return daño;
    }

    public float obtenerIntervaloAtaque() {
        return intervaloAtaque;
    }

    public boolean estaAlMaximo() {
        return nivel >= 3;
    }

    public abstract String obtenerNombre();

    public abstract void mejorar();
}
