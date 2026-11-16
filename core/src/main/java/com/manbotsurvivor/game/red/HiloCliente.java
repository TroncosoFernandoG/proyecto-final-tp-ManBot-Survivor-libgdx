package com.manbotsurvivor.game.red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;

public class HiloCliente extends Thread {

    private DatagramSocket socket;
    private InetAddress ipServidor;

    private int puertoServidor;

    private boolean clienteActivo;
    private boolean servidorEncontrado;

    private long ultimoEnvioConexion;
    private long ultimoEnvioPing;
    private long ultimaRespuesta;

    public HiloCliente() throws SocketException {

        socket = new DatagramSocket();

        socket.setBroadcast(true);
        socket.setSoTimeout(1000);

        puertoServidor = 50000;

        clienteActivo = true;
        servidorEncontrado = false;

        ultimoEnvioConexion = 0;
        ultimoEnvioPing = 0;

        ultimaRespuesta = System.currentTimeMillis();
    }

    @Override
    public void run() {

        while (clienteActivo) {

            long ahora = System.currentTimeMillis();

            if (!servidorEncontrado
                    && ahora - ultimoEnvioConexion >= 2000) {

                enviarConexion();

                ultimoEnvioConexion = ahora;
            }

            if (servidorEncontrado
                    && ahora - ultimoEnvioPing >= 2000) {

                enviarMensaje("ping");

                ultimoEnvioPing = ahora;
            }

            if (servidorEncontrado
                    && ahora - ultimaRespuesta > 8000) {

                servidorEncontrado = false;
                ipServidor = null;

                Gdx.app.postRunnable(new Runnable() {

                    @Override
                    public void run() {

                        EstadoRed.conectado = false;
                        EstadoRed.empieza = false;
                        EstadoRed.perdioConexion = true;
                    }
                });
            }

            try {

                byte[] datos = new byte[1024];

                DatagramPacket paquete = new DatagramPacket(
                        datos,
                        datos.length
                );

                socket.receive(paquete);

                String mensaje = new String(
                        paquete.getData(),
                        0,
                        paquete.getLength(),
                        StandardCharsets.UTF_8
                ).trim();

                ultimaRespuesta = System.currentTimeMillis();

                procesarMensaje(mensaje, paquete);

            } catch (SocketTimeoutException e) {

                // Se mantiene el ciclo para reintentar la conexion.

            } catch (SocketException e) {

                if (clienteActivo) {
                    System.out.println("Se cerro el socket del cliente.");
                }

                clienteActivo = false;

            } catch (IOException e) {

                System.out.println(
                        "Error al recibir datos del servidor."
                );
            }
        }
    }

    private void enviarConexion() {

        try {

            InetAddress direccionBroadcast =
                    InetAddress.getByName("255.255.255.255");

            byte[] datos = "conexion".getBytes(
                    StandardCharsets.UTF_8
            );

            DatagramPacket paquete = new DatagramPacket(
                    datos,
                    datos.length,
                    direccionBroadcast,
                    puertoServidor
            );

            socket.send(paquete);

        } catch (IOException e) {

            System.out.println("No se pudo buscar el servidor.");
        }
    }

