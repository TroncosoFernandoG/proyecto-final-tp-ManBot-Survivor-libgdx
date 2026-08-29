package com.manbotsurvivor.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Principal extends Game {
    public SpriteBatch batch;

    //aura 
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new PlaySreen(this));
    }

    @Override
    public void render() {
       super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
