package com.manbotsurvivor.game.cliente;

import java.util.ArrayList;
import java.util.List;

import com.manbotsurvivor.game.jugador.Mejora;

	// Seleccion de mejora de un jugador (mensaje Seleccion)
	public class DatosSeleccion {
	
	    private final int numero;
	    private final int idSeleccion;
	    private final boolean activa;
	    private final List<Mejora> opciones = new ArrayList<>();
	
	    public DatosSeleccion(String[] partes) {
	        numero = Integer.parseInt(partes[1]);
	        idSeleccion = Integer.parseInt(partes[2]);
	        activa = partes[3].equals("1");
	
	        if (partes.length > 4 && !partes[4].isEmpty()) {
	            for (String opcion : partes[4].split(ProtocolosRed.SEPARADOR_OPCION)) {
	                opciones.add(Mejora.values()[Integer.parseInt(opcion)]);
	            }
	        }
	    }
	
	    public int getNumero() {
	        return numero;
	    }
	
	    public int getIdSeleccion() {
	        return idSeleccion;
	    }
	
	    public boolean isActiva() {
	        return activa;
	    }
	
	    public List<Mejora> getOpciones() {
	        return opciones;
	    }
	}