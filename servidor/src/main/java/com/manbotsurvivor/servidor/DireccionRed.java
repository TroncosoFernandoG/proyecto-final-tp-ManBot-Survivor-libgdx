package com.manbotsurvivor.servidor;

import java.net.InetAddress;

public class DireccionRed {

    InetAddress ip;
    int puerto;
    int numeroJugador;
    long ultimoMensaje;
    boolean conectado;

    public DireccionRed(InetAddress ip, int puerto, int numeroJugador) {

        this.ip = ip;
        this.puerto = puerto;
        this.numeroJugador = numeroJugador;

        ultimoMensaje = System.currentTimeMillis();

        conectado = true;
    }

    public boolean coincide(InetAddress otraIp, int otroPuerto) {

        return ip.equals(otraIp) && puerto == otroPuerto;
    }
}