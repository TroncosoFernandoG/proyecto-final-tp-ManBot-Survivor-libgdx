package com.manbotsurvivor.game.escenas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.manbotsurvivor.game.cliente.DatosJugador;
import com.manbotsurvivor.game.cliente.DatosPartida;
import com.manbotsurvivor.game.cliente.HiloCliente;
import com.manbotsurvivor.game.elementos.EstadoPartida;

public class PanelResultado {

    public void dibujar(SpriteBatch batch, BitmapFont fuente, HiloCliente hiloCliente, float ancho, float alto) {

        DatosPartida partida = hiloCliente.getDatosPartida();
        DatosJugador jugador = hiloCliente.getDatosJugador(1);
        DatosJugador segundoJugador = hiloCliente.getDatosJugador(2);

        int eliminados1 = jugador == null ? 0 : jugador.getEnemigosEliminados();
        int eliminados2 = segundoJugador == null ? 0 : segundoJugador.getEnemigosEliminados();

        String titulo = partida.getEstado() == EstadoPartida.VICTORIA ? "VICTORIA" : "DERROTA";

        int minutos = (int) (partida.getTiempoEnJuego() / 60f);
        int segundos = (int) (partida.getTiempoEnJuego() % 60f);

        batch.begin();

        fuente.setColor(partida.getEstado() == EstadoPartida.VICTORIA ? Color.GREEN : Color.RED);

        fuente.draw(batch, titulo, ancho * 0.43f, alto * 0.90f);

        fuente.setColor(Color.WHITE);

        fuente.draw(batch, String.format("Tiempo: %02d:%02d", minutos, segundos), ancho * 0.34f, alto * 0.75f);

        fuente.draw(batch, "Jugador 1 - Enemigos: " + eliminados1
                + " | Nivel: " + partida.getNivel(), ancho * 0.25f, alto * 0.62f);

        fuente.draw(batch, "Jugador 2 - Enemigos: " + eliminados2
                + " | Nivel: " + partida.getNivel(), ancho * 0.25f, alto * 0.52f);

        fuente.draw(batch, "Total eliminados: " + (eliminados1 + eliminados2), ancho * 0.34f, alto * 0.42f);

        if (hiloCliente.isEsperandoNuevaPartida()) {
            fuente.setColor(Color.YELLOW);
            fuente.draw(batch, "Esperando al otro jugador...", ancho * 0.30f, alto * 0.30f);
            fuente.setColor(Color.WHITE);
        } else {
            fuente.draw(batch, "ENTER - Jugar otra vez", ancho * 0.34f, alto * 0.30f);
        }

        fuente.draw(batch, "M - Volver al menú", ancho * 0.34f, alto * 0.20f);

        batch.end();
    }
}