    private void procesarMensaje(
            String mensaje,
            DatagramPacket paquete
    ) {

        String[] partes = mensaje.split("-");

        if (partes[0].equals("ok") && partes.length == 2) {

            try {

                int jugadorAsignado = Integer.parseInt(partes[1]);

                ipServidor = paquete.getAddress();

                servidorEncontrado = true;

                ultimaRespuesta = System.currentTimeMillis();

                Gdx.app.postRunnable(new Runnable() {

                    @Override
                    public void run() {

                        EstadoRed.conectado = true;
                        EstadoRed.numeroJugador = jugadorAsignado;

                        EstadoRed.servidorLleno = false;
                        EstadoRed.perdioConexion = false;
                    }
                });

            } catch (NumberFormatException e) {

                System.out.println(
                        "El servidor envio un numero de jugador incorrecto."
                );
            }

            return;
        }

        if (mensaje.equals("empieza")) {

            Gdx.app.postRunnable(new Runnable() {

                @Override
                public void run() {
                    EstadoRed.empieza = true;
                }
            });

            return;
        }

        if (mensaje.equals("lleno")) {

            Gdx.app.postRunnable(new Runnable() {

                @Override
                public void run() {
                    EstadoRed.servidorLleno = true;
                }
            });

            return;
        }

        if (mensaje.equals("pong")) {
            return;
        }

        if (partes[0].equals("partida") && partes.length == 12) {

            try {

                String estado = partes[1];

                float tiempo = Float.parseFloat(partes[2]);

                int vida = Integer.parseInt(partes[3]);
                int experiencia = Integer.parseInt(partes[4]);
                int nivel = Integer.parseInt(partes[5]);

                int experienciaNecesaria = Integer.parseInt(partes[6]);
                int oleada = Integer.parseInt(partes[7]);

                int eliminados1 = Integer.parseInt(partes[8]);
                int eliminados2 = Integer.parseInt(partes[9]);

                boolean esperandoMejora =
                        Boolean.parseBoolean(partes[10]);

                boolean jefeAparecio =
                        Boolean.parseBoolean(partes[11]);

                Gdx.app.postRunnable(new Runnable() {

                    @Override
                    public void run() {

                        EstadoRed.estadoPartida = estado;

                        if (!estado.equals("espera")) {
                            EstadoRed.empieza = true;
                        }

                        EstadoRed.tiempoEnJuego = tiempo;

                        EstadoRed.vida = vida;
                        EstadoRed.experiencia = experiencia;
                        EstadoRed.nivel = nivel;

                        EstadoRed.experienciaNecesaria =
                                experienciaNecesaria;

                        EstadoRed.oleada = oleada;

                        EstadoRed.enemigosEliminados1 = eliminados1;
                        EstadoRed.enemigosEliminados2 = eliminados2;

                        EstadoRed.esperandoMejora = esperandoMejora;
                        EstadoRed.jefeAparecio = jefeAparecio;

                        EstadoRed.estadoInicialRecibido = true;
                    }
                });

            } catch (NumberFormatException e) {

                System.out.println(
                        "No se pudo interpretar el estado de la partida."
                );
            }

            return;
        }

        if (partes[0].equals("jugadores") && partes.length == 7) {

            try {

                float x1 = Float.parseFloat(partes[1]);
                float y1 = Float.parseFloat(partes[2]);

                float x2 = Float.parseFloat(partes[3]);
                float y2 = Float.parseFloat(partes[4]);

                boolean conectado1 =
                        Boolean.parseBoolean(partes[5]);

                boolean conectado2 =
                        Boolean.parseBoolean(partes[6]);

                Gdx.app.postRunnable(new Runnable() {

                    @Override
                    public void run() {

                        EstadoRed.posicionX1 = x1;
                        EstadoRed.posicionY1 = y1;

                        EstadoRed.posicionX2 = x2;
                        EstadoRed.posicionY2 = y2;

                        EstadoRed.conectadoJugador1 = conectado1;
                        EstadoRed.conectadoJugador2 = conectado2;
                    }
                });

            } catch (NumberFormatException e) {

                System.out.println(
                        "No se pudieron interpretar las posiciones de los jugadores."
                );
            }

            return;
        }

        if (partes[0].equals("enemigos") && partes.length >= 2) {

            try {

                int cantidad = Integer.parseInt(partes[1]);

                if (partes.length != 2 + cantidad * 5) {
                    return;
                }

                ArrayList<EstadoRed.EnemigoRed> lista =
                        new ArrayList<>();

                int indice = 2;

                for (int i = 0; i < cantidad; i++) {

                    int id = Integer.parseInt(partes[indice++]);

                    String tipo = partes[indice++];

                    float x = Float.parseFloat(partes[indice++]);
                    float y = Float.parseFloat(partes[indice++]);

                    int vida = Integer.parseInt(partes[indice++]);

                    lista.add(
                            new EstadoRed.EnemigoRed(
                                    id,
                                    tipo,
                                    x,
                                    y,
                                    vida
                            )
                    );
                }

                Gdx.app.postRunnable(new Runnable() {

                    @Override
                    public void run() {
                        EstadoRed.enemigos = lista;
                    }
                });

            } catch (NumberFormatException e) {

                System.out.println(
                        "No se pudieron interpretar los enemigos."
                );
            }

            return;
        }

        if (partes[0].equals("proyectiles") && partes.length >= 2) {

            try {

                int cantidad = Integer.parseInt(partes[1]);

                if (partes.length != 2 + cantidad * 6) {
                    return;
                }

                ArrayList<EstadoRed.ProyectilRed> lista =
                        new ArrayList<>();

                int indice = 2;

                for (int i = 0; i < cantidad; i++) {

                    int id = Integer.parseInt(partes[indice++]);

                    String tipo = partes[indice++];

                    float x = Float.parseFloat(partes[indice++]);
                    float y = Float.parseFloat(partes[indice++]);

                    boolean explotado =
                            Boolean.parseBoolean(partes[indice++]);

                    float radio = Float.parseFloat(partes[indice++]);

                    lista.add(
                            new EstadoRed.ProyectilRed(
                                    id,
                                    tipo,
                                    x,
                                    y,
                                    explotado,
                                    radio
                            )
                    );
                }

                Gdx.app.postRunnable(new Runnable() {

                    @Override
                    public void run() {
                        EstadoRed.proyectiles = lista;
                    }
                });

            } catch (NumberFormatException e) {

                System.out.println(
                        "No se pudieron interpretar los proyectiles."
                );
            }

            return;
        }

        if (partes[0].equals("chips") && partes.length >= 2) {

            try {

                int cantidad = Integer.parseInt(partes[1]);

                if (partes.length != 2 + cantidad * 3) {
                    return;
                }

                ArrayList<EstadoRed.ChipRed> lista =
                        new ArrayList<>();

                int indice = 2;

                for (int i = 0; i < cantidad; i++) {

                    int id = Integer.parseInt(partes[indice++]);

                    float x = Float.parseFloat(partes[indice++]);
                    float y = Float.parseFloat(partes[indice++]);

                    lista.add(
                            new EstadoRed.ChipRed(id, x, y)
                    );
                }

                Gdx.app.postRunnable(new Runnable() {

                    @Override
                    public void run() {
                        EstadoRed.chips = lista;
                    }
                });

            } catch (NumberFormatException e) {

                System.out.println(
                        "No se pudieron interpretar los chips."
                );
            }

            return;
        }

        if (partes[0].equals("mejoras") && partes.length == 6) {

            try {

                int jugador = Integer.parseInt(partes[1]);

                boolean activa =
                        Boolean.parseBoolean(partes[2]);

                String[] opciones = {
                    partes[3],
                    partes[4],
                    partes[5]
                };

                Gdx.app.postRunnable(new Runnable() {

                    @Override
                    public void run() {

                        if (jugador == EstadoRed.numeroJugador) {

                            EstadoRed.esperandoMejora = activa;
                            EstadoRed.opcionesMejora = opciones;
                        }
                    }
                });

            } catch (NumberFormatException e) {

                System.out.println(
                        "No se pudieron interpretar las mejoras."
                );
            }
        }
    }

    public void enviarMensaje(String mensaje) {

        if (!servidorEncontrado
                || ipServidor == null
                || socket == null
                || socket.isClosed()) {

            return;
        }

        try {

            byte[] datos = mensaje.getBytes(StandardCharsets.UTF_8);

            DatagramPacket paquete = new DatagramPacket(
                    datos,
                    datos.length,
                    ipServidor,
                    puertoServidor
            );

            socket.send(paquete);

        } catch (IOException e) {

            System.out.println(
                    "Error al enviar el mensaje al servidor."
            );
        }
    }

    public boolean estaConectado() {
        return servidorEncontrado;
    }

    public void cerrar() {

        if (servidorEncontrado) {
            enviarMensaje("desconexion");
        }

        clienteActivo = false;

        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}