package com.manbotsurvivor.game.elementos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.manbotsurvivor.game.enemigos.Enemigo;
import com.manbotsurvivor.game.jugador.Granada;
import com.manbotsurvivor.game.jugador.Jugador;
import com.manbotsurvivor.game.jugador.Laser;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SistemaCombate {

    private final Jugador jugador;
    private final List<Enemigo> enemigos;
    private final Color colorProyectil;

    private final Laser laser;
    private final Granada granada;

    private final List<Proyectil> proyectiles = new ArrayList<>();

    private float tiempoLaser;
    private float tiempoGranada;
    private Texture texturaBomba;
    private Texture texturaExplosion;

    private Animation<TextureRegion> animacionBomba;
    private Animation<TextureRegion> animacionExplosion;

    public SistemaCombate(Jugador jugador, List<Enemigo> enemigos) {
        this(jugador, enemigos, Color.RED);
    }

    public SistemaCombate(Jugador jugador, List<Enemigo> enemigos, Color colorProyectil) {
        this.jugador = jugador;
        this.enemigos = enemigos;
        this.colorProyectil = colorProyectil;

        laser = new Laser();
        granada = new Granada();
        texturaBomba = new Texture("bomba_explota.png");
        texturaExplosion = new Texture("explosion.png");
        
        TextureRegion[][] cuadrosBomba = TextureRegion.split(texturaBomba, 16, 16);

        TextureRegion[] framesBomba = new TextureRegion[2];

        for (int i = 0; i < framesBomba.length; i++) {
            framesBomba[i] = cuadrosBomba[0][i];
        }

        animacionBomba = new Animation<TextureRegion>(0.15f, framesBomba);

        animacionBomba.setPlayMode(Animation.PlayMode.LOOP);

        TextureRegion[][] cuadrosExplosion = TextureRegion.split(texturaExplosion, 32, 32);

        TextureRegion[] framesExplosion = new TextureRegion[6];

        for (int i = 0; i < framesExplosion.length; i++) {
            framesExplosion[i] = cuadrosExplosion[0][i];
        }

        animacionExplosion = new Animation<TextureRegion>(0.047f, framesExplosion);

        animacionExplosion.setPlayMode(Animation.PlayMode.NORMAL);
    }

    public Laser obtenerLaser() {
        return laser;
    }

    public Granada obtenerGranada() {
        return granada;
    }

    private Enemigo obtenerEnemigoMasCercano(float rango) {
        Enemigo cercano = null;
        float distanciaMinima = rango * rango;

        float centroJugadorX = jugador.obtenerPosicionX() + jugador.obtenerAncho() / 2f;
        float centroJugadorY = jugador.obtenerPosicionY() + jugador.obtenerAlto() / 2f;

        for (Enemigo enemigo : enemigos) {
            if (!enemigo.estaVivo()) {
                continue;
            }

            float dx = enemigo.obtenerPosicionX() + enemigo.obtenerAncho() / 2f
                    - centroJugadorX;

            float dy = enemigo.obtenerPosicionY() + enemigo.obtenerAlto() / 2f
                    - centroJugadorY;

            float distancia = dx * dx + dy * dy;

            if (distancia <= distanciaMinima) {
                distanciaMinima = distancia;
                cercano = enemigo;
            }
        }

        return cercano;
    }

    private boolean crearProyectil(Enemigo objetivo, boolean esGranada) {
        if (objetivo == null) {
            return false;
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
            return false;
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
            proyectil.distanciaMaxima = granada.obtenerRangoAtaque() * 1.5f;
        } else {
            proyectil.velocidad = 240f;
            proyectil.daño = laser.obtenerDaño();
            proyectil.radio = 0f;
            proyectil.distanciaMaxima = laser.obtenerRangoAtaque() * 1.5f;
        }

        proyectiles.add(proyectil);
        return true;
    }

    public void actualizar(float delta) {

        if (jugador.estaVivo()) {

            tiempoLaser += delta;
            tiempoGranada += delta;

            // Solo se reinicia el temporizador si realmente se disparó:
            // así el arma dispara apenas un enemigo entra en rango.
            if (tiempoLaser >= laser.obtenerIntervaloAtaque()) {
                if (crearProyectil(obtenerEnemigoMasCercano(laser.obtenerRangoAtaque()), false)) {
                    tiempoLaser = 0f;
                }
            }

            if (tiempoGranada >= granada.obtenerIntervaloAtaque()) {
                if (crearProyectil(obtenerEnemigoMasCercano(granada.obtenerRangoAtaque()), true)) {
                    tiempoGranada = 0f;
                }
            }
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

            float pasoX = proyectil.dx * proyectil.velocidad * delta;
            float pasoY = proyectil.dy * proyectil.velocidad * delta;

            proyectil.x += pasoX;
            proyectil.y += pasoY;
            proyectil.distanciaRecorrida += (float) Math.sqrt(pasoX * pasoX + pasoY * pasoY);
            
            if (proyectil.granada) {
                proyectil.tiempoAnimacionBomba += delta;
            }

            // Si el proyectil se pasó de largo (el enemigo se movió), desaparece
            if (proyectil.distanciaRecorrida > proyectil.distanciaMaxima) {
                if (proyectil.granada) {
                    explotar(proyectil);
                } else {
                    iterador.remove();
                }
                continue;
            }

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
                    proyectil.objetivo.recibirDaño(proyectil.daño, jugador);
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
                enemigo.recibirDaño(proyectil.daño, jugador);
            }
        }
    }

    public void dibujarDisparo(ShapeRenderer renderer) {
        for (Proyectil proyectil : proyectiles) {
        	if (proyectil.granada) {
                continue;
            }

            renderer.setColor(colorProyectil);

            renderer.circle(proyectil.x, proyectil.y, 3f);
            
        }
    }

    // Dibuja el círculo del rango de ataque (usar con ShapeType.Line)
    public void dibujarRango(ShapeRenderer renderer) {

        if (!jugador.estaVivo()) {
            return;
        }

        float centroX = jugador.obtenerPosicionX() + jugador.obtenerAncho() / 2f;
        float centroY = jugador.obtenerPosicionY() + jugador.obtenerAlto() / 2f;

        renderer.setColor(colorProyectil.r, colorProyectil.g, colorProyectil.b, 0.35f);
        renderer.circle(centroX, centroY, laser.obtenerRangoAtaque(), 48);

        renderer.setColor(1f, 0.6f, 0f, 0.25f);
        renderer.circle(centroX, centroY, granada.obtenerRangoAtaque(), 48);
    }
    
    public void dibujarGranadas(SpriteBatch batch) {

        for (Proyectil proyectil : proyectiles) {

            if (!proyectil.granada) {
                continue;
            }

            if (proyectil.explotado) {

                TextureRegion cuadroActual =  animacionExplosion.getKeyFrame(proyectil.tiempoExplosion, false);

                float tamaño = proyectil.radio * 2;

                batch.draw(cuadroActual, proyectil.x - tamaño / 2, proyectil.y - tamaño / 2, tamaño, tamaño);

            } else {

                TextureRegion cuadroActual = animacionBomba.getKeyFrame(proyectil.tiempoAnimacionBomba);

                batch.draw(cuadroActual, proyectil.x - 8, proyectil.y - 8, 16, 16);
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
        float tiempoAnimacionBomba;
        float distanciaRecorrida;
        float distanciaMaxima;

        int daño;

        boolean granada;
        boolean explotado;

        Enemigo objetivo;
    }
    
    public void disponer() {

        texturaBomba.dispose();
        texturaExplosion.dispose();
    }
}