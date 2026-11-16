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
	    private boolean servidorEncontrado;
	    private String ultimoMensaje;

	    public HiloCliente() throws SocketException {

	        socket = new DatagramSocket();

	        socket.setBroadcast(true);
	        socket.setSoTimeout(1000);

	        puertoServidor = 50000;

	        clienteActivo = true;
	        servidorEncontrado = false;

	        ultimoMensaje = "";
	    }

	    @Override
	    public void run() {

	        long ultimoEnvio = 0;

	        while (clienteActivo) {

	            if (!servidorEncontrado && System.currentTimeMillis() - ultimoEnvio >= 2000) {

	                enviarConexion();

	                ultimoEnvio = System.currentTimeMillis();
	            }

	            try {

	                byte[] datos = new byte[1024];

	                DatagramPacket paquete = new DatagramPacket(datos, datos.length);

	                socket.receive(paquete);

	                String mensaje = new String(paquete.getData()).trim();

	                if (mensaje.startsWith("ok")) {

	                    ipServidor = paquete.getAddress();
	                    servidorEncontrado = true;
	                }

	                ultimoMensaje = mensaje;

	                System.out.println("Mensaje del servidor: " + mensaje);

	            } catch (SocketTimeoutException e) {

	               

	            } catch (SocketException e) {

	                if (clienteActivo) {
	                    System.out.println("Se cerro el socket del cliente.");
	                }

	                clienteActivo = false;

	            } catch (IOException e) {

	                System.out.println("Error al recibir datos.");
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

	            System.out.println("Error al buscar el servidor.");
	        }
	    }

	    public void enviarMensaje(String mensaje) {

	        if (!servidorEncontrado|| socket == null || socket.isClosed()) {
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

	            System.out.println("Error al enviar mensaje al servidor.");
	        }
	    }

	    public String obtenerUltimoMensaje() {

	        String mensaje = ultimoMensaje;

	        ultimoMensaje = "";

	        return mensaje;
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
