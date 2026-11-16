package com.manbotsurvivor.servidor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

public class PartidaServidor {

    private JugadorServidor[] jugadores;
    private ArrayList<EnemigoServidor> enemigos;
    private ArrayList<ProyectilServidor> proyectiles;
    private ArrayList<ChipServidor> chips;

    private int vidaEquipo;
    private int experiencia;
    private int nivel;
    private int experienciaNecesaria;
    private int nivelesPendientes;
    private int oleada;
    private int siguienteIdEnemigo;
    private int siguienteIdProyectil;
    private int siguienteIdChip;

    private float tiempoEnJuego;
    private float tiempoEntreOleadas;
    private boolean esperandoOleada;
    private boolean jefeAparecio;
    private boolean esperandoMejoras;
    private String estado;

    private boolean[] votosReinicio;
    private Random azar;

    private static float[][] PAREDES = {
        {2208,1632,64,64}, {2624,1920,32,32}, {2752,2176,64,64},
        {1952,2016,32,32}, {1984,2304,64,64}, {2432,2432,32,32},
        {2304,2752,64,64}, {2752,2912,32,32}, {1760,2816,64,64},
        {1536,2464,32,32}, {1536,1792,64,64}, {2784,1376,64,64},
        {2208,1248,32,32}, {1696,1280,64,64}, {1472,1056,32,32},
        {1856,800,64,64}, {2560,864,64,64}, {2208,544,32,32},
        {2688,320,64,64}, {1632,352,32,32}, {1440,352,64,64},
        {1792,160,64,64}, {1280,2880,32,32}, {672,2944,64,64},
        {448,2880,32,32}, {768,2656,32,32}, {1120,2432,64,64},
        {1280,2112,32,32}, {864,1824,64,64}, {672,2272,32,32},
        {288,2464,64,64}, {352,1952,32,32}, {288,1504,64,64},
        {608,1440,32,32}, {1088,1344,64,64}, {1216,1728,32,32},
        {992,864,64,64}, {1088,608,32,32}, {768,224,32,32},
        {704,448,64,64}, {384,992,32,32}, {160,288,64,64}
    };

    public PartidaServidor() {

        azar = new Random();

        jugadores = new JugadorServidor[] {
            new JugadorServidor(1, 200, 100),
            new JugadorServidor(2, 260, 100)
        };

        enemigos = new ArrayList<>();
        proyectiles = new ArrayList<>();
        chips = new ArrayList<>();

        votosReinicio = new boolean[2];

        siguienteIdEnemigo = 1;
        siguienteIdProyectil = 1;
        siguienteIdChip = 1;

        reiniciarDatos();

        estado = "espera";
    }

    public void conectarJugador(int numero) {

        jugadores[numero - 1].conectado = true;

        if (esperandoMejoras) {
            jugadores[numero - 1].eligiMejora = false;
        }

        if (estado.equals("espera")
                && jugadores[0].conectado
                && jugadores[1].conectado) {

            iniciarPartida();
        }
    }

    public void desconectarJugador(int numero) {

        JugadorServidor jugador = jugadores[numero - 1];

        jugador.conectado = false;
        jugador.direccionX = 0;
        jugador.direccionY = 0;

        if (esperandoMejoras) {
            jugador.eligiMejora = true;
            verificarMejorasElegidas();
        }
    }

    public void procesarMensaje(int numeroJugador, String mensaje) {

        if (numeroJugador < 1 || numeroJugador > 2 || mensaje == null) {
            return;
        }

        JugadorServidor jugador = jugadores[numeroJugador - 1];

        String[] partes = mensaje.split("-");

        if (partes[0].equals("mover") && partes.length == 3) {

            try {

                jugador.direccionX = Math.max(
                        -1,
                        Math.min(1, Integer.parseInt(partes[1]) - 1)
                );

                jugador.direccionY = Math.max(
                        -1,
                        Math.min(1, Integer.parseInt(partes[2]) - 1)
                );

            } catch (NumberFormatException e) {

                jugador.direccionX = 0;
                jugador.direccionY = 0;
            }

        } else if (partes[0].equals("mejora") && partes.length == 2) {

            try {
                elegirMejora(numeroJugador, Integer.parseInt(partes[1]));
            } catch (NumberFormatException e) {
                // Se ignora una seleccion que no sea numerica.
            }

        } else if (mensaje.equals("reiniciar")) {

            solicitarReinicio(numeroJugador);
        }
    }

