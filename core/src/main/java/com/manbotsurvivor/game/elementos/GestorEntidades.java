
package com.manbotsurvivor.game.elementos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GestorEntidades {

    private final Jugador jugador;
    private final List<Enemigo> enemigos;
    private final List<ChipEnergia> chips;

    public GestorEntidades(
            Jugador jugador,
            List<Enemigo> enemigos,
            List<ChipEnergia> chips) {

        this.jugador = jugador;
        this.enemigos = enemigos;
        this.chips = chips;
    }

    public void eliminarEnemigosMuertos() {

        Iterator<Enemigo> iterador = enemigos.iterator();
        while (iterador.hasNext()) {
            Enemigo enemigo = iterador.next();

            if (!enemigo.estaVivo()) {
            	chips.add(new ChipEnergia(enemigo.obtenerPosicionX(), enemigo.obtenerPosicionY()));


                jugador.registrarEnemigoEliminado();

                iterador.remove();
            }
        }
    }

    public void recogerChips() {

        Iterator<ChipEnergia> iterador = chips.iterator();

        while (iterador.hasNext()) {

            ChipEnergia chip = iterador.next();

            if (chip.hayColision(jugador.obtenerPosicionX(), jugador.obtenerPosicionY(), jugador.obtenerAncho(), jugador.obtenerAlto())) {

                jugador.ganarExperiencia(chip.obtenerExperiencia());
                
                iterador.remove();
            }
        }
    }
}

