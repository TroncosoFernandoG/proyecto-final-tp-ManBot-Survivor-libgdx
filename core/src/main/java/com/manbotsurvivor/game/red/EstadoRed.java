package com.manbotsurvivor.game.red;

public class EstadoRed {

	public static boolean conectado = false;
    public static boolean empieza = false;
    public static boolean servidorLleno = false;

    public static boolean estadoInicialRecibido = false;

    public static int numeroJugador = 0;

    public static float posicionX1 = 200;
    public static float posicionY1 = 100;

    public static float posicionX2 = 260;
    public static float posicionY2 = 100;

    public static void reiniciar() {

        conectado = false;
        empieza = false;
        servidorLleno = false;

        estadoInicialRecibido = false;

        numeroJugador = 0;

        posicionX1 = 200;
        posicionY1 = 100;

        posicionX2 = 260;
        posicionY2 = 100;
    }
	
}
