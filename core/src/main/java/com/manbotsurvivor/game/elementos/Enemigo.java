
package com.manbotsurvivor.game.elementos;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public abstract class Enemigo {

    protected float posicionX;
    protected float posicionY;
    protected float velocidad;

    protected float ancho;
    protected float alto;
    protected int vida;

    protected Rectangle hitbox;
    protected final int DAÑO;
    protected float tiempoDesdeUltimoDaño;
    protected final float TIEMPO_ENTRE_DAÑOS;

    protected Animation<TextureRegion> animacionEnemigo;
    protected float tiempoAnimacion;

    protected Mapa mapa;

    protected Enemigo(float posicionX, float posicionY, float velocidad, int vida, int daño, Mapa mapa, Animation<TextureRegion> animacionEnemigo) {

        this.posicionX = posicionX;
        this.posicionY = posicionY;
        this.velocidad = velocidad;
        this.ancho = 32;
        this.alto = 32;
        this.vida = vida;
        this.mapa = mapa;
        this.hitbox = new Rectangle(posicionX, posicionY, ancho, alto);
        this.DAÑO = daño;
        this.TIEMPO_ENTRE_DAÑOS = 1;
        this.tiempoDesdeUltimoDaño = 1;
        this.animacionEnemigo = animacionEnemigo;
        this.tiempoAnimacion = 0;
    }

    public float obtenerPosicionX() {
        return posicionX;
    }

    public float obtenerPosicionY() {
        return posicionY;
    }

    public float obtenerAncho() {
        return ancho;
    }

    public float obtenerAlto() {
        return alto;
    }

    public Rectangle obtenerHitbox() {
        return hitbox;
    }

    public int obtenerDaño() {
        return DAÑO;
    }

    public void recibirDaño(int daño) {
        vida -= daño;

        if (vida < 0) {
            vida = 0;
        }
    }

    public boolean estaVivo() {
        return vida > 0;
    }
    
    public void aumentarDificultad(float factor) {
        velocidad *= factor;
        vida = Math.max(1, Math.round(vida * factor));
    }

    public void intentarHacerDaño(Jugador jugador, float delta) {

        tiempoDesdeUltimoDaño += delta;

        Rectangle hitboxJugador = new Rectangle(
                jugador.obtenerPosicionX(),
                jugador.obtenerPosicionY(),
                jugador.obtenerAncho(),
                jugador.obtenerAlto());

        if (hitbox.overlaps(hitboxJugador)
                && tiempoDesdeUltimoDaño >= TIEMPO_ENTRE_DAÑOS) {

            jugador.recibirDaño(DAÑO);
            tiempoDesdeUltimoDaño = 0;
        }
    }

    public void mover(
            float movimientoX,
            float movimientoY,
            Jugador jugador,
            ArrayList<Enemigo> enemigos) {

        float nuevaPosicionX = posicionX + movimientoX;
        float nuevaPosicionY = posicionY + movimientoY;

        Rectangle futuraHitboxX = new Rectangle(
                nuevaPosicionX, posicionY, ancho, alto);

        if (!mapa.hayColision(nuevaPosicionX, posicionY, ancho, alto)
                && !hayColisionConOtroEnemigo(futuraHitboxX, enemigos)) {

            posicionX = nuevaPosicionX;
        }

        Rectangle futuraHitboxY = new Rectangle(
                posicionX, nuevaPosicionY, ancho, alto);

        if (!mapa.hayColision(posicionX, nuevaPosicionY, ancho, alto)
                && !hayColisionConOtroEnemigo(futuraHitboxY, enemigos)) {

            posicionY = nuevaPosicionY;
        }

        hitbox.setPosition(posicionX, posicionY);
    }

    public void perseguir(
            Jugador jugador,
            float delta,
            ArrayList<Enemigo> enemigos) {

        float diferenciaX = jugador.obtenerPosicionX() - posicionX;
        float diferenciaY = jugador.obtenerPosicionY() - posicionY;

        float movimientoX = 0;
        float movimientoY = 0;

        if (diferenciaX > 0) {
            movimientoX = 1;
        }

        if (diferenciaX < 0) {
            movimientoX = -1;
        }

        if (diferenciaY > 0) {
            movimientoY = 1;
        }

        if (diferenciaY < 0) {
            movimientoY = -1;
        }

        float longitudMovimiento = (float) Math.sqrt(
                movimientoX * movimientoX + movimientoY * movimientoY);

        if (longitudMovimiento > 0) {
            movimientoX = movimientoX / longitudMovimiento * velocidad * delta;
            movimientoY = movimientoY / longitudMovimiento * velocidad * delta;
        }

        mover(movimientoX, movimientoY, jugador, enemigos);
    }

    private boolean hayColisionConOtroEnemigo(
            Rectangle futuraHitbox,
            ArrayList<Enemigo> enemigos) {

        for (Enemigo enemigo : enemigos) {

            if (enemigo == this) {
                continue;
            }

            if (futuraHitbox.overlaps(enemigo.obtenerHitbox())) {
                return true;
            }
        }

        return false;
    }

    public void dibujar(OrthographicCamera camaraJuego, SpriteBatch lote) {

        TextureRegion cuadroActual =
                animacionEnemigo.getKeyFrame(tiempoAnimacion);

        lote.draw(cuadroActual, posicionX, posicionY, ancho, alto);
    }

    public void actualizar(
            float delta,
            Jugador jugador,
            ArrayList<Enemigo> enemigos) {

        perseguir(jugador, delta, enemigos);
        intentarHacerDaño(jugador, delta);
        tiempoAnimacion += delta;
    }
}
