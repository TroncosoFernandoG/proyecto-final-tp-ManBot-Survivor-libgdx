package com.manbotsurvivor.game.escenas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.manbotsurvivor.game.elementos.EstadoPartida;
import com.manbotsurvivor.game.elementos.GestorPartida;
import com.manbotsurvivor.game.elementos.Jugador;

public class PanelResultado {

    public void dibujar(SpriteBatch batch, BitmapFont fuente, GestorPartida partida, Jugador jugador, float ancho, float alto) {

        String titulo = partida.obtenerEstado() == EstadoPartida.VICTORIA ? "VICTORIA" : "DERROTA";

        int minutos = (int) (partida.obtenerTiempoEnJuego() / 60f);
        int segundos = (int) (partida.obtenerTiempoEnJuego() % 60f);

        batch.begin();

        fuente.setColor(partida.obtenerEstado() == EstadoPartida.VICTORIA ? Color.GREEN : Color.RED);

        fuente.draw(batch, titulo, ancho * 0.43f, alto * 0.90f);

        fuente.setColor(Color.WHITE);

        fuente.draw(batch, String.format("Tiempo: %02d:%02d", minutos, segundos), ancho * 0.34f, alto * 0.70f);

        fuente.draw(batch, "Enemigos eliminados: " + jugador.obtenerEnemigosEliminados(), ancho * 0.34f, alto * 0.60f);

        fuente.draw(batch, "Nivel alcanzado: " + jugador.obtenerNivel(), ancho * 0.34f, alto * 0.50f);

        fuente.draw(batch, "ENTER - Volver al menú", ancho * 0.34f, alto * 0.40f);

        batch.end();
    }
}
