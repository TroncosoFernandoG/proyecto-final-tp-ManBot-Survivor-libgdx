package com.manbotsurvivor.game.cliente;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// Hilo del cliente: recibe los paquetes del servidor y guarda los datos para que
// PantallaJuego los dibuje. El cliente no calcula nada del juego.
// Las listas se reemplazan enteras (volatile) para que el hilo del juego nunca lea una lista a medio armar.
public class HiloCliente extends Thread {

    private volatile InetAddress direccionServer;
    private int puertoServer = ProtocolosRed.PUERTO_SERVIDOR;
    private DatagramSocket puertoCliente;
    private volatile boolean fin = false;

    private volatile int numeroJugadorCliente = 0;

    private volatile DatosPartida datosPartida;
    private volatile DatosJugador[] datosJugadores = new DatosJugador[2];
    private volatile DatosSeleccion[] datosSelecciones = new DatosSeleccion[2];
    private volatile List<DatosEnemigo> enemigos = new ArrayList<>();
    private volatile List<DatosProyectil> proyectiles = new ArrayList<>();
    private volatile List<float[]> chips = new ArrayList<>();

    private volatile boolean empieza = false;

    private volatile boolean servidorEncontrado = false;
    private volatile long momentoEncontrado = 0;
    private volatile boolean servidorConfirmado = false;
    private volatile boolean servidorDesconectado = false;

    private volatile long ultimoMensaje = 0;

    private final long TIEMPO_MAXIMO_SIN_RESPUESTA = 5000;

    private volatile boolean seDesconectoUnJugador = false;

    private volatile int cuentaRegresiva = 0;

    private volatile boolean hayEspacio = true;

    private volatile boolean finSesion = false;

    private volatile boolean esperandoNuevaPartida = false;
    private volatile boolean nuevaPartida = false;

    public HiloCliente() {
        super("HiloCliente");
        try {
            direccionServer = InetAddress.getByName("255.255.255.255");
            puertoCliente = new DatagramSocket(); // Puerto libre elegido por el sistema operativo
            puertoCliente.setSoTimeout(1000);
            puertoCliente.setBroadcast(true);
        } catch (SocketException | UnknownHostException evento) {
            evento.printStackTrace();
        }
    }

    // Se manda directo al servidor (antes de encontrarlo, direccionServer es 255.255.255.255)
    public void enviarMensaje(String mensaje) {
        if (puertoCliente == null || puertoCliente.isClosed()) {
            System.err.println("Error, cliente no disponible para enviar mensajes.");
            return;
        }

        enviar(mensaje, direccionServer);
    }

    // Busca el servidor en la red local sin escribir su IP.
    // Primero buscamos y despues nos conectamos SOLO a la IP que respondio: asi un mismo
    // cliente nunca ocupa dos lugares aunque el broadcast salga por varias placas de red.
    public void buscarServidor() {
        if (puertoCliente == null || puertoCliente.isClosed()) {
            return;
        }

        for (InetAddress direccion : obtenerDireccionesBroadcast()) {
            enviar(ProtocolosRed.BUSCAR_SERVIDOR, direccion);
        }
    }

    private void enviar(String mensaje, InetAddress destino) {
        byte[] data = mensaje.getBytes(StandardCharsets.UTF_8);
        DatagramPacket dp = new DatagramPacket(data, data.length, destino, puertoServer);
        try {
            puertoCliente.send(dp);
        } catch (IOException event) {
            System.err.println("No se pudo enviar a " + destino.getHostAddress() + ": " + event.getMessage());
        }
    }

    // 255.255.255.255 + el broadcast de cada placa de red (en Windows, con varias placas,
    // 255.255.255.255 sale por una sola) + esta misma computadora.
    private Set<InetAddress> obtenerDireccionesBroadcast() {
        Set<InetAddress> direcciones = new LinkedHashSet<>();

        try {
            direcciones.add(InetAddress.getByName("255.255.255.255"));
        } catch (UnknownHostException error) {
            System.err.println(error.getMessage());
        }

        direcciones.add(InetAddress.getLoopbackAddress());

        try {
            for (NetworkInterface interfaz : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!interfaz.isUp() || interfaz.isLoopback()) {
                    continue;
                }

                for (InterfaceAddress direccion : interfaz.getInterfaceAddresses()) {
                    if (direccion.getBroadcast() != null) {
                        direcciones.add(direccion.getBroadcast());
                    }
                }
            }
        } catch (SocketException error) {
            System.err.println("No se pudieron leer las placas de red: " + error.getMessage());
        }

