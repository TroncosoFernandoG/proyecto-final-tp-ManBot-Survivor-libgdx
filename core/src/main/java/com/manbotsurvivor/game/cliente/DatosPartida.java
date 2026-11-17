package com.manbotsurvivor.game.cliente;

import com.manbotsurvivor.game.elementos.EstadoPartida;

	//Datos generales de la partida que manda el servidor (mensaje Partida)
	public class DatosPartida {
	
	 private final EstadoPartida estado;
	 private final float tiempoEnJuego;
	 private final int vida;
	 private final int vidaMaxima;
	 private final int nivel;
	 private final int experiencia;
	 private final int experienciaNecesaria;
	 private final boolean hayJefe;
	
	 public DatosPartida(String[] partes) {
	     estado = EstadoPartida.values()[Integer.parseInt(partes[1])];
	     tiempoEnJuego = Integer.parseInt(partes[2]) / 1000f;
	     vida = Integer.parseInt(partes[3]);
	     vidaMaxima = Integer.parseInt(partes[4]);
	     nivel = Integer.parseInt(partes[5]);
	     experiencia = Integer.parseInt(partes[6]);
	     experienciaNecesaria = Integer.parseInt(partes[7]);
	     hayJefe = partes[8].equals("1");
	 }
	
	 public EstadoPartida getEstado() {
	     return estado;
	 }
	
	 public boolean terminoLaPartida() {
	     return estado == EstadoPartida.DERROTA || estado == EstadoPartida.VICTORIA;
	 }
	
	 public float getTiempoEnJuego() {
	     return tiempoEnJuego;
	 }
	
	 public int getVida() {
	     return vida;
	 }
	
	 public int getVidaMaxima() {
	     return vidaMaxima;
	 }
	
	 public int getNivel() {
	     return nivel;
	 }
	
	 public int getExperiencia() {
	     return experiencia;
	 }
	
	 public int getExperienciaNecesaria() {
	     return experienciaNecesaria;
	 }
	
	 public boolean isHayJefe() {
	     return hayJefe;
	 }
	}
