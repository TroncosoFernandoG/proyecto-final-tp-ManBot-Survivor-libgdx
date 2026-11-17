package com.manbotsurvivor.game.cliente;

	//Datos de un jugador que manda el servidor (mensaje Jugador)
	public class DatosJugador {
	
	 private final int numero;
	 private final boolean conectado;
	 private final float x;
	 private final float y;
	 private final boolean mirandoDerecha;
	 private final boolean moviendose;
	 private final int enemigosEliminados;
	
	 public DatosJugador(String[] partes) {
	     numero = Integer.parseInt(partes[1]);
	     conectado = partes[2].equals("1");
	     x = Float.parseFloat(partes[3]);
	     y = Float.parseFloat(partes[4]);
	     mirandoDerecha = partes[5].equals("1");
	     moviendose = partes[6].equals("1");
	     enemigosEliminados = Integer.parseInt(partes[7]);
	 }
	
	 public int getNumero() {
	     return numero;
	 }
	
	 public boolean isConectado() {
	     return conectado;
	 }
	
	 public float getX() {
	     return x;
	 }
	
	 public float getY() {
	     return y;
	 }
	
	 public boolean isMirandoDerecha() {
	     return mirandoDerecha;
	 }
	
	 public boolean isMoviendose() {
	     return moviendose;
	 }
	
	 public int getEnemigosEliminados() {
	     return enemigosEliminados;
	 }
	}