        return direcciones;
    }

    public void reiniciarPartida() {
        empieza = false;
        cuentaRegresiva = 0;
        esperandoNuevaPartida = false;
        datosPartida = null;
        datosJugadores = new DatosJugador[2];
        datosSelecciones = new DatosSeleccion[2];
        enemigos = new ArrayList<>();
        proyectiles = new ArrayList<>();
        chips = new ArrayList<>();
        ultimoMensaje = System.currentTimeMillis();
    }

    @Override
    public void run() {

        if (puertoCliente == null) {
            System.err.println("El socket del cliente no está disponible.");
            return;
        }

        while (!fin && !puertoCliente.isClosed()) {

            byte[] data = new byte[ProtocolosRed.TAMAÑO_PAQUETE];
            DatagramPacket dp = new DatagramPacket(data, data.length);

            try {
                puertoCliente.receive(dp);
                procesarMensaje(dp);

            } catch (SocketTimeoutException timeout) {
                // Paso 1 segundo sin paquetes: seguimos para revisar la conexion
            } catch (IOException event) {
                if (puertoCliente.isClosed()) {
                    break;
                }

                // En Windows, si el servidor se cerro, receive() puede tirar "Connection reset".
                // Seguimos: la caida se detecta con el tiempo maximo sin respuesta.
                System.err.println("Error al recibir: " + event.getMessage());
            }

            revisarConexionServidor();
        }

        System.out.println("HiloCliente terminado.");
    }

    private void procesarMensaje(DatagramPacket dp) {
        // Una vez que encontramos el servidor, ignoramos paquetes de otras computadoras
        if (servidorEncontrado && !dp.getAddress().equals(direccionServer)) {
            return;
        }

        String mensaje = new String(dp.getData(), 0, dp.getLength(), StandardCharsets.UTF_8).trim();

        try {
            evaluarMensaje(mensaje, dp);
        } catch (RuntimeException error) {
            // NumberFormatException, ArrayIndexOutOfBounds... un paquete dañado no tiene que cerrar el juego
            System.err.println("Mensaje invalido descartado: " + mensaje);
        }
    }

    private void evaluarMensaje(String mensaje, DatagramPacket dp) {
        switch (mensaje) {
            case ProtocolosRed.SERVIDOR_ENCONTRADO -> {
                if (!servidorEncontrado) {
                    direccionServer = dp.getAddress();
                    servidorEncontrado = true;
                    momentoEncontrado = System.currentTimeMillis();
                    System.out.println("Servidor encontrado en " + direccionServer.getHostAddress());
                }
                return;
            }
            case ProtocolosRed.ESTOY_ACA -> {
                ultimoMensaje = System.currentTimeMillis();
                return;
            }
            case ProtocolosRed.JUGADOR_DESCONECTADO -> {
                seDesconectoUnJugador = true;
                return;
            }
            case ProtocolosRed.OK -> {
                confirmarServidor(dp.getAddress());
                return;
            }
            case ProtocolosRed.EMPEZAR -> {
                empieza = true;
                return;
            }
            case ProtocolosRed.ERROR_LIMITE_CLIENTES -> {
                hayEspacio = false;
                return;
            }
            case ProtocolosRed.FIN_SESION -> {
                finSesion = true;
                return;
            }
            case ProtocolosRed.ESPERANDO_NUEVA_PARTIDA -> {
                esperandoNuevaPartida = true;
                return;
            }
            case ProtocolosRed.NUEVA_PARTIDA -> {
                reiniciarPartida();
                nuevaPartida = true;
                return;
            }
            case ProtocolosRed.SERVIDOR_CERRADO -> {
                servidorDesconectado = true;
                System.out.println("El servidor se cerró.");
                return;
            }
        }

        ultimoMensaje = System.currentTimeMillis();

        String[] mensajePorPartes = mensaje.split(ProtocolosRed.SEPARADOR, -1);

        switch (mensajePorPartes[0]) {
            case ProtocolosRed.NUMERO_JUGADOR -> {
                confirmarServidor(dp.getAddress());
                numeroJugadorCliente = Integer.parseInt(mensajePorPartes[1]);
            }
            case ProtocolosRed.CUENTA_REGRESIVA -> cuentaRegresiva = Integer.parseInt(mensajePorPartes[1]);
            case ProtocolosRed.PARTIDA -> {
                datosPartida = new DatosPartida(mensajePorPartes);
                empieza = true; // Por si se perdio el paquete "Empezar"
            }
            case ProtocolosRed.JUGADOR -> {
                DatosJugador datos = new DatosJugador(mensajePorPartes);
                DatosJugador[] copia = datosJugadores.clone();
                copia[datos.getNumero() - 1] = datos;
                datosJugadores = copia;

                if (datos.isConectado() && datos.getNumero() != numeroJugadorCliente) {
                    seDesconectoUnJugador = false; // El compañero volvio
                }
            }
            case ProtocolosRed.SELECCION -> {
                DatosSeleccion datos = new DatosSeleccion(mensajePorPartes);
                DatosSeleccion[] copia = datosSelecciones.clone();
                copia[datos.getNumero() - 1] = datos;
                datosSelecciones = copia;
            }
            case ProtocolosRed.ENEMIGOS -> procesarEnemigos(mensajePorPartes[1]);
            case ProtocolosRed.PROYECTILES -> procesarProyectiles(mensajePorPartes[1]);
            case ProtocolosRed.CHIPS -> procesarChips(mensajePorPartes[1]);
            default -> { }
        }
    }

    private void confirmarServidor(InetAddress ipServidor) {
        if (!servidorConfirmado) {
            direccionServer = ipServidor;
            servidorEncontrado = true;
            servidorConfirmado = true;
            servidorDesconectado = false;
            ultimoMensaje = System.currentTimeMillis();
            System.out.println("Conectado al servidor " + ipServidor.getHostAddress());
        }
    }

    private String[] dividirElementos(String datos) {
        if (datos.isEmpty()) {
            return new String[0];
        }
        return datos.split(ProtocolosRed.SEPARADOR_ELEMENTO);
    }

    private void procesarEnemigos(String datos) {
        List<DatosEnemigo> lista = new ArrayList<>();

        for (String elemento : dividirElementos(datos)) {
            lista.add(new DatosEnemigo(elemento.split(ProtocolosRed.SEPARADOR_DATO)));
        }

        enemigos = lista;
    }

    private void procesarProyectiles(String datos) {
        List<DatosProyectil> lista = new ArrayList<>();

        for (String elemento : dividirElementos(datos)) {
            lista.add(new DatosProyectil(elemento.split(ProtocolosRed.SEPARADOR_DATO)));
        }

        proyectiles = lista;
    }

    private void procesarChips(String datos) {
        List<float[]> lista = new ArrayList<>();

        for (String elemento : dividirElementos(datos)) {
            String[] posicion = elemento.split(ProtocolosRed.SEPARADOR_DATO);
            lista.add(new float[] { Float.parseFloat(posicion[0]), Float.parseFloat(posicion[1]) });
        }

        chips = lista;
    }

    private void revisarConexionServidor() {

        // Respondio la busqueda pero nunca acepto la conexion: volvemos a buscar
        if (servidorEncontrado && !servidorConfirmado
                && System.currentTimeMillis() - momentoEncontrado >= TIEMPO_MAXIMO_SIN_RESPUESTA) {
            try {
                direccionServer = InetAddress.getByName("255.255.255.255");
            } catch (UnknownHostException error) {
                System.err.println(error.getMessage());
            }
            servidorEncontrado = false;
            return;
        }

        if (!servidorConfirmado || servidorDesconectado) {
            return;
        }

        long tiempoSinRespuesta = System.currentTimeMillis() - ultimoMensaje;

        if (tiempoSinRespuesta >= TIEMPO_MAXIMO_SIN_RESPUESTA) {
            servidorDesconectado = true;

            System.out.println("El servidor dejó de responder.");
        }
    }

    public void finalizarHilo() {
        fin = true;

        if (puertoCliente != null && !puertoCliente.isClosed()) {
            puertoCliente.close();
        }

        try {
            join(1500); // Esperamos a que el hilo termine de verdad
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // true una sola vez cuando el servidor arranca una partida nueva
    public boolean consumirNuevaPartida() {
        boolean valor = nuevaPartida;
        nuevaPartida = false;
        return valor;
    }

    public int getCuentaRegresiva() {
        return cuentaRegresiva;
    }

    public int getNumeroJugador() {
        return numeroJugadorCliente;
    }

    public DatosPartida getDatosPartida() {
        return datosPartida;
    }

    public DatosJugador getDatosJugador(int numero) {
        return datosJugadores[numero - 1];
    }

    public DatosSeleccion getDatosSeleccion(int numero) {
        return datosSelecciones[numero - 1];
    }

    public List<DatosEnemigo> getEnemigos() {
        return enemigos;
    }

    public List<DatosProyectil> getProyectiles() {
        return proyectiles;
    }

    public List<float[]> getChips() {
        return chips;
    }

    public boolean isSeDesconectoUnJugador() {
        return seDesconectoUnJugador;
    }

    public boolean isEmpieza() {
        return empieza;
    }

    public boolean isServidorDesconectado() {
        return servidorDesconectado;
    }

    public boolean isServidorEncontrado() {
        return servidorEncontrado;
    }

    public boolean isServidorConfirmado() {
        return servidorConfirmado;
    }

    public boolean isHayEspacio() {
        return hayEspacio;
    }

    public boolean isFinSesion() {
        return finSesion;
    }

    public boolean isEsperandoNuevaPartida() {
        return esperandoNuevaPartida;
    }
}