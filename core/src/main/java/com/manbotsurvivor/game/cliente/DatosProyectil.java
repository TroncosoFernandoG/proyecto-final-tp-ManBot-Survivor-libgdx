package com.manbotsurvivor.game.cliente;

	//Un proyectil dentro del mensaje Proyectiles:
	//Laser: L,x,y,numeroJugador   Granada: G,x,y   Explosion: X,x,y,radio,milisegundos
	public class DatosProyectil {
	
	 private final String tipo;
	 private final float x;
	 private final float y;
	 private final int numeroJugador;
	 private final float radio;
	 private final float tiempoExplosion;
	
	 public DatosProyectil(String[] datos) {
	     tipo = datos[0];
	     x = Float.parseFloat(datos[1]);
	     y = Float.parseFloat(datos[2]);
	     numeroJugador = tipo.equals(ProtocolosRed.PROYECTIL_LASER) ? Integer.parseInt(datos[3]) : 0;
	     radio = tipo.equals(ProtocolosRed.PROYECTIL_EXPLOSION) ? Float.parseFloat(datos[3]) : 0;
	     tiempoExplosion = tipo.equals(ProtocolosRed.PROYECTIL_EXPLOSION) ? Integer.parseInt(datos[4]) / 1000f : 0;
	 }
	
	 public String getTipo() {
	     return tipo;
	 }
	
	 public float getX() {
	     return x;
	 }
	
	 public float getY() {
	     return y;
	 }
	
	 public int getNumeroJugador() {
	     return numeroJugador;
	 }
	
	 public float getRadio() {
	     return radio;
	 }
	
	 public float getTiempoExplosion() {
	     return tiempoExplosion;
	 }
	}