package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.manbotsurvivor.game.ManBotSurvivor;

import escenas.Hud;

public class PantallaJuego implements Screen {
    private ManBotSurvivor game;
    private OrthographicCamera camaraJuego;
    private Viewport gamePort;
    private Hud hud;
    
    public PantallaJuego(ManBotSurvivor game) {
        this.game = game;
        camaraJuego = new OrthographicCamera();
        gamePort = new FitViewport(ManBotSurvivor.V_WIDTH, ManBotSurvivor.V_HEIGHT, camaraJuego);
        hud = new Hud(game.batch);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    	
    	gamePort.update(width, height);
    	
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
