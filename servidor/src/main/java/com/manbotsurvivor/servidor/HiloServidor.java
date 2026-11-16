package com.manbotsurvivor.servidor;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class HiloServidor extends Thread{

	private DatagramSocket socket;
    private boolean servidorActivo;
    private int puertoServidor;

    public HiloServidor() {

        servidorActivo = true;
        puertoServidor = 50000;
    }

    @Override
    public void run() {

        try {

            socket = new DatagramSocket(puertoServidor);

            System.out.println("Servidor de ManBot Survivor iniciado.");
            System.out.println("Escuchando en el puerto " + puertoServidor);

            while (servidorActivo) {

                byte[] datos = new byte[1024];

                DatagramPacket paquete = new DatagramPacket(
                        datos,
                        datos.length
                );

                socket.receive(paquete);

                String mensaje = new String(
                        paquete.getData()
                ).trim();

                System.out.println("Mensaje recibido: " + mensaje);

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

            System.out.println("Error al recibir un paquete.");

        } finally {

            servidorActivo = false;

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            System.out.println("Servidor detenido.");
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
