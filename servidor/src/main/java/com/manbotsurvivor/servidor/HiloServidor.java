package com.manbotsurvivor.servidor;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HiloServidor extends Thread{

	private DatagramSocket socket;

    private ArrayList<DireccionRed> clientes;

    private int cantidadClientes;
    private int puertoServidor;

    private boolean servidorActivo;

    public HiloServidor() {

        clientes = new ArrayList<>();

        cantidadClientes = 0;
        puertoServidor = 50000;

        servidorActivo = true;
    }

    @Override
    public void run() {

        try {

            socket = new DatagramSocket(puertoServidor);

            System.out.println("Servidor de ManBot Survivor iniciado.");

            while (servidorActivo) {

                byte[] datos = new byte[1024];

                DatagramPacket paquete = new DatagramPacket(
                        datos,
                        datos.length
                );

                socket.receive(paquete);

                String mensaje = new String(paquete.getData()).trim();

                procesarMensaje(mensaje, paquete);
            }

        } catch (BindException e) {

            System.out.println(
                    "El puerto " + puertoServidor + " ya esta ocupado."
            );

        } catch (SocketException e) {

            if (servidorActivo) {
                System.out.println("Se cerro el socket del servidor.");
            }

        } catch (IOException e) {

            System.out.println("Error al recibir un paquete.");

        } finally {

            servidorActivo = false;

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            System.out.println("Servidor detenido.");
        }
    }

    private void procesarMensaje(String mensaje, DatagramPacket paquete) {

        if (mensaje.equals("conexion")) {

            registrarCliente(paquete);

        } else if (mensaje.equals("desconexion")) {

            eliminarCliente(paquete);
        }
    }

    private void registrarCliente(DatagramPacket paquete) {

        for (int i = 0; i < clientes.size(); i++) {

            DireccionRed cliente = clientes.get(i);

            if (cliente.ip.equals(paquete.getAddress())
                    && cliente.puerto == paquete.getPort()) {

                enviarMensaje(
                        "ok-" + (i + 1),
                        cliente.ip,
                        cliente.puerto
                );

                if (cantidadClientes == 2) {

                    enviarMensaje(
                            "estado-200-100-260-100",
                            cliente.ip,
                            cliente.puerto
                    );

                    enviarMensaje(
                            "empieza",
                            cliente.ip,
                            cliente.puerto
                    );
                }

                return;
            }
        }

        if (cantidadClientes < 2) {

            DireccionRed nuevoCliente = new DireccionRed(paquete.getAddress(), paquete.getPort());

            clientes.add(nuevoCliente);

            cantidadClientes++;

            enviarMensaje(
                    "ok-" + cantidadClientes,
                    nuevoCliente.ip,
                    nuevoCliente.puerto
            );

            System.out.println(
                    "Se conecto el jugador " + cantidadClientes
            );

            if (cantidadClientes == 2) {

                enviarATodos("estado-200-100-260-100");

                enviarATodos("empieza");

                System.out.println(
                        "Los dos jugadores estan conectados."
                );

                System.out.println(
                        "La partida puede comenzar."
                );
            }

        } else {

            enviarMensaje(
                    "lleno",
                    paquete.getAddress(),
                    paquete.getPort()
            );
        }
    }

    private void eliminarCliente(DatagramPacket paquete) {

        for (int i = 0; i < clientes.size(); i++) {

            DireccionRed cliente = clientes.get(i);

            if (cliente.ip.equals(paquete.getAddress())
                    && cliente.puerto == paquete.getPort()) {

                clientes.remove(i);

                cantidadClientes--;

                System.out.println(
                        "Se desconecto un jugador."
                );

                break;
            }
        }
    }

    public void enviarMensaje(String mensaje, java.net.InetAddress ip, int puerto) {

        if (socket == null || socket.isClosed()) {
            return;
        }

        try {

            byte[] datos = mensaje.getBytes(
                    StandardCharsets.UTF_8
            );

            DatagramPacket paquete = new DatagramPacket(
                    datos,
                    datos.length,
                    ip,
                    puerto
            );

            socket.send(paquete);

        } catch (IOException e) {

            System.out.println(
                    "Error al enviar un mensaje."
            );
        }
    }

    public void enviarATodos(String mensaje) {

        for (DireccionRed cliente : clientes) {

            enviarMensaje(
                    mensaje,
                    cliente.ip,
                    cliente.puerto
            );
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
