package com.manbotsurvivor.game.elementos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestorProgresion {

    private final Jugador jugador;
    private final SistemaCombate combate;
    private final GestorPartida partida;

    private final List<Mejora> opciones = new ArrayList<>();

    private boolean seleccionActiva;

    public GestorProgresion(
            Jugador jugador,
            SistemaCombate combate,
            GestorPartida partida) {

        this.jugador = jugador;
        this.combate = combate;
        this.partida = partida;
    }

    public void actualizar() {
        if (seleccionActiva || !jugador.hayNivelPendiente() || partida.terminoLaPartida()) {
            return;
        }

        if (partida.estaEnCurso()) {
            partida.pausar();
        }

        generarOpciones();
        seleccionActiva = true;
    }

    private void generarOpciones() {
        List<Mejora> disponibles = new ArrayList<>();

        if (!combate.obtenerLaser().estaAlMaximo()) {
            disponibles.add(Mejora.MEJORAR_LASER);
        }

        if (!combate.obtenerGranada().estaAlMaximo()) {
            disponibles.add(Mejora.MEJORAR_GRANADA);
        }

        disponibles.add(Mejora.RECUPERAR_VIDA);
        disponibles.add(Mejora.AUMENTAR_VELOCIDAD);

        Collections.shuffle(disponibles);

        opciones.clear();

        for (int i = 0; i < 3 && i < disponibles.size(); i++) {
            opciones.add(disponibles.get(i));
        }
    }

    public void elegir(int numero) {
        if (!seleccionActiva
                || numero < 1
                || numero > opciones.size()) {
            return;
        }

        aplicar(opciones.get(numero - 1));

        jugador.consumirNivelPendiente();
        seleccionActiva = false;

        if (jugador.hayNivelPendiente()) {
            generarOpciones();
            seleccionActiva = true;
        } else if (partida.estaPausada()) {
            partida.reanudar();
        }
    }

    private void aplicar(Mejora mejora) {
        switch (mejora) {
            case MEJORAR_LASER:
                combate.obtenerLaser().mejorar();
                break;

            case MEJORAR_GRANADA:
                combate.obtenerGranada().mejorar();
                break;

            case RECUPERAR_VIDA:
                jugador.curar(30);
                break;

            case AUMENTAR_VELOCIDAD:
                jugador.aumentarVelocidad(10f);
                break;

            default:
                break;
        }
    }

    public boolean haySeleccionActiva() {
        return seleccionActiva;
    }

    public List<Mejora> obtenerOpciones() {
        return Collections.unmodifiableList(opciones);
    }

}
