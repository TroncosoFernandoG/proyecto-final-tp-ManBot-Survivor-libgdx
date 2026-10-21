
package com.manbotsurvivor.game.elementos;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class NanoBot extends Enemigo {

    private static Texture texturaNanoBot;
    private static Animation<TextureRegion> animacionNanoBot;

    public NanoBot(float posicionX, float posicionY, Mapa mapa) {

        super(posicionX, posicionY, 85, 1, 5, mapa, obtenerAnimacionNanoBot());
    }

    private static Animation<TextureRegion> obtenerAnimacionNanoBot() {

        if (animacionNanoBot == null) {

            texturaNanoBot = new Texture("dronebasico.png");

            TextureRegion[][] cuadros = TextureRegion.split(texturaNanoBot, 32, 32);

            TextureRegion[] cuadrosAnimacion = new TextureRegion[4];

            for (int i = 0; i < cuadrosAnimacion.length; i++) {
                cuadrosAnimacion[i] = cuadros[0][i];
            }

            animacionNanoBot = new Animation<TextureRegion>(0.10f, cuadrosAnimacion);

            animacionNanoBot.setPlayMode(Animation.PlayMode.LOOP);
        }

        return animacionNanoBot;
    }

    @Override
    public void dibujar(OrthographicCamera camaraJuego, SpriteBatch lote) {

        lote.setColor(Color.CYAN);

        super.dibujar(camaraJuego, lote);

        lote.setColor(Color.WHITE);
    }

    @Override
    public void actualizar(
            float delta,
            Jugador jugador,
            ArrayList<Enemigo> enemigos) {

        super.actualizar(delta, jugador, enemigos);
    }

    public static void disponerTextura() {

        if (texturaNanoBot != null) {
            texturaNanoBot.dispose();
            texturaNanoBot = null;
            animacionNanoBot = null;
        }
    }
}

