package com.manbotsurvivor.game.red;

import java.util.ArrayList;

public class EstadoRed {

    public static boolean conectado = false;
    public static boolean empieza = false;
    public static boolean servidorLleno = false;
    public static boolean perdioConexion = false;
    public static boolean estadoInicialRecibido = false;
    public static boolean esperandoMejora = false;

    public static int numeroJugador = 0;

    public static float posicionX1 = 200;
    public static float posicionY1 = 100;
    public static float posicionX2 = 260;
    public static float posicionY2 = 100;

    public static boolean conectadoJugador1 = false;
    public static boolean conectadoJugador2 = false;

    public static int vida = 100;
    public static int experiencia = 0;
    public static int nivel = 1;
    public static int experienciaNecesaria = 100;
    public static int oleada = 0;

    public static int enemigosEliminados1 = 0;
    public static int enemigosEliminados2 = 0;

    public static float tiempoEnJuego = 0;
    public static String estadoPartida = "espera";
    public static boolean jefeAparecio = false;

    public static String[] opcionesMejora = {
        "LASER",
        "GRANADA",
        "VIDA"
    };

    public static ArrayList<EnemigoRed> enemigos = new ArrayList<>();
    public static ArrayList<ProyectilRed> proyectiles = new ArrayList<>();
    public static ArrayList<ChipRed> chips = new ArrayList<>();

    public static void reiniciar() {

        conectado = false;
        empieza = false;
        servidorLleno = false;
        perdioConexion = false;
        estadoInicialRecibido = false;
        esperandoMejora = false;

        numeroJugador = 0;

        posicionX1 = 200;
        posicionY1 = 100;

        posicionX2 = 260;
        posicionY2 = 100;

        conectadoJugador1 = false;
        conectadoJugador2 = false;

        vida = 100;
        experiencia = 0;
        nivel = 1;
        experienciaNecesaria = 100;
        oleada = 0;

        enemigosEliminados1 = 0;
        enemigosEliminados2 = 0;

        tiempoEnJuego = 0;
        estadoPartida = "espera";
        jefeAparecio = false;

        opcionesMejora = new String[] {
            "LASER",
            "GRANADA",
            "VIDA"
        };

        enemigos.clear();
        proyectiles.clear();
        chips.clear();
    }

    public static class EnemigoRed {

        public int id;
        public String tipo;
        public float x;
        public float y;
        public int vida;

        public EnemigoRed(
                int id,
                String tipo,
                float x,
                float y,
                int vida
        ) {

            this.id = id;
            this.tipo = tipo;
            this.x = x;
            this.y = y;
            this.vida = vida;
        }
    }

    public static class ProyectilRed {

        public int id;
        public String tipo;
        public float x;
        public float y;
        public boolean explotado;
        public float radio;

        public ProyectilRed(
                int id,
                String tipo,
                float x,
                float y,
                boolean explotado,
                float radio
        ) {

            this.id = id;
            this.tipo = tipo;
            this.x = x;
            this.y = y;
            this.explotado = explotado;
            this.radio = radio;
        }
    }

    public static class ChipRed {

        public int id;
        public float x;
        public float y;

        public ChipRed(int id, float x, float y) {

            this.id = id;
            this.x = x;
            this.y = y;
        }
    }
}