    private void iniciarPartida() {

        estado = "curso";

        crearOleada(1);
    }

    public void actualizar(float delta) {

        if (!estado.equals("curso") || esperandoMejoras) {
            return;
        }

        if (delta < 0) {
            return;
        }

        if (delta > 0.1f) {
            delta = 0.1f;
        }

        tiempoEnJuego += delta;

        actualizarJugadores(delta);

        if (!jefeAparecio && tiempoEnJuego >= 900f) {
            aparecerJefe();
        }

        actualizarEnemigos(delta);
        actualizarArmas(delta);
        actualizarProyectiles(delta);
        eliminarEnemigosMuertos();
        recogerChips();

        if (vidaEquipo <= 0) {
            vidaEquipo = 0;
            estado = "derrota";
        }

        if (jefeAparecio && enemigos.isEmpty() && estado.equals("curso")) {
            estado = "victoria";
        }

        if (!jefeAparecio) {
            actualizarOleadas(delta);
        }
    }

    private void actualizarJugadores(float delta) {

        for (JugadorServidor jugador : jugadores) {

            if (!jugador.conectado || vidaEquipo <= 0) {
                continue;
            }

            float dx = jugador.direccionX;
            float dy = jugador.direccionY;

            float longitud = (float) Math.sqrt(dx * dx + dy * dy);

            if (longitud > 0) {
                dx = dx / longitud * jugador.velocidad * delta;
                dy = dy / longitud * jugador.velocidad * delta;
            }

            moverJugador(jugador, dx, dy);
        }
    }

    private void moverJugador(JugadorServidor jugador, float dx, float dy) {

        float nuevaX = limitar(jugador.x + dx, 0, 3168);

        if (!hayColision(nuevaX, jugador.y, 32, 32)) {
            jugador.x = nuevaX;
        }

        float nuevaY = limitar(jugador.y + dy, 0, 3168);

        if (!hayColision(jugador.x, nuevaY, 32, 32)) {
            jugador.y = nuevaY;
        }
    }

    private void actualizarOleadas(float delta) {

        if (!enemigos.isEmpty()) {
            esperandoOleada = false;
            tiempoEntreOleadas = 0;
            return;
        }

        if (!esperandoOleada) {
            esperandoOleada = true;
            tiempoEntreOleadas = 0;
            return;
        }

        tiempoEntreOleadas += delta;

        if (tiempoEntreOleadas >= 2f) {
            crearOleada(oleada + 1);
        }
    }

    private void crearOleada(int numero) {

        oleada = numero;
        esperandoOleada = false;
        tiempoEntreOleadas = 0;

        int nivelDificultad = (int) (tiempoEnJuego / 120f) + 1;
        int cantidad = 5 + nivelDificultad * 2;
        float dificultad = 1f + (nivelDificultad - 1) * 0.1f;

        for (int i = 0; i < cantidad; i++) {

            float[] posicion = buscarPosicionEnemigo();

            EnemigoServidor enemigo;

            if (i % 7 == 6) {

                enemigo = new EnemigoServidor(
                        siguienteIdEnemigo++,
                        "tanque",
                        posicion[0],
                        posicion[1],
                        20,
                        10,
                        25,
                        64,
                        64
                );

            } else if (i % 2 == 0) {

                enemigo = new EnemigoServidor(
                        siguienteIdEnemigo++,
                        "nano",
                        posicion[0],
                        posicion[1],
                        85,
                        1,
                        5,
                        32,
                        32
                );

            } else {

                enemigo = new EnemigoServidor(
                        siguienteIdEnemigo++,
                        "drone",
                        posicion[0],
                        posicion[1],
                        50,
                        3,
                        10,
                        32,
                        32
                );
            }

            enemigo.velocidad *= dificultad;
            enemigo.vida = Math.max(1, Math.round(enemigo.vida * dificultad));

            enemigos.add(enemigo);
        }
    }

    private void aparecerJefe() {

        jefeAparecio = true;

        float[] posicion = buscarPosicionEnemigo();

        enemigos.add(new EnemigoServidor(
                siguienteIdEnemigo++,
                "jefe",
                posicion[0],
                posicion[1],
                45,
                180,
                25,
                112,
                112
        ));
    }

    private float[] buscarPosicionEnemigo() {

        float x = 100;
        float y = 100;

        for (int intento = 0; intento < 100; intento++) {

            x = azar.nextFloat() * 3152;
            y = azar.nextFloat() * 3152;

            if (hayColision(x, y, 48, 48)) {
                continue;
            }

            boolean lejos = true;

            for (JugadorServidor jugador : jugadores) {

                if (!jugador.conectado) {
                    continue;
                }

                float dx = x - jugador.x;
                float dy = y - jugador.y;

                if (dx * dx + dy * dy < 150 * 150) {
                    lejos = false;
                    break;
                }
            }

            if (lejos) {
                return new float[] {x, y};
            }
        }

        return new float[] {x, y};
    }

    private void actualizarEnemigos(float delta) {

        for (EnemigoServidor enemigo : enemigos) {

            JugadorServidor objetivo = obtenerJugadorCercano(enemigo.x, enemigo.y);

            if (objetivo == null) {
                continue;
            }

            float diferenciaX = objetivo.x - enemigo.x;
            float diferenciaY = objetivo.y - enemigo.y;

            float movimientoX = 0;
            float movimientoY = 0;

            if (diferenciaX > 0) movimientoX = 1;
            if (diferenciaX < 0) movimientoX = -1;
            if (diferenciaY > 0) movimientoY = 1;
            if (diferenciaY < 0) movimientoY = -1;

            float longitud = (float) Math.sqrt(
                    movimientoX * movimientoX + movimientoY * movimientoY
            );

            if (longitud > 0) {

                float pasoX = movimientoX / longitud * enemigo.velocidad * delta;
                float pasoY = movimientoY / longitud * enemigo.velocidad * delta;

                float nuevaX = enemigo.x + pasoX;

                if (!hayColision(nuevaX, enemigo.y, enemigo.ancho, enemigo.alto)
                        && !hayColisionConOtroEnemigo(enemigo, nuevaX, enemigo.y)) {

                    enemigo.x = nuevaX;
                }

                float nuevaY = enemigo.y + pasoY;

                if (!hayColision(enemigo.x, nuevaY, enemigo.ancho, enemigo.alto)
                        && !hayColisionConOtroEnemigo(enemigo, enemigo.x, nuevaY)) {

                    enemigo.y = nuevaY;
                }
            }

            enemigo.tiempoDesdeDaño += delta;

            if (enemigo.tiempoDesdeDaño >= 1f) {

                for (JugadorServidor jugador : jugadores) {

                    if (jugador.conectado
                            && hayColisionEnemigoJugador(enemigo, jugador)) {

                        vidaEquipo -= enemigo.daño;
                        enemigo.tiempoDesdeDaño = 0;
                        break;
                    }
                }
            }
        }
    }

    private boolean hayColisionConOtroEnemigo(
            EnemigoServidor actual,
            float x,
            float y
    ) {

        for (EnemigoServidor otro : enemigos) {

            if (otro == actual) {
                continue;
            }

            if (x < otro.x + otro.ancho
                    && x + actual.ancho > otro.x
                    && y < otro.y + otro.alto
                    && y + actual.alto > otro.y) {

                return true;
            }
        }

        return false;
    }

    private JugadorServidor obtenerJugadorCercano(float x, float y) {

        JugadorServidor objetivo = null;
        float distanciaMinima = Float.MAX_VALUE;

        for (JugadorServidor jugador : jugadores) {

            if (!jugador.conectado || vidaEquipo <= 0) {
                continue;
            }

            float dx = jugador.x - x;
            float dy = jugador.y - y;

            float distancia = dx * dx + dy * dy;

            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                objetivo = jugador;
            }
        }

