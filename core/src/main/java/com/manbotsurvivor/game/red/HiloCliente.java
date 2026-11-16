package com.manbotsurvivor.game.red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.badlogic.gdx.Gdx;

public class HiloCliente extends Thread {

    private DatagramSocket socket;

    private InetAddress ipServidor;

    private int puertoServidor;

    private boolean clienteActivo;
    private boolean partidaIniciada;
    private boolean servidorLleno;

    private boolean recibioEstadoInicial;
    private boolean recibioInicio;

    public HiloCliente() throws SocketException {

        socket = new DatagramSocket();

        socket.setBroadcast(true);
        socket.setSoTimeout(1000);

        puertoServidor = 50000;

        clienteActivo = true;
        partidaIniciada = false;
        servidorLleno = false;

        recibioEstadoInicial = false;
        recibioInicio = false;
    }

    @Override
    public void run() {

        long ultimoEnvio = 0;

        while (clienteActivo) {

            if (!partidaIniciada
                    && !servidorLleno
                    && System.currentTimeMillis() - ultimoEnvio >= 2000) {

                enviarConexion();

                ultimoEnvio = System.currentTimeMillis();
            }

            try {

                byte[] datos = new byte[1024];

                DatagramPacket paquete = new DatagramPacket(
                        datos,
                        datos.length
                );

                socket.receive(paquete);

                String mensaje = new String(
                        paquete.getData()
                ).trim();

                procesarMensaje(mensaje, paquete);

            } catch (SocketTimeoutException e) {

                // Se vuelve a intentar buscar el servidor.

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

            byte[] datos = "conexion".getBytes();

            DatagramPacket paquete = new DatagramPacket(
                    datos,
                    datos.length,
                    direccionBroadcast,
                    puertoServidor
            );

            socket.send(paquete);

        } catch (IOException e) {

            System.out.println(
                    "Error al buscar el servidor."
            );
        }
    }

    private void procesarMensaje(
            String mensaje,
            DatagramPacket paquete
    ) {

        String[] partes = mensaje.split("-");

        if (partes[0].equals("ok") && partes.length == 2) {

            ipServidor = paquete.getAddress();

            int jugadorAsignado = Integer.parseInt(partes[1]);

            Gdx.app.postRunnable(new Runnable() {

                @Override
                public void run() {

                    EstadoRed.conectado = true;

                    EstadoRed.numeroJugador = jugadorAsignado;
                }
            });

        } else if (partes[0].equals("estado") && partes.length == 5) {

            float x1 = Float.parseFloat(partes[1]);
            float y1 = Float.parseFloat(partes[2]);

            float x2 = Float.parseFloat(partes[3]);
            float y2 = Float.parseFloat(partes[4]);

            recibioEstadoInicial = true;

            Gdx.app.postRunnable(new Runnable() {

                @Override
                public void run() {

                    EstadoRed.posicionX1 = x1;
                    EstadoRed.posicionY1 = y1;

                    EstadoRed.posicionX2 = x2;
                    EstadoRed.posicionY2 = y2;

                    EstadoRed.estadoInicialRecibido = true;
                }
            });

        } else if (mensaje.equals("empieza")) {

            recibioInicio = true;

            Gdx.app.postRunnable(new Runnable() {

                @Override
                public void run() {

                    EstadoRed.empieza = true;
                }
            });

        } else if (mensaje.equals("lleno")) {

            servidorLleno = true;

            Gdx.app.postRunnable(new Runnable() {

                @Override
                public void run() {

                    EstadoRed.servidorLleno = true;
                }
            });
        }

        if (recibioEstadoInicial && recibioInicio) {
            partidaIniciada = true;
        }
    }

    public void enviarMensaje(String mensaje) {

        if (!EstadoRed.conectado
                || ipServidor == null
                || socket.isClosed()) {
            return;
        }

        try {

            byte[] datos = mensaje.getBytes();

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

    public void cerrar() {

        if (EstadoRed.conectado) {
            enviarMensaje("desconexion");
        }

        clienteActivo = false;

        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}