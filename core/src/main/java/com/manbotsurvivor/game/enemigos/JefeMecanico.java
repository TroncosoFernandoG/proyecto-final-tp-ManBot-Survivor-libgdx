package com.manbotsurvivor.game.enemigos;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.manbotsurvivor.game.elementos.Mapa;
import com.manbotsurvivor.game.jugador.Jugador;

public class JefeMecanico extends Enemigo {

    private static Texture textura;
    private static Animation<TextureRegion> animacion;

    public JefeMecanico(float x, float y) {
        super(x, y, obtenerAnimacion());

        ancho = 112;
        alto = 112;
    }
    
    private static Animation<TextureRegion> obtenerAnimacion() {
        if (animacion == null) {
            textura = new Texture("JefeMecanico.png");

            TextureRegion[][] cuadros = TextureRegion.split(textura, 40, 64);

            TextureRegion[] frames = new TextureRegion[4];

            for (int i = 0; i < frames.length; i++) {
                frames[i] = cuadros[0][i];
            }

            animacion = new Animation<>(0.12f, frames);
            animacion.setPlayMode(Animation.PlayMode.LOOP);
        }

        return animacion;
    }

    public static void disponerTextura() {
        if (textura != null) {
            textura.dispose();
            textura = null;
            animacion = null;
        }
    }
}