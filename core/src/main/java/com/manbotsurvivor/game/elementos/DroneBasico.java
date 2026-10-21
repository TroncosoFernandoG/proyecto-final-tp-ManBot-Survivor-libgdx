
package com.manbotsurvivor.game.elementos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DroneBasico extends Enemigo {

    private static Texture texturaDrone;
    private static Animation<TextureRegion> animacionDrone;

    public DroneBasico(float posicionX, float posicionY, float velocidad, Mapa mapa) {

        super(posicionX, posicionY, velocidad, 3, 10, mapa, obtenerAnimacionDrone());
    }

    private static Animation<TextureRegion> obtenerAnimacionDrone() {

        if (animacionDrone == null) {

            texturaDrone = new Texture("dronebasico.png");

            TextureRegion[][] cuadros =
                    TextureRegion.split(texturaDrone, 32, 32);

            TextureRegion[] cuadrosAnimacion = new TextureRegion[4];

            for (int i = 0; i < cuadrosAnimacion.length; i++) {
                cuadrosAnimacion[i] = cuadros[0][i];
            }

            animacionDrone =
                    new Animation<TextureRegion>(0.15f, cuadrosAnimacion);

            animacionDrone.setPlayMode(Animation.PlayMode.LOOP);
        }

        return animacionDrone;
    }

    public static void disponerTextura() {

        if (texturaDrone != null) {
            texturaDrone.dispose();
            texturaDrone = null;
            animacionDrone = null;
        }
    }
}
