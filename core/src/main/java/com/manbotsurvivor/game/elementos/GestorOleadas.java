package com.manbotsurvivor.game.elementos;

import java.util.List;

import com.badlogic.gdx.math.MathUtils;

public class GestorOleadas {

    private final List<Enemigo> enemigos;
    private final Jugador jugador;
    private final Mapa mapa;
    private final GestorPartida partida;

    private final float anchoMapa;
    private final float altoMapa;

    private int oleadaActual = 0;
    private float tiempoEntreOleadas = 0f;
    private boolean esperandoSiguienteOleada = false;

    private boolean jefeAparecio = false;
    private JefeMecanico jefe;

    public GestorOleadas(
            List<Enemigo> enemigos,
            Jugador jugador,
            Mapa mapa,
            GestorPartida partida,
            float anchoMapa,
            float altoMapa) {

        this.enemigos = enemigos;
        this.jugador = jugador;
        this.mapa = mapa;
        this.partida = partida;
        this.anchoMapa = anchoMapa;
        this.altoMapa = altoMapa;

        iniciarOleada(1);
    }

    public void actualizar(float delta) {
        if (!partida.estaEnCurso()) {
            return;
        }

        if (!jefeAparecio && partida.obtenerTiempoEnJuego() >= 900f) {
            aparecerJefe();
        }

        if (jefeAparecio) {
            if (jefe != null && !jefe.estaVivo()) {
                partida.registrarVictoria();
            }

            return;
        }

        if (!enemigos.isEmpty()) {
            esperandoSiguienteOleada = false;
            tiempoEntreOleadas = 0f;
            return;
        }

        if (!esperandoSiguienteOleada) {
            esperandoSiguienteOleada = true;
            tiempoEntreOleadas = 0f;
            return;
        }

        tiempoEntreOleadas += delta;

        if (tiempoEntreOleadas >= 2f) {
            iniciarOleada(oleadaActual + 1);
        }
    }

    private void iniciarOleada(int numero) {
        oleadaActual = numero;
        esperandoSiguienteOleada = false;
        tiempoEntreOleadas = 0f;

        int cantidad = 5 + oleadaActual * 2;

        float dificultad = 1f + (oleadaActual - 1) * 0.1f;

        for (int i = 0; i < cantidad; i++) {
            float[] posicion = buscarPosicionEnemigo();

            Enemigo enemigo;

            if (i % 7 == 6) {
                enemigo = new RobotTanque(posicion[0], posicion[1], 20, mapa);
                
            } else if (i % 2 == 0) {
                enemigo = new NanoBot(posicion[0], posicion[1], mapa);
                
            } else {
                enemigo = new DroneBasico(posicion[0], posicion[1], 50, mapa);
                
            }

            enemigo.aumentarDificultad(dificultad);
            enemigos.add(enemigo);
        }
    }

    private void aparecerJefe() {
        jefeAparecio = true;

        float[] posicion = buscarPosicionEnemigo();

        jefe = new JefeMecanico(posicion[0], posicion[1], mapa);

        enemigos.add(jefe);
    }

    private float[] buscarPosicionEnemigo() {
        float x = 0f;
        float y = 0f;

        for (int intento = 0; intento < 100; intento++) {
            x = MathUtils.random(0f, anchoMapa - 48f);
            y = MathUtils.random(0f, altoMapa - 48f);

            if (mapa.hayColision(x, y, 32, 32)) {
                continue;
            }

            float dx = x - jugador.obtenerPosicionX();
            float dy = y - jugador.obtenerPosicionY();

            if (dx * dx + dy * dy >= 150f * 150f) {
                return new float[] { x, y };
            }
        }

        return new float[] { x, y };
    }

    public int obtenerOleadaActual() {
        return oleadaActual;
    }

    public boolean jefeAparecio() {
        return jefeAparecio;
    }

    public JefeMecanico obtenerJefe() {
        return jefe;
    }
}