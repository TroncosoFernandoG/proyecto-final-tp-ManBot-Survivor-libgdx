package com.manbotsurvivor.game.elementos;

public class Granada extends Arma {

    private float radioExplosion;

    public Granada() {
        super(2, 4.0f);
        radioExplosion = 60f;
    }

    public float obtenerRadioExplosion() {
        return radioExplosion;
    }

    @Override
    public String obtenerNombre() {
        return "Granada";
    }

    @Override
    public void mejorar() {
        if (estaAlMaximo()) {
            return;
        }

        nivel++;

        if (nivel == 2) {
            daño = 3;
            radioExplosion = 75f;
            intervaloAtaque = 3.5f;
        } else if (nivel == 3) {
            daño = 5;
            radioExplosion = 90f;
            intervaloAtaque = 3.0f;
        }
    }
}
