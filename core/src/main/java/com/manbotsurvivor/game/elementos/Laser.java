package com.manbotsurvivor.game.elementos;

public class Laser extends Arma {

    public Laser() {
        super(1, 1.5f);
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
        } else if (nivel == 3) {
            daño = 3;
            intervaloAtaque = 1.0f;
        }
    }
}