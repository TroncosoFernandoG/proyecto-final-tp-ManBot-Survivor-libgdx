package com.manbotsurvivor.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.manbotsurvivor.game.elementos.GestorAudio;
import com.manbotsurvivor.game.pantallas.PantallaJuego;
import com.manbotsurvivor.game.pantallas.PantallaMenu;
import com.manbotsurvivor.game.pantallas.PantallaOpciones;
import com.badlogic.gdx.Screen;

public class ManBotSurvivor extends Game implements NavegacionPantallas{
	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208;
	
    public SpriteBatch batch;
    public GestorAudio gestorAudio;

    @Override
    public void create() {
        batch = new SpriteBatch();
        gestorAudio = new GestorAudio();

        mostrarMenu();
    }
    
    @Override
    public void comenzarPartida() {
        setScreen(
            new PantallaJuego(batch, this, V_WIDTH, V_HEIGHT)
        );
    }
    
    @Override
    public void mostrarMenu() {
        setScreen(
            new PantallaMenu(batch, this, V_WIDTH, V_HEIGHT)
        );
    }
    
    @Override
    public void mostrarOpciones() {
        setScreen(
            new PantallaOpciones(gestorAudio, this, V_WIDTH, V_HEIGHT)
        );
    }
    
    @Override
    public void salir() {
        Gdx.app.exit();
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
        
        if (gestorAudio != null) {
            gestorAudio.disponer();
        }

        if (batch != null) {
            batch.dispose();
        }
    }
    
    
}
