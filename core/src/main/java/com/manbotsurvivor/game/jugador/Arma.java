package com.manbotsurvivor.game.jugador;

public abstract class Arma {

    protected int nivel;
    protected int daño;
    protected float intervaloAtaque;
    protected float rangoAtaque;

    public Arma(int dañoInicial, float intervaloInicial, float rangoInicial) {
        nivel = 1;
        daño = dañoInicial;
        intervaloAtaque = intervaloInicial;
        rangoAtaque = rangoInicial;
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

    public float obtenerRangoAtaque() {
        return rangoAtaque;
    }

    public boolean estaAlMaximo() {
        return nivel >= 3;
    }

    public abstract String obtenerNombre();

    public abstract void mejorar();
}