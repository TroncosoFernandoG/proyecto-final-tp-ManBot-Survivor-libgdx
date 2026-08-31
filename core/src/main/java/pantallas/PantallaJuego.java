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
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import elementos.Mapa;

public class PantallaJuego implements Screen {
    private ManBotSurvivor game;
    private OrthographicCamera camaraJuego;
    private Viewport gamePort;
    private Hud hud;
    private Mapa mapa;
    private OrthogonalTiledMapRenderer renderizadorMapa;
    private float anchoMapa;
    private float altoMapa;
    private ControladorEntrada controladorEntrada;
    private Jugador jugador;
    
    public PantallaJuego(ManBotSurvivor game) {

        this.game = game;

        controladorEntrada = new ControladorEntrada();

        mapa = new Mapa();

        anchoMapa = mapa.obtenerMapa().getProperties().get("width", Integer.class) * mapa.obtenerMapa().getProperties().get("tilewidth", Integer.class);

        altoMapa = mapa.obtenerMapa().getProperties().get("height", Integer.class) * mapa.obtenerMapa().getProperties().get("tileheight", Integer.class);

        jugador = new Jugador(200, 100, 100, controladorEntrada, anchoMapa, altoMapa);

        camaraJuego = new OrthographicCamera();

        gamePort = new FitViewport( ManBotSurvivor.V_WIDTH, ManBotSurvivor.V_HEIGHT, camaraJuego);

        camaraJuego.position.set(ManBotSurvivor.V_WIDTH / 2, ManBotSurvivor.V_HEIGHT / 2, 0 );

        camaraJuego.update();

        renderizadorMapa = new OrthogonalTiledMapRenderer(mapa.obtenerMapa());
        
        hud = new Hud(game.batch);

        mapa = new Mapa();
    }

    @Override
    public void show() {
    	Gdx.input.setInputProcessor(controladorEntrada);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        jugador.actualizar(delta);
        
        camaraJuego.position.set(jugador.obtenerPosicionX() + 16, jugador.obtenerPosicionY() + 16, 0);
        
        float mitadAnchoCamara = camaraJuego.viewportWidth / 2;
        float mitadAltoCamara = camaraJuego.viewportHeight / 2;

        if (camaraJuego.position.x < mitadAnchoCamara) {
            camaraJuego.position.x = mitadAnchoCamara;
        }

        if (camaraJuego.position.x > anchoMapa - mitadAnchoCamara) {
            camaraJuego.position.x = anchoMapa - mitadAnchoCamara;
        }

        if (camaraJuego.position.y < mitadAltoCamara) {
            camaraJuego.position.y = mitadAltoCamara;
        }

        if (camaraJuego.position.y > altoMapa - mitadAltoCamara) {
            camaraJuego.position.y = altoMapa - mitadAltoCamara;
        }

        camaraJuego.update();

        renderizadorMapa.setView(camaraJuego);
        renderizadorMapa.render();

        game.batch.setProjectionMatrix(camaraJuego.combined);

        game.batch.begin();

        jugador.dibujar(camaraJuego, game.batch);

        game.batch.end();

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

        mapa.dispose();

        renderizadorMapa.dispose();
    }
}
