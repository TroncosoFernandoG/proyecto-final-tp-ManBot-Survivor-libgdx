package com.manbotsurvivor.game.enemigos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.manbotsurvivor.game.elementos.Mapa;


public class RobotTanque extends Enemigo {

    private static Texture texturaTanque;
    private static Animation<TextureRegion> animacionTanque;

    public RobotTanque(float posicionX, float posicionY, float velocidad, Mapa mapa) {

        super(posicionX, posicionY, velocidad, 10, 25, mapa, obtenerAnimacionTanque());
        ancho=64;
        alto=64;
        hitbox.setSize(ancho, alto);
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