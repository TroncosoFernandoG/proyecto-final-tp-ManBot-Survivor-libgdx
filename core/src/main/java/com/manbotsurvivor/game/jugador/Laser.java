package com.manbotsurvivor.game.jugador;

public class Laser extends Arma {

    public Laser() {
        super(1, 1.5f, 220f);
    }

    @Override
    public String obtenerNombre() {
        return "Láser";
    }

    @Override
    public void mejorar() {
        if (estaAlMaximo()) {
            return;
        }

        nivel++;

        if (nivel == 2) {
            daño = 2;
            rangoAtaque = 250f;
        } else if (nivel == 3) {
            daño = 3;
            intervaloAtaque = 1.0f;
            rangoAtaque = 280f;
        }
    }
}