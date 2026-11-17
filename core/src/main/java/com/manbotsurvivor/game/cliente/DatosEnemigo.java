package com.manbotsurvivor.game.cliente;

	//Un enemigo dentro del mensaje Enemigos: id,tipo,x,y
	public class DatosEnemigo {
	
	 private final int id;
	 private final String tipo;
	 private final float x;
	 private final float y;
	
	 public DatosEnemigo(String[] datos) {
	     id = Integer.parseInt(datos[0]);
	     tipo = datos[1];
	     x = Float.parseFloat(datos[2]);
	     y = Float.parseFloat(datos[3]);
	 }
	
	 public int getId() {
	     return id;
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
	}
