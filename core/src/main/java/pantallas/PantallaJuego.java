package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.manbotsurvivor.game.ManBotSurvivor;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import elementos.ControladorEntrada;
import elementos.Jugador;
import escenas.Hud;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import elementos.Mapa;
import elementos.Enemigo;
import java.util.ArrayList;

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
    private ArrayList<Enemigo> enemigos;
    private ShapeRenderer formaEnemigo;
    
    public PantallaJuego(ManBotSurvivor game) {

        this.game = game;

        enemigos = new ArrayList<>();
        controladorEntrada = new ControladorEntrada();

        mapa = new Mapa();

        float limiteMapaAncho = 30 * 32;
        float limiteMapaAlto = 20 * 32;
        
        anchoMapa = 30 * 32;
        altoMapa = 20 * 32;

        jugador = new Jugador(200, 100, 100, controladorEntrada, limiteMapaAncho, limiteMapaAlto, mapa);
        
        enemigos.add(new Enemigo(400, 300, 50, mapa));
        enemigos.add(new Enemigo(100, 300, 50, mapa));
        enemigos.add(new Enemigo(300, 200, 50, mapa));
        formaEnemigo = new ShapeRenderer();

        camaraJuego = new OrthographicCamera();

        gamePort = new FitViewport(ManBotSurvivor.V_WIDTH, ManBotSurvivor.V_HEIGHT, camaraJuego);

        camaraJuego.position.set(ManBotSurvivor.V_WIDTH / 2, ManBotSurvivor.V_HEIGHT / 2, 0);

        camaraJuego.update();

        renderizadorMapa = new OrthogonalTiledMapRenderer(
            mapa.obtenerMapa()
        );

        hud = new Hud(game.batch);
    }

    @Override
    public void show() {
    	Gdx.input.setInputProcessor(controladorEntrada);
    }

    private Enemigo obtenerEnemigoMasCercano() {
        Enemigo enemigoMasCercano = null;
        double distanciaMinima = Double.MAX_VALUE;

        for (Enemigo enemigo : enemigos) {
            double diferenciaX = enemigo.obtenerPosicionX() - jugador.obtenerPosicionX();
            double diferenciaY = enemigo.obtenerPosicionY() - jugador.obtenerPosicionY();
            double distancia = Math.sqrt(
                diferenciaX * diferenciaX + diferenciaY * diferenciaY
            );
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                enemigoMasCercano = enemigo;
            }
        }
        return enemigoMasCercano;
    }
    
    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        jugador.actualizar(delta);
        for (Enemigo enemigo : enemigos) {
            enemigo.actualizar(delta, jugador);
        }
        
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
        
        formaEnemigo.setProjectionMatrix(camaraJuego.combined);
        for (Enemigo enemigo : enemigos) {
            enemigo.dibujar(formaEnemigo);
        }
        

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
        
        formaEnemigo.dispose();
    }
}
