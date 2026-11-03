package com.manbotsurvivor.game.elementos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class JefeMecanico extends Enemigo {

    private static Texture textura;
    private static Animation<TextureRegion> animacion;

    public JefeMecanico(float x, float y, Mapa mapa) {
        super(x, y, 28f, 180, 25, mapa, obtenerAnimacion());

        ancho = 48f;
        alto = 48f;
        hitbox.setSize(ancho, alto);
    }

    private static Animation<TextureRegion> obtenerAnimacion() {
        if (animacion == null) {
            textura = new Texture("dronebasico.png");

            TextureRegion[][] cuadros = TextureRegion.split(textura, 32, 32);

            TextureRegion[] frames = new TextureRegion[4];

            for (int i = 0; i < frames.length; i++) {
                frames[i] = cuadros[0][i];
            }

            animacion = new Animation<>(0.12f, frames);
            animacion.setPlayMode(Animation.PlayMode.LOOP);
        }

        return animacion;
    }

    @Override
    public void dibujar(OrthographicCamera camara, SpriteBatch lote) {

        lote.setColor(Color.ORANGE);
        super.dibujar(camara, lote);
        lote.setColor(Color.WHITE);
    }

    public static void disponerTextura() {
        if (textura != null) {
            textura.dispose();
            textura = null;
            animacion = null;
        }
    }
}