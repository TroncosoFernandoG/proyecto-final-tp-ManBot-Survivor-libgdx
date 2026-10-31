package com.manbotsurvivor.game.elementos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class SistemaCombate {

    private final Jugador jugador;
    private final List<Enemigo> enemigos;

    private final Laser laser;
    private final Granada granada;

    private final List<Proyectil> proyectiles = new ArrayList<>();

    private float tiempoLaser;
    private float tiempoGranada;

    public SistemaCombate(Jugador jugador, List<Enemigo> enemigos) {
        this.jugador = jugador;
        this.enemigos = enemigos;

        laser = new Laser();
        granada = new Granada();
    }

    public Laser obtenerLaser() {
        return laser;
    }

    public Granada obtenerGranada() {
        return granada;
    }

    private Enemigo obtenerEnemigoMasCercano() {
        Enemigo cercano = null;
        float distanciaMinima = Float.MAX_VALUE;

        for (Enemigo enemigo : enemigos) {
            if (!enemigo.estaVivo()) {
                continue;
            }

            float dx = enemigo.obtenerPosicionX()
                    - jugador.obtenerPosicionX();

            float dy = enemigo.obtenerPosicionY()
                    - jugador.obtenerPosicionY();

            float distancia = (float) Math.sqrt(dx * dx + dy * dy);

            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                cercano = enemigo;
            }
        }

        return cercano;
    }

    private void crearProyectil(Enemigo objetivo, boolean esGranada) {
        if (objetivo == null) {
            return;
        }

        float x = jugador.obtenerPosicionX()
                + jugador.obtenerAncho() / 2f;

        float y = jugador.obtenerPosicionY()
                + jugador.obtenerAlto() / 2f;

        float destinoX = objetivo.obtenerPosicionX()
                + objetivo.obtenerAncho() / 2f;

        float destinoY = objetivo.obtenerPosicionY()
                + objetivo.obtenerAlto() / 2f;

        float dx = destinoX - x;
        float dy = destinoY - y;

        float distancia = (float) Math.sqrt(dx * dx + dy * dy);

        if (distancia <= 0) {
            return;
        }

        Proyectil proyectil = new Proyectil();

        proyectil.x = x;
        proyectil.y = y;
        proyectil.dx = dx / distancia;
        proyectil.dy = dy / distancia;
        proyectil.objetivo = objetivo;
        proyectil.granada = esGranada;

        if (esGranada) {
            proyectil.velocidad = 145f;
            proyectil.daño = granada.obtenerDaño();
            proyectil.radio = granada.obtenerRadioExplosion();
        } else {
            proyectil.velocidad = 240f;
            proyectil.daño = laser.obtenerDaño();
            proyectil.radio = 0f;
        }

        proyectiles.add(proyectil);
    }

    public void actualizar(float delta) {
        tiempoLaser += delta;
        tiempoGranada += delta;

        if (tiempoLaser >= laser.obtenerIntervaloAtaque()) {
            crearProyectil(obtenerEnemigoMasCercano(), false);
            tiempoLaser = 0f;
        }

        if (tiempoGranada >= granada.obtenerIntervaloAtaque()) {
            crearProyectil(obtenerEnemigoMasCercano(), true);
            tiempoGranada = 0f;
        }

        actualizarProyectiles(delta);
    }

    private void actualizarProyectiles(float delta) {
        Iterator<Proyectil> iterador = proyectiles.iterator();

        while (iterador.hasNext()) {
            Proyectil proyectil = iterador.next();

            if (proyectil.explotado) {
                proyectil.tiempoExplosion += delta;

                if (proyectil.tiempoExplosion >= 0.28f) {
                    iterador.remove();
                }

                continue;
            }

            if (proyectil.objetivo == null
                    || !proyectil.objetivo.estaVivo()) {
                iterador.remove();
                continue;
            }

            proyectil.x += proyectil.dx * proyectil.velocidad * delta;
            proyectil.y += proyectil.dy * proyectil.velocidad * delta;

            Rectangle areaProyectil = new Rectangle(
                    proyectil.x - 3,
                    proyectil.y - 3,
                    6,
                    6
            );

            Rectangle areaObjetivo = new Rectangle(
                    proyectil.objetivo.obtenerPosicionX(),
                    proyectil.objetivo.obtenerPosicionY(),
                    proyectil.objetivo.obtenerAncho(),
                    proyectil.objetivo.obtenerAlto()
            );

            if (areaProyectil.overlaps(areaObjetivo)) {
                if (!proyectil.granada) {
                    proyectil.objetivo.recibirDaño(proyectil.daño);
                    iterador.remove();
                } else {
                    explotar(proyectil);
                }
            }
        }
    }

    private void explotar(Proyectil proyectil) {
        if (proyectil.explotado) {
            return;
        }

        proyectil.explotado = true;
        proyectil.tiempoExplosion = 0f;

        for (Enemigo enemigo : enemigos) {
            if (!enemigo.estaVivo()) {
                continue;
            }

            float dx = enemigo.obtenerPosicionX()
                    + enemigo.obtenerAncho() / 2f
                    - proyectil.x;

            float dy = enemigo.obtenerPosicionY()
                    + enemigo.obtenerAlto() / 2f
                    - proyectil.y;

            if (dx * dx + dy * dy
                    <= proyectil.radio * proyectil.radio) {
                enemigo.recibirDaño(proyectil.daño);
            }
        }
    }

    public void dibujarDisparo(ShapeRenderer renderer) {
        for (Proyectil proyectil : proyectiles) {
            if (proyectil.explotado) {
                renderer.setColor(Color.ORANGE);
                renderer.circle(
                        proyectil.x,
                        proyectil.y,
                        proyectil.radio
                );
            } else {
                renderer.setColor(
                        proyectil.granada ? Color.ORANGE : Color.RED
                );

                renderer.circle(
                        proyectil.x,
                        proyectil.y,
                        proyectil.granada ? 5f : 3f
                );
            }
        }
    }

    private static class Proyectil {
        float x;
        float y;
        float dx;
        float dy;
        float velocidad;
        float radio;
        float tiempoExplosion;

        int daño;

        boolean granada;
        boolean explotado;

        Enemigo objetivo;
    }
}