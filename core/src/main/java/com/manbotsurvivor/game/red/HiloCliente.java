package com.manbotsurvivor.game.red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class HiloCliente extends Thread{

	private DatagramSocket socket;
    private InetAddress ipServidor;
    private int puertoServidor;

    private boolean clienteActivo;
    private String ultimoMensaje;

    public HiloCliente(InetAddress ipServidor, int puertoServidor) throws SocketException {

        this.ipServidor = ipServidor;
        this.puertoServidor = puertoServidor;

        socket = new DatagramSocket();
        socket.setSoTimeout(1000);

        clienteActivo = true;
        ultimoMensaje = "";

        enviarMensaje("conexion");
    }

    @Override
    public void run() {

        while (clienteActivo) {

            try {

                byte[] datos = new byte[1024];

                DatagramPacket paquete = new DatagramPacket(datos, datos.length);

                socket.receive(paquete);

                ultimoMensaje = new String(paquete.getData()).trim();

                System.out.println("Mensaje recibido del servidor: "+ ultimoMensaje);

            } catch (SocketTimeoutException e) {

            } catch (SocketException e) {

                if (clienteActivo) {
                    System.out.println("Se cerro el socket del cliente.");
                }

                clienteActivo = false;

            } catch (IOException e) {

                System.out.println("Error al recibir datos del servidor.");
            }
        }
    }

    public void enviarMensaje(String mensaje) {

        if (socket == null || socket.isClosed()) {
            return;
        }

        try {

            byte[] datos = mensaje.getBytes();

            DatagramPacket paquete = new DatagramPacket(datos, datos.length, ipServidor, puertoServidor);

            socket.send(paquete);

        } catch (IOException e) {

            System.out.println("Error al enviar datos al servidor.");
        }
    }

    public String obtenerUltimoMensaje() {

        String mensaje = ultimoMensaje;

        ultimoMensaje = "";

        return mensaje;
    }

    public void cerrar() {

        if (socket != null && !socket.isClosed()) {

            enviarMensaje("desconexion");

            clienteActivo = false;

            socket.close();
        }
    }
	
}
