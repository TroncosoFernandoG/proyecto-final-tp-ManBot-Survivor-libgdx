package com.manbotsurvivor.game.escenas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.manbotsurvivor.game.ManBotSurvivor;
import com.manbotsurvivor.game.elementos.Jugador;
import com.manbotsurvivor.game.elementos.GestorPartida;

public class Hud {

	private Stage stage;
	private Viewport viewport;
	private BitmapFont fuente;
	private Jugador jugador;
	private GestorPartida gestorPartida;
	
	Label vidaLabel;
	Label experienciaLabel;
	Label tiempoLabel;
	Label tiempoTranscurridoLabel;
	Label enemigosEliminadosLabel;
	
	public Hud(SpriteBatch sb, Jugador jugador, GestorPartida gestorPartida) {
		
		this.jugador = jugador;
		this.gestorPartida = gestorPartida;
		
		viewport = new FitViewport(ManBotSurvivor.V_WIDTH, ManBotSurvivor.V_HEIGHT, new OrthographicCamera());
		stage = new Stage(viewport, sb);
		
		fuente = new BitmapFont();	
		
		Table table = new Table(); 
		table.top().left();
		table.setFillParent(true);
		
		vidaLabel = new Label("Vida: 100/100",  new Label.LabelStyle(fuente, Color.WHITE));
		experienciaLabel = new Label("XP: 0/100", new Label.LabelStyle(fuente, Color.WHITE));
		tiempoLabel = new Label("Tiempo: 00:00", new Label.LabelStyle(fuente, Color.WHITE));
		tiempoTranscurridoLabel = new Label("Tiempo transcurrido: 00:00", new Label.LabelStyle(fuente, Color.WHITE));
		enemigosEliminadosLabel = new Label("Enemigos: 0", new Label.LabelStyle(fuente, Color.WHITE));
		
		table.add(vidaLabel).left().padTop(10).padLeft(10);
		table.row();
		table.add(experienciaLabel).left().padLeft(10);
		table.row();
		table.add(tiempoLabel).left().padLeft(10);
		table.row();
		table.add(tiempoTranscurridoLabel).left().padLeft(10);
		table.row();
		table.add(enemigosEliminadosLabel).left().padLeft(10);
		
		stage.addActor(table);
		
	}
	
	public void actualizar() {

	    int minutosJuego = (int) (gestorPartida.obtenerTiempoEnJuego() / 60);
	    int segundosJuego = (int) (gestorPartida.obtenerTiempoEnJuego() % 60);

	    int minutosTranscurridos = (int) (gestorPartida.obtenerTiempoTranscurrido() / 60);
	    int segundosTranscurridos = (int) (gestorPartida.obtenerTiempoTranscurrido() % 60);

	    vidaLabel.setText("Vida: " + jugador.obtenerVida() + "/" + jugador.obtenerVidaMaxima());

	    experienciaLabel.setText("XP: " + jugador.obtenerExperiencia() + "/100");

	    tiempoLabel.setText(String.format("Tiempo en juego: %02d:%02d", minutosJuego, segundosJuego));

	    tiempoTranscurridoLabel.setText(String.format("Tiempo transcurrido: %02d:%02d", minutosTranscurridos, segundosTranscurridos));

	    enemigosEliminadosLabel.setText("Enemigos: " + jugador.obtenerEnemigosEliminados());
	}
	
	public void dibujar() {
	    stage.draw();
	}
	
	public void resize(int ancho, int alto) {
	    viewport.update(ancho, alto, true);
	}
	
	public OrthographicCamera obtenerCamara() {
	    return (OrthographicCamera) stage.getCamera();
	}
	
	public void disponer() {
	    stage.dispose();
	    fuente.dispose();
	}
	
}
