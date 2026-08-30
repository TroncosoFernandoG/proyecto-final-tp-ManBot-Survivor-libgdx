package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.manbotsurvivor.game.ManBotSurvivor;
import elementos.ControladorEntrada;
import elementos.Jugador;
import escenas.Hud;

public class PantallaJuego implements Screen {
    private ManBotSurvivor game;
    private OrthographicCamera camaraJuego;
    private Viewport gamePort;
    private Hud hud;
    private ControladorEntrada controladorEntrada;
    private Jugador jugador;
    
    public PantallaJuego(ManBotSurvivor game) {
        this.game = game;
        controladorEntrada = new ControladorEntrada();
        jugador = new Jugador(200, 100, 100, controladorEntrada);
        camaraJuego = new OrthographicCamera();
        gamePort = new FitViewport(ManBotSurvivor.V_WIDTH, ManBotSurvivor.V_HEIGHT, camaraJuego);
        camaraJuego.position.set(ManBotSurvivor.V_WIDTH / 2, ManBotSurvivor.V_HEIGHT / 2, 0);
        camaraJuego.update();
        hud = new Hud(game.batch);
    }

    @Override
    public void show() {
    	Gdx.input.setInputProcessor(controladorEntrada);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        jugador.actualizar(delta);
        jugador.dibujar(camaraJuego);
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
    	Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
    	jugador.disponer();
    }
}
