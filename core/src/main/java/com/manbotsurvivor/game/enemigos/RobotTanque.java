package com.manbotsurvivor.game.enemigos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class RobotTanque extends Enemigo {

    private static Texture texturaTanque;
    private static Animation<TextureRegion> animacionTanque;

    public RobotTanque(float posicionX, float posicionY) {

        super(posicionX, posicionY, obtenerAnimacionTanque());
        ancho=64;
        alto=64;
    }

    private static Animation<TextureRegion> obtenerAnimacionTanque() {

        if (animacionTanque == null) {

            texturaTanque = new Texture("Tanque.png");

            TextureRegion[][] cuadros =
                TextureRegion.split(texturaTanque, 32, 32);

            TextureRegion[] cuadrosAnimacion = new TextureRegion[4];

            for (int i = 0; i < cuadrosAnimacion.length; i++) {
                cuadrosAnimacion[i] = cuadros[0][i];
            }

            animacionTanque = new Animation<TextureRegion>(
                0.15f,
                cuadrosAnimacion
            );

            animacionTanque.setPlayMode(Animation.PlayMode.LOOP);
        }

        return animacionTanque;
    }

    public static void disponerTextura() {

        if (texturaTanque != null) {
            texturaTanque.dispose();
            texturaTanque = null;
            animacionTanque = null;
        }
    }
}