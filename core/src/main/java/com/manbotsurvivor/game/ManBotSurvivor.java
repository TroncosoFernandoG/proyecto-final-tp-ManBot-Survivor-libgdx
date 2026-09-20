package com.manbotsurvivor.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pantallas.PantallaJuego;
import pantallas.PantallaMenu;
import elementos.GestorAudio;

import com.badlogic.gdx.Screen;

public class ManBotSurvivor extends Game {
	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208;
	
    public SpriteBatch batch;
    public GestorAudio gestorAudio;

    @Override
    public void create() {
        batch = new SpriteBatch();
        gestorAudio = new GestorAudio();
        setScreen(new PantallaMenu(this));
    }
    
    public void comenzarPartida() {

        setScreen(new PantallaJuego(this));
    }

    @Override
    public void render() {
       super.render();
    }
    
    @Override
    public void setScreen(Screen pantallaNueva) {

        Screen pantallaAnterior = getScreen();

        super.setScreen(pantallaNueva);

        if (pantallaAnterior != null && pantallaAnterior != pantallaNueva) {
            pantallaAnterior.dispose();
        }
    }

    @Override
    public void dispose() {

        if (getScreen() != null) {
            getScreen().dispose();
        }

        gestorAudio.disponer();
        batch.dispose();
    }
    
    
}
