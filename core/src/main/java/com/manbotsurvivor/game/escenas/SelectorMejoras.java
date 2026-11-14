package com.manbotsurvivor.game.escenas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.manbotsurvivor.game.gestores.GestorProgresion;
import com.manbotsurvivor.game.jugador.Mejora;

public class SelectorMejoras {

    public void dibujar(SpriteBatch batch, BitmapFont fuente, GestorProgresion progresion, int numeroJugador, float ancho, float alto) {

        String[] teclas = numeroJugador == 1
                ? new String[] { "1", "2", "3" }
                : new String[] { "7", "8", "9" };

        batch.begin();

        fuente.setColor(Color.YELLOW);
        fuente.draw(batch, "JUGADOR " + numeroJugador + " - SUBISTE DE NIVEL", ancho * 0.30f, alto * 0.68f);

        fuente.setColor(Color.WHITE);
        fuente.draw(batch, "Elegí una mejora:", ancho * 0.35f, alto * 0.61f);

        for (int i = 0; i < progresion.obtenerOpciones().size(); i++) {

            Mejora mejora = progresion.obtenerOpciones().get(i);

            fuente.draw(batch, teclas[i] + " - " + mejora.obtenerDescripcion(), ancho * 0.25f, alto * (0.53f - i * 0.07f));
        }

        fuente.setColor(Color.WHITE);
        batch.end();
    }
}