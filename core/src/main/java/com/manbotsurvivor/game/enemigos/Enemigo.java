package com.manbotsurvivor.game.enemigos;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

// Enemigo del CLIENTE: solo se dibuja.
// La persecucion, el daño y la vida los calcula el servidor.
public abstract class Enemigo {

    private static final float VELOCIDAD_SUAVIZADO = 15f;
    private static final float DISTANCIA_MAXIMA_SUAVIZADO = 150f;

    protected float posicionX;
    protected float posicionY;

    protected float ancho;
    protected float alto;

    protected Animation<TextureRegion> animacionEnemigo;
    protected float tiempoAnimacion;

    protected Enemigo(float posicionX, float posicionY, Animation<TextureRegion> animacionEnemigo) {

        this.posicionX = posicionX;
        this.posicionY = posicionY;
        this.ancho = 32;
        this.alto = 32;
        this.animacionEnemigo = animacionEnemigo;
        this.tiempoAnimacion = 0;
    }

    // Se acerca suavemente a la posicion que mando el servidor
    public void asignarPosicion(float x, float y, float delta) {

        float diferenciaX = x - posicionX;
        float diferenciaY = y - posicionY;

        if (diferenciaX * diferenciaX + diferenciaY * diferenciaY > DISTANCIA_MAXIMA_SUAVIZADO * DISTANCIA_MAXIMA_SUAVIZADO) {
            posicionX = x;
            posicionY = y;
            return;
        }

        float factor = Math.min(1f, delta * VELOCIDAD_SUAVIZADO);

        posicionX += diferenciaX * factor;
        posicionY += diferenciaY * factor;
    }

    public void animar(float delta) {
        tiempoAnimacion += delta;
    }

    public void dibujar(OrthographicCamera camaraJuego, SpriteBatch lote) {

        TextureRegion cuadroActual =
                animacionEnemigo.getKeyFrame(tiempoAnimacion);

        lote.draw(cuadroActual, posicionX, posicionY, ancho, alto);
    }
}