
package com.manbotsurvivor.game.elementos;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class SistemaCombate {

    private final Jugador jugador;
    private final List<Enemigo> enemigos;

    private float tiempoAtaque;
    private final float INTERVALO_ATAQUE = 1.5f;

    private boolean hayDisparo;
    private float posicionDisparoX;
    private float posicionDisparoY;

    private Enemigo enemigoObjetivoDisparo;

    private float direccionDisparoX;
    private float direccionDisparoY;
    private float velocidadDisparo;

    public SistemaCombate(Jugador jugador, List<Enemigo> enemigos) {
        this.jugador = jugador;
        this.enemigos = enemigos;

        tiempoAtaque = 0;
        hayDisparo = false;

        posicionDisparoX = 0;
        posicionDisparoY = 0;

        enemigoObjetivoDisparo = null;

        direccionDisparoX = 0;
        direccionDisparoY = 0;

        velocidadDisparo = 200;
    }

    private Enemigo obtenerEnemigoMasCercano() {
        Enemigo enemigoMasCercano = null;
        double distanciaMinima = Double.MAX_VALUE;

        for (Enemigo enemigo : enemigos) {
            double diferenciaX =
                enemigo.obtenerPosicionX() - jugador.obtenerPosicionX();

            double diferenciaY =
                enemigo.obtenerPosicionY() - jugador.obtenerPosicionY();

            double distancia = Math.sqrt(
                diferenciaX * diferenciaX + diferenciaY * diferenciaY
            );

            if (enemigo.estaVivo() && distancia < distanciaMinima) {
                distanciaMinima = distancia;
                enemigoMasCercano = enemigo;
            }
        }

        return enemigoMasCercano;
    }

    private void atacar() {
        Enemigo enemigoCercano = obtenerEnemigoMasCercano();

        if (enemigoCercano == null) {
            return;
        }

        hayDisparo = true;

        posicionDisparoX =
            jugador.obtenerPosicionX() + jugador.obtenerAncho() / 2;

        posicionDisparoY =
            jugador.obtenerPosicionY() + jugador.obtenerAlto() / 2;

        enemigoObjetivoDisparo = enemigoCercano;

        float diferenciaX =
            enemigoObjetivoDisparo.obtenerPosicionX() - posicionDisparoX;

        float diferenciaY =
            enemigoObjetivoDisparo.obtenerPosicionY() - posicionDisparoY;

        float distancia = (float) Math.sqrt(
            diferenciaX * diferenciaX + diferenciaY * diferenciaY
        );

        if (distancia > 0) {
            direccionDisparoX = diferenciaX / distancia;
            direccionDisparoY = diferenciaY / distancia;
        } else {
            direccionDisparoX = 0;
            direccionDisparoY = 0;
        }
    }

    private void actualizarDisparo(float delta) {
        if (!hayDisparo) {
            return;
        }

        posicionDisparoX += direccionDisparoX * velocidadDisparo * delta;
        posicionDisparoY += direccionDisparoY * velocidadDisparo * delta;
    }

    private void comprobarColisionDisparo() {
        if (!hayDisparo || enemigoObjetivoDisparo == null) {
            return;
        }

        Rectangle areaDisparo = new Rectangle(
            posicionDisparoX,
            posicionDisparoY,
            6,
            6
        );

        Rectangle areaEnemigo = new Rectangle(
            enemigoObjetivoDisparo.obtenerPosicionX(),
            enemigoObjetivoDisparo.obtenerPosicionY(),
            enemigoObjetivoDisparo.obtenerAncho(),
            enemigoObjetivoDisparo.obtenerAlto()
        );

        if (areaDisparo.overlaps(areaEnemigo)) {
            enemigoObjetivoDisparo.recibirDaño(1);

            hayDisparo = false;
            enemigoObjetivoDisparo = null;
        }
    }

    public void actualizar(float delta) {
        tiempoAtaque += delta;

        if (tiempoAtaque >= INTERVALO_ATAQUE) {
            atacar();
            tiempoAtaque = 0;
        }

        actualizarDisparo(delta);
        comprobarColisionDisparo();
    }

    public void dibujarDisparo(ShapeRenderer formaEnemigo) {
        if (hayDisparo) {
            formaEnemigo.setColor(Color.RED);
            formaEnemigo.rect(
                posicionDisparoX,
                posicionDisparoY,
                6,
                6
            );
        }
    }
}

