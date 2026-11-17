package com.manbotsurvivor.game.escenas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.manbotsurvivor.game.cliente.DatosSeleccion;
import com.manbotsurvivor.game.jugador.Mejora;

public class SelectorMejoras {

    // Muestra las opciones que genero el servidor para este jugador (teclas 1, 2 y 3)
    public void dibujar(SpriteBatch batch, BitmapFont fuente, DatosSeleccion seleccion, int numeroJugador, float ancho, float alto) {

        batch.begin();

        fuente.setColor(Color.YELLOW);
        fuente.draw(batch, "JUGADOR " + numeroJugador + " - SUBISTE DE NIVEL", ancho * 0.30f, alto * 0.68f);

        fuente.setColor(Color.WHITE);
        fuente.draw(batch, "Elegí una mejora:", ancho * 0.35f, alto * 0.61f);

        for (int i = 0; i < seleccion.getOpciones().size(); i++) {

            Mejora mejora = seleccion.getOpciones().get(i);

            fuente.draw(batch, (i + 1) + " - " + mejora.obtenerDescripcion(), ancho * 0.25f, alto * (0.53f - i * 0.07f));
        }

        fuente.setColor(Color.WHITE);
        batch.end();
    }

    // Este jugador ya eligio pero el compañero todavia no
    public void dibujarEspera(SpriteBatch batch, BitmapFont fuente, float ancho, float alto) {

        batch.begin();

        fuente.setColor(Color.YELLOW);
        fuente.draw(batch, "Esperando que tu compañero elija su mejora...", ancho * 0.18f, alto * 0.55f);

        fuente.setColor(Color.WHITE);
        batch.end();
    }
}