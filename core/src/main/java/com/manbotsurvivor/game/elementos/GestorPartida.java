
package com.manbotsurvivor.game.elementos;

public class GestorPartida {

    private EstadoPartida estado;
    private float tiempoTranscurrido;
    private float tiempoEnJuego;

    public GestorPartida() {
        estado = EstadoPartida.EN_CURSO;
        tiempoTranscurrido = 0;
        tiempoEnJuego = 0;
    }

    public void actualizar(float delta, int vidaJugador) {

        if (estado == EstadoPartida.DERROTA
                || estado == EstadoPartida.VICTORIA) {
            return;
        }

        tiempoTranscurrido += delta;

        if (estado == EstadoPartida.EN_CURSO) {
            tiempoEnJuego += delta;

            if (vidaJugador <= 0) {
                estado = EstadoPartida.DERROTA;
            }
        }
    }

    public void pausar() {
        if (estado == EstadoPartida.EN_CURSO) {
            estado = EstadoPartida.PAUSADA;
        }
    }

    public void reanudar() {
        if (estado == EstadoPartida.PAUSADA) {
            estado = EstadoPartida.EN_CURSO;
        }
    }

    public void registrarVictoria() {
        if (estado == EstadoPartida.EN_CURSO) {
            estado = EstadoPartida.VICTORIA;
        }
    }

    public EstadoPartida obtenerEstado() {
        return estado;
    }

    public float obtenerTiempoTranscurrido() {
        return tiempoTranscurrido;
    }

    public float obtenerTiempoEnJuego() {
        return tiempoEnJuego;
    }

    public boolean estaEnCurso() {
        return estado == EstadoPartida.EN_CURSO;
    }

    public boolean estaPausada() {
        return estado == EstadoPartida.PAUSADA;
    }

    public boolean terminoLaPartida() {
        return estado == EstadoPartida.DERROTA || estado == EstadoPartida.VICTORIA;
    }
}