        return objetivo;
    }

    private boolean hayColisionEnemigoJugador(
            EnemigoServidor enemigo,
            JugadorServidor jugador
    ) {

        return enemigo.x < jugador.x + 32
                && enemigo.x + enemigo.ancho > jugador.x
                && enemigo.y < jugador.y + 32
                && enemigo.y + enemigo.alto > jugador.y;
    }

    private void actualizarArmas(float delta) {

        for (JugadorServidor jugador : jugadores) {

            if (!jugador.conectado || vidaEquipo <= 0) {
                continue;
            }

            jugador.tiempoLaser += delta;
            jugador.tiempoGranada += delta;

            int dañoLaser = jugador.nivelLaser == 1 ? 1
                    : jugador.nivelLaser == 2 ? 2 : 3;

            float rangoLaser = jugador.nivelLaser == 1 ? 220
                    : jugador.nivelLaser == 2 ? 250 : 280;

            float intervaloLaser = jugador.nivelLaser == 3 ? 1.0f : 1.5f;

            if (jugador.tiempoLaser >= intervaloLaser) {

                EnemigoServidor objetivo = obtenerEnemigoCercano(
                        jugador,
                        rangoLaser
                );

                if (objetivo != null) {

                    crearProyectil(
                            jugador,
                            objetivo,
                            false,
                            dañoLaser,
                            240,
                            rangoLaser * 1.5f,
                            0
                    );

                    jugador.tiempoLaser = 0;
                }
            }

            int dañoGranada = jugador.nivelGranada == 1 ? 2
                    : jugador.nivelGranada == 2 ? 3 : 5;

            float rangoGranada = jugador.nivelGranada == 1 ? 180
                    : jugador.nivelGranada == 2 ? 200 : 220;

            float intervaloGranada = jugador.nivelGranada == 1 ? 4
                    : jugador.nivelGranada == 2 ? 3.5f : 3;

            float radioGranada = jugador.nivelGranada == 1 ? 60
                    : jugador.nivelGranada == 2 ? 75 : 90;

            if (jugador.tiempoGranada >= intervaloGranada) {

                EnemigoServidor objetivo = obtenerEnemigoCercano(
                        jugador,
                        rangoGranada
                );

                if (objetivo != null) {

                    crearProyectil(
                            jugador,
                            objetivo,
                            true,
                            dañoGranada,
                            145,
                            rangoGranada * 1.5f,
                            radioGranada
                    );

                    jugador.tiempoGranada = 0;
                }
            }
        }
    }

    private EnemigoServidor obtenerEnemigoCercano(
            JugadorServidor jugador,
            float rango
    ) {

        EnemigoServidor cercano = null;
        float distanciaMinima = rango * rango;

        for (EnemigoServidor enemigo : enemigos) {

            if (enemigo.vida <= 0) {
                continue;
            }

            float dx = enemigo.x + enemigo.ancho / 2f - (jugador.x + 16);
            float dy = enemigo.y + enemigo.alto / 2f - (jugador.y + 16);

            float distancia = dx * dx + dy * dy;

            if (distancia <= distanciaMinima) {
                distanciaMinima = distancia;
                cercano = enemigo;
            }
        }

        return cercano;
    }

    private void crearProyectil(
            JugadorServidor jugador,
            EnemigoServidor objetivo,
            boolean granada,
            int daño,
            float velocidad,
            float distanciaMaxima,
            float radio
    ) {

        float x = jugador.x + 16;
        float y = jugador.y + 16;

        float dx = objetivo.x + objetivo.ancho / 2f - x;
        float dy = objetivo.y + objetivo.alto / 2f - y;

        float distancia = (float) Math.sqrt(dx * dx + dy * dy);

        if (distancia <= 0) {
            return;
        }

        proyectiles.add(new ProyectilServidor(
                siguienteIdProyectil++,
                jugador.numero,
                objetivo.id,
                granada ? "granada" : "laser",
                x,
                y,
                dx / distancia,
                dy / distancia,
                velocidad,
                daño,
                distanciaMaxima,
                radio
        ));
    }

    private void actualizarProyectiles(float delta) {

        Iterator<ProyectilServidor> iterador = proyectiles.iterator();

        while (iterador.hasNext()) {

            ProyectilServidor proyectil = iterador.next();

            if (proyectil.explotado) {

                proyectil.tiempoExplosion += delta;

                if (proyectil.tiempoExplosion >= 0.28f) {
                    iterador.remove();
                }

                continue;
            }

            EnemigoServidor objetivo = obtenerEnemigoPorId(
                    proyectil.idObjetivo
            );

            if (objetivo == null || objetivo.vida <= 0) {

                if (proyectil.tipo.equals("granada")) {
                    explotar(proyectil);
                } else {
                    iterador.remove();
                }

                continue;
            }

            float pasoX = proyectil.dx * proyectil.velocidad * delta;
            float pasoY = proyectil.dy * proyectil.velocidad * delta;

            proyectil.x += pasoX;
            proyectil.y += pasoY;

            proyectil.distanciaRecorrida += (float) Math.sqrt(
                    pasoX * pasoX + pasoY * pasoY
            );

            if (proyectil.distanciaRecorrida >= proyectil.distanciaMaxima) {

                if (proyectil.tipo.equals("granada")) {
                    explotar(proyectil);
                } else {
                    iterador.remove();
                }

                continue;
            }

            if (proyectil.x >= objetivo.x
                    && proyectil.x <= objetivo.x + objetivo.ancho
                    && proyectil.y >= objetivo.y
                    && proyectil.y <= objetivo.y + objetivo.alto) {

                if (proyectil.tipo.equals("granada")) {

                    explotar(proyectil);

                } else {

                    objetivo.vida -= proyectil.daño;
                    objetivo.ultimoAtacante = proyectil.numeroJugador;

                    iterador.remove();
                }
            }
        }
    }

    private void explotar(ProyectilServidor proyectil) {

        if (proyectil.explotado) {
            return;
        }

        proyectil.explotado = true;
        proyectil.tiempoExplosion = 0;

        for (EnemigoServidor enemigo : enemigos) {

            if (enemigo.vida <= 0) {
                continue;
            }

            float dx = enemigo.x + enemigo.ancho / 2f - proyectil.x;
            float dy = enemigo.y + enemigo.alto / 2f - proyectil.y;

            if (dx * dx + dy * dy <= proyectil.radio * proyectil.radio) {

                enemigo.vida -= proyectil.daño;
                enemigo.ultimoAtacante = proyectil.numeroJugador;
            }
        }
    }

    private EnemigoServidor obtenerEnemigoPorId(int id) {

        for (EnemigoServidor enemigo : enemigos) {

            if (enemigo.id == id) {
                return enemigo;
            }
        }

        return null;
    }

    private void eliminarEnemigosMuertos() {

        Iterator<EnemigoServidor> iterador = enemigos.iterator();

        while (iterador.hasNext()) {

            EnemigoServidor enemigo = iterador.next();

            if (enemigo.vida <= 0) {

                if (enemigo.tipo.equals("jefe") && estado.equals("curso")) {
                    estado = "victoria";
                }

                chips.add(new ChipServidor(
                        siguienteIdChip++,
                        enemigo.x,
                        enemigo.y
                ));

                int atacante = enemigo.ultimoAtacante;

                if (atacante < 1 || atacante > 2) {
                    atacante = 1;
                }

                jugadores[atacante - 1].enemigosEliminados++;

                iterador.remove();
            }
        }
    }

    private void recogerChips() {

        Iterator<ChipServidor> iterador = chips.iterator();

        while (iterador.hasNext()) {

            ChipServidor chip = iterador.next();
            boolean recogido = false;

            for (JugadorServidor jugador : jugadores) {

                if (!jugador.conectado) {
                    continue;
                }

                if (chip.x < jugador.x + 32
                        && chip.x + 16 > jugador.x
                        && chip.y < jugador.y + 32
                        && chip.y + 16 > jugador.y) {

                    experiencia += 10;
                    recogido = true;
                    break;
                }
            }

            if (recogido) {
                iterador.remove();
                comprobarSubidaNivel();
            }
        }
    }

    private void comprobarSubidaNivel() {

        while (experiencia >= experienciaNecesaria) {
            experiencia -= experienciaNecesaria;
            nivel++;
            nivelesPendientes++;
        }

        if (nivelesPendientes > 0 && !esperandoMejoras) {
            generarOpcionesMejora();
        }
    }

    private void generarOpcionesMejora() {

        esperandoMejoras = true;
        estado = "pausa";

        for (JugadorServidor jugador : jugadores) {

            jugador.eligiMejora = !jugador.conectado;

            ArrayList<String> disponibles = new ArrayList<>();

            if (jugador.nivelLaser < 3) {
                disponibles.add("LASER");
            }

            if (jugador.nivelGranada < 3) {
                disponibles.add("GRANADA");
            }

            disponibles.add("VIDA");
            disponibles.add("VELOCIDAD");

            while (disponibles.size() < 3) {
                disponibles.add(
                        disponibles.get(azar.nextInt(disponibles.size()))
                );
            }

            Collections.shuffle(disponibles);

            for (int i = 0; i < 3; i++) {
                jugador.opciones[i] = disponibles.get(i);
            }
        }

        verificarMejorasElegidas();
    }

    private void elegirMejora(int numeroJugador, int numeroOpcion) {

        JugadorServidor jugador = jugadores[numeroJugador - 1];

        if (!esperandoMejoras
                || jugador.eligiMejora
                || numeroOpcion < 1
                || numeroOpcion > 3) {

            return;
        }

        String mejora = jugador.opciones[numeroOpcion - 1];

        if (mejora.equals("LASER")) {

            jugador.nivelLaser = Math.min(3, jugador.nivelLaser + 1);

        } else if (mejora.equals("GRANADA")) {

            jugador.nivelGranada = Math.min(3, jugador.nivelGranada + 1);

        } else if (mejora.equals("VIDA")) {

            vidaEquipo = Math.min(100, vidaEquipo + 30);

        } else if (mejora.equals("VELOCIDAD")) {

            jugador.velocidad += 10;
        }

        jugador.eligiMejora = true;

        verificarMejorasElegidas();
    }

    private void verificarMejorasElegidas() {

        if (jugadores[0].eligiMejora && jugadores[1].eligiMejora) {

            nivelesPendientes--;

            if (nivelesPendientes > 0) {

                generarOpcionesMejora();

            } else {

                esperandoMejoras = false;
                estado = "curso";
            }
        }
    }

    private void solicitarReinicio(int numeroJugador) {

        if (!estado.equals("victoria") && !estado.equals("derrota")) {
            return;
        }

        votosReinicio[numeroJugador - 1] = true;

        boolean hayConectados = false;
        boolean todosVotaron = true;

        for (int i = 0; i < 2; i++) {

            if (jugadores[i].conectado) {

                hayConectados = true;

                if (!votosReinicio[i]) {
                    todosVotaron = false;
                }
            }
        }

        if (hayConectados && todosVotaron) {
            reiniciarPartida();
        }
    }

    private void reiniciarPartida() {

        boolean conectado1 = jugadores[0].conectado;
        boolean conectado2 = jugadores[1].conectado;

        jugadores = new JugadorServidor[] {
            new JugadorServidor(1, 200, 100),
            new JugadorServidor(2, 260, 100)
        };

        jugadores[0].conectado = conectado1;
        jugadores[1].conectado = conectado2;

        reiniciarDatos();

        estado = "curso";

        crearOleada(1);
    }

    private void reiniciarDatos() {

        vidaEquipo = 100;
        experiencia = 0;
        nivel = 1;
        experienciaNecesaria = 100;
        nivelesPendientes = 0;
        oleada = 0;

        tiempoEnJuego = 0;
        tiempoEntreOleadas = 0;

        esperandoOleada = false;
        jefeAparecio = false;
        esperandoMejoras = false;

        votosReinicio[0] = false;
        votosReinicio[1] = false;

        enemigos.clear();
        proyectiles.clear();
        chips.clear();
    }

    private boolean hayColision(
            float x,
            float y,
            float ancho,
            float alto
    ) {

        for (float[] pared : PAREDES) {

            if (x < pared[0] + pared[2]
                    && x + ancho > pared[0]
                    && y < pared[1] + pared[3]
                    && y + alto > pared[1]) {

                return true;
            }
        }

        return false;
    }

    private float limitar(float valor, float minimo, float maximo) {

        return Math.max(minimo, Math.min(maximo, valor));
    }

    public String obtenerMensajePartida() {

        return "partida-" + estado
                + "-" + Math.round(tiempoEnJuego)
                + "-" + vidaEquipo
                + "-" + experiencia
                + "-" + nivel
                + "-" + experienciaNecesaria
                + "-" + oleada
                + "-" + jugadores[0].enemigosEliminados
                + "-" + jugadores[1].enemigosEliminados
                + "-" + esperandoMejoras
                + "-" + jefeAparecio;
    }

    public String obtenerMensajeJugadores() {

        return "jugadores-"
                + Math.round(jugadores[0].x)
                + "-" + Math.round(jugadores[0].y)
                + "-" + Math.round(jugadores[1].x)
                + "-" + Math.round(jugadores[1].y)
                + "-" + jugadores[0].conectado
                + "-" + jugadores[1].conectado;
    }

    public String obtenerMensajeEnemigos() {

        StringBuilder mensaje = new StringBuilder("enemigos-");

        mensaje.append(enemigos.size());

        for (EnemigoServidor enemigo : enemigos) {

            mensaje.append("-").append(enemigo.id)
                    .append("-").append(enemigo.tipo)
                    .append("-").append(Math.round(enemigo.x))
                    .append("-").append(Math.round(enemigo.y))
                    .append("-").append(enemigo.vida);
        }

        return mensaje.toString();
    }

    public String obtenerMensajeProyectiles() {

        StringBuilder mensaje = new StringBuilder("proyectiles-");

        mensaje.append(proyectiles.size());

        for (ProyectilServidor proyectil : proyectiles) {

            mensaje.append("-").append(proyectil.id)
                    .append("-").append(proyectil.tipo)
                    .append("-").append(Math.round(proyectil.x))
                    .append("-").append(Math.round(proyectil.y))
                    .append("-").append(proyectil.explotado)
                    .append("-").append(Math.round(proyectil.radio));
        }

        return mensaje.toString();
    }

    public String obtenerMensajeChips() {

        StringBuilder mensaje = new StringBuilder("chips-");

        mensaje.append(chips.size());

        for (ChipServidor chip : chips) {

            mensaje.append("-").append(chip.id)
                    .append("-").append(Math.round(chip.x))
                    .append("-").append(Math.round(chip.y));
        }

        return mensaje.toString();
    }

    public String obtenerMensajeMejoras(int numeroJugador) {

        JugadorServidor jugador = jugadores[numeroJugador - 1];

        return "mejoras-" + numeroJugador
                + "-" + esperandoMejoras
                + "-" + jugador.opciones[0]
                + "-" + jugador.opciones[1]
                + "-" + jugador.opciones[2];
    }

    private static class JugadorServidor {

        int numero;
        float x;
        float y;
        float velocidad;

        int direccionX;
        int direccionY;

        int nivelLaser;
        int nivelGranada;

        float tiempoLaser;
        float tiempoGranada;

        int enemigosEliminados;

        boolean conectado;
        boolean eligiMejora;

        String[] opciones;

        JugadorServidor(int numero, float x, float y) {

            this.numero = numero;
            this.x = x;
            this.y = y;

            velocidad = 100;

            nivelLaser = 1;
            nivelGranada = 1;

            opciones = new String[3];
        }
    }

    private static class EnemigoServidor {

        int id;
        String tipo;

        float x;
        float y;

        float velocidad;

        int vida;
        int daño;

        float ancho;
        float alto;

        float tiempoDesdeDaño;

        int ultimoAtacante;

        EnemigoServidor(
                int id,
                String tipo,
                float x,
                float y,
                float velocidad,
                int vida,
                int daño,
                float ancho,
                float alto
        ) {

            this.id = id;
            this.tipo = tipo;

            this.x = x;
            this.y = y;

            this.velocidad = velocidad;

            this.vida = vida;
            this.daño = daño;

            this.ancho = ancho;
            this.alto = alto;

            tiempoDesdeDaño = 1;
        }
    }

    private static class ProyectilServidor {

        int id;
        int numeroJugador;
        int idObjetivo;

        String tipo;

        float x;
        float y;

        float dx;
        float dy;

        float velocidad;

        int daño;

        float distanciaMaxima;
        float distanciaRecorrida;

        float radio;

        float tiempoExplosion;

        boolean explotado;

        ProyectilServidor(
                int id,
                int numeroJugador,
                int idObjetivo,
                String tipo,
                float x,
                float y,
                float dx,
                float dy,
                float velocidad,
                int daño,
                float distanciaMaxima,
                float radio
        ) {

            this.id = id;
            this.numeroJugador = numeroJugador;
            this.idObjetivo = idObjetivo;

            this.tipo = tipo;

            this.x = x;
            this.y = y;

            this.dx = dx;
            this.dy = dy;

            this.velocidad = velocidad;

            this.daño = daño;

            this.distanciaMaxima = distanciaMaxima;

            this.radio = radio;
        }
    }

    private static class ChipServidor {

        int id;

        float x;
        float y;

        ChipServidor(int id, float x, float y) {

            this.id = id;
            this.x = x;
            this.y = y;
        }
    }
}