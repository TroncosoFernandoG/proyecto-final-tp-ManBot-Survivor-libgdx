
package com.manbotsurvivor.game.enemigos;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.manbotsurvivor.game.elementos.Mapa;
import com.manbotsurvivor.game.jugador.Jugador;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class NanoBot extends Enemigo {

    private static Texture texturaNanoBot;
    private static Animation<TextureRegion> animacionNanoBot;

    public NanoBot(float posicionX, float posicionY) {

        super(posicionX, posicionY, obtenerAnimacionNanoBot());
    }

    private static Animation<TextureRegion> obtenerAnimacionNanoBot() {

        if (animacionNanoBot == null) {

            texturaNanoBot = new Texture("enjambre.png");

            TextureRegion[][] cuadros = TextureRegion.split(texturaNanoBot, 32, 32);

            TextureRegion[] cuadrosAnimacion = new TextureRegion[8];

            for (int i = 0; i < cuadrosAnimacion.length; i++) {
                cuadrosAnimacion[i] = cuadros[0][i];
            }

            animacionNanoBot = new Animation<TextureRegion>(0.10f, cuadrosAnimacion);

            animacionNanoBot.setPlayMode(Animation.PlayMode.LOOP);
        }

        return animacionNanoBot;
    }

    public static void disponerTextura() {

        if (texturaNanoBot != null) {
            texturaNanoBot.dispose();
            texturaNanoBot = null;
            animacionNanoBot = null;
        }
    }
}

