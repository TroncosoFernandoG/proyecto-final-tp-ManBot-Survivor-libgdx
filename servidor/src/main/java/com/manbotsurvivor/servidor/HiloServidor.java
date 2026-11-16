package com.manbotsurvivor.servidor;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HiloServidor extends Thread {

    private DatagramSocket socket;
    private ArrayList<DireccionRed> clientes;
    private PartidaServidor partida;

    private int puertoServidor;
    private boolean servidorActivo;

    private long ultimoEnvioEstado;
    private long ultimoActualizar;

    public HiloServidor() {

        clientes = new ArrayList<>();
        partida = new PartidaServidor();

        puertoServidor = 50000;
        servidorActivo = true;

        ultimoEnvioEstado = 0;
        ultimoActualizar = System.currentTimeMillis();
    }

    @Override
    public void run() {

        try {

            socket = new DatagramSocket(puertoServidor);
            socket.setSoTimeout(25);

            System.out.println(
                    "Servidor de ManBot Survivor iniciado en el puerto "
                    + puertoServidor
            );

            while (servidorActivo) {

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

                    procesarMensaje(mensaje, paquete);

                } catch (SocketTimeoutException e) {

                    // Se aprovecha el tiempo sin paquetes para actualizar la partida.
                }

                actualizarPartida();

                enviarEstadoPeriodicamente();
            }

        } catch (BindException e) {

            System.out.println(
                    "No se pudo iniciar el servidor: el puerto "
                    + puertoServidor + " ya esta ocupado."
            );

        } catch (SocketException e) {

            if (servidorActivo) {
                System.out.println("Se cerro el socket del servidor.");
            }

        } catch (IOException e) {

            System.out.println(
                    "Error al recibir un paquete del cliente."
            );

        } finally {

            servidorActivo = false;

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            System.out.println("Servidor detenido.");
        }
    }

    private void procesarMensaje(
            String mensaje,
            DatagramPacket paquete
    ) {

        if (mensaje.equals("conexion")) {
            registrarCliente(paquete);
            return;
        }

        DireccionRed cliente = buscarCliente(
                paquete.getAddress(),
                paquete.getPort()
        );

        if (cliente == null || !cliente.conectado) {
            return;
        }

        cliente.ultimoMensaje = System.currentTimeMillis();

        if (mensaje.equals("ping")) {
            enviarMensaje("pong", cliente.ip, cliente.puerto);
            return;
        }

        if (mensaje.equals("desconexion")) {
            desconectarCliente(cliente);
            return;
        }

        partida.procesarMensaje(
                cliente.numeroJugador,
                mensaje
        );
    }

    private void registrarCliente(DatagramPacket paquete) {

        DireccionRed cliente = buscarCliente(
                paquete.getAddress(),
                paquete.getPort()
        );

        if (cliente != null) {

            cliente.ultimoMensaje = System.currentTimeMillis();

            enviarRespuestaConexion(cliente);

            return;
        }

        cliente = buscarLugarDisponible();

        if (cliente == null) {

            enviarMensaje(
                    "lleno",
                    paquete.getAddress(),
                    paquete.getPort()
            );

            return;
        }

        cliente.ip = paquete.getAddress();
        cliente.puerto = paquete.getPort();

        cliente.ultimoMensaje = System.currentTimeMillis();
        cliente.conectado = true;

        partida.conectarJugador(cliente.numeroJugador);

        System.out.println(
                "Se conecto el jugador " + cliente.numeroJugador
                + " desde " + cliente.ip.getHostAddress()
        );

        enviarRespuestaConexion(cliente);

        if (cantidadClientes() == 2) {

            enviarATodos("empieza");

            System.out.println(
                    "Los dos jugadores estan conectados. Comienza la partida."
            );
        }
    }

    private DireccionRed buscarLugarDisponible() {

        for (DireccionRed cliente : clientes) {

            if (!cliente.conectado) {
                return cliente;
            }
        }

        if (clientes.size() < 2) {

            DireccionRed nuevo = new DireccionRed(
                    null,
                    0,
                    clientes.size() + 1
            );

            nuevo.conectado = false;

            clientes.add(nuevo);

            return nuevo;
        }

        return null;
    }

    private DireccionRed buscarCliente(InetAddress ip, int puerto) {

        for (DireccionRed cliente : clientes) {

            if (cliente.conectado && cliente.coincide(ip, puerto)) {
                return cliente;
            }
        }

        return null;
    }

    private void desconectarCliente(DireccionRed cliente) {

        if (!cliente.conectado) {
            return;
        }

        cliente.conectado = false;

        partida.desconectarJugador(cliente.numeroJugador);

        System.out.println(
                "Se desconecto el jugador " + cliente.numeroJugador + "."
        );
    }

    private void enviarRespuestaConexion(DireccionRed cliente) {

        enviarMensaje(
                "ok-" + cliente.numeroJugador,
                cliente.ip,
                cliente.puerto
        );

        if (cantidadClientes() == 2) {

            enviarMensaje(
                    "empieza",
                    cliente.ip,
                    cliente.puerto
            );
        }

        enviarEstadoACliente(cliente);
    }

    private void actualizarPartida() {

        long ahora = System.currentTimeMillis();

        float delta = (ahora - ultimoActualizar) / 1000f;

        ultimoActualizar = ahora;

        if (cantidadClientes() > 0) {
            partida.actualizar(delta);
        }
    }

    private void enviarEstadoPeriodicamente() {

        long ahora = System.currentTimeMillis();

        if (ahora - ultimoEnvioEstado < 50) {
            return;
        }

        ultimoEnvioEstado = ahora;

        enviarATodos(partida.obtenerMensajePartida());
        enviarATodos(partida.obtenerMensajeJugadores());
        enviarATodos(partida.obtenerMensajeEnemigos());
        enviarATodos(partida.obtenerMensajeProyectiles());
        enviarATodos(partida.obtenerMensajeChips());

        for (DireccionRed cliente : clientes) {

            if (cliente.conectado) {

                enviarMensaje(
                        partida.obtenerMensajeMejoras(cliente.numeroJugador),
                        cliente.ip,
                        cliente.puerto
                );
            }
        }
    }

    private void enviarEstadoACliente(DireccionRed cliente) {

        enviarMensaje(
                partida.obtenerMensajePartida(),
                cliente.ip,
                cliente.puerto
        );

        enviarMensaje(
                partida.obtenerMensajeJugadores(),
                cliente.ip,
                cliente.puerto
        );

        enviarMensaje(
                partida.obtenerMensajeEnemigos(),
                cliente.ip,
                cliente.puerto
        );

        enviarMensaje(
                partida.obtenerMensajeProyectiles(),
                cliente.ip,
                cliente.puerto
        );

        enviarMensaje(
                partida.obtenerMensajeChips(),
                cliente.ip,
                cliente.puerto
        );

        enviarMensaje(
                partida.obtenerMensajeMejoras(cliente.numeroJugador),
                cliente.ip,
                cliente.puerto
        );
    }

    private int cantidadClientes() {

        int cantidad = 0;

        for (DireccionRed cliente : clientes) {

            if (cliente.conectado) {
                cantidad++;
            }
        }

        return cantidad;
    }

    public void enviarMensaje(
            String mensaje,
            InetAddress ip,
            int puerto
    ) {

        if (socket == null || socket.isClosed() || ip == null) {
            return;
        }

        try {

            byte[] datos = mensaje.getBytes(StandardCharsets.UTF_8);

            DatagramPacket paquete = new DatagramPacket(
                    datos,
                    datos.length,
                    ip,
                    puerto
            );

            socket.send(paquete);

        } catch (IOException e) {

            System.out.println(
                    "Error al enviar un mensaje al cliente."
            );
        }
    }

    public void enviarATodos(String mensaje) {

        for (DireccionRed cliente : clientes) {

            if (cliente.conectado) {

                enviarMensaje(
                        mensaje,
                        cliente.ip,
                        cliente.puerto
                );
            }
        }
    }

    public void cerrar() {

        servidorActivo = false;

        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public static void main(String[] args) {

        HiloServidor servidor = new HiloServidor();

        servidor.start();
    }
}