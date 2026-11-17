package com.manbotsurvivor.game.elementos;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.manbotsurvivor.game.cliente.DatosProyectil;
import com.manbotsurvivor.game.cliente.ProtocolosRed;

	// Dibuja los disparos, granadas y explosiones que manda el servidor.
	// El combate (a quien se dispara, daño, impactos) se calcula en el servidor.
	public class DibujoProyectiles {

    private static final Color[] COLORES_LASER = { Color.RED, Color.CYAN };

    private Texture texturaBomba;
    private Texture texturaExplosion;

    private Animation<TextureRegion> animacionBomba;
    private Animation<TextureRegion> animacionExplosion;

    private float tiempoAnimacionBomba;

    public DibujoProyectiles() {
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

    public void animar(float delta) {
        tiempoAnimacionBomba += delta;
    }

    public void dibujarGranadas(SpriteBatch batch, List<DatosProyectil> proyectiles) {

        for (DatosProyectil proyectil : proyectiles) {

            if (proyectil.getTipo().equals(ProtocolosRed.PROYECTIL_GRANADA)) {

                TextureRegion cuadroActual = animacionBomba.getKeyFrame(tiempoAnimacionBomba);

                batch.draw(cuadroActual, proyectil.getX() - 8, proyectil.getY() - 8, 16, 16);

            } else if (proyectil.getTipo().equals(ProtocolosRed.PROYECTIL_EXPLOSION)) {

                TextureRegion cuadroActual = animacionExplosion.getKeyFrame(proyectil.getTiempoExplosion(), false);

                float tamaño = proyectil.getRadio() * 2;

                batch.draw(cuadroActual, proyectil.getX() - tamaño / 2, proyectil.getY() - tamaño / 2, tamaño, tamaño);
            }
        }
    }

    public void dibujarDisparos(ShapeRenderer renderer, List<DatosProyectil> proyectiles) {

        for (DatosProyectil proyectil : proyectiles) {

            if (!proyectil.getTipo().equals(ProtocolosRed.PROYECTIL_LASER)) {
                continue;
            }

            renderer.setColor(COLORES_LASER[proyectil.getNumeroJugador() - 1]);
            renderer.circle(proyectil.getX(), proyectil.getY(), 3f);
        }
    }

    public void disponer() {
        texturaBomba.dispose();
        texturaExplosion.dispose();
    }
}
