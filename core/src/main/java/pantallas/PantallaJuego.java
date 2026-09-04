package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.manbotsurvivor.game.ManBotSurvivor;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import elementos.ChipEnergia;
import elementos.ControladorEntrada;
import elementos.Jugador;
import escenas.Hud;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import elementos.Mapa;
import elementos.Enemigo;
import java.util.ArrayList;
import java.util.Iterator;

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
    private ArrayList<ChipEnergia> chipsEnergia;
    private ShapeRenderer formaEnemigo;
    private float tiempoAtaque;
    private final float intervaloAtaque = 1.5f;
    private boolean hayDisparo;
    private float posicionDisparoX;
    private float posicionDisparoY;
    private Enemigo enemigoObjetivoDisparo;
    private float direccionDisparoX;
    private float direccionDisparoY;
    private float velocidadDisparo;
    
    public PantallaJuego(ManBotSurvivor game) {

        this.game = game;

        enemigos = new ArrayList<>();
        chipsEnergia = new ArrayList<>();
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
        tiempoAtaque = 0;
        hayDisparo = false;
        posicionDisparoX = 0;
        posicionDisparoY = 0;
        enemigoObjetivoDisparo = null;
        direccionDisparoX = 0;
        direccionDisparoY = 0;
        velocidadDisparo = 200;

        camaraJuego = new OrthographicCamera();

        gamePort = new FitViewport(ManBotSurvivor.V_WIDTH, ManBotSurvivor.V_HEIGHT, camaraJuego);

        camaraJuego.position.set(ManBotSurvivor.V_WIDTH / 2, ManBotSurvivor.V_HEIGHT / 2, 0);

        camaraJuego.update();

        renderizadorMapa = new OrthogonalTiledMapRenderer(
            mapa.obtenerMapa()
        );

        hud = new Hud(game.batch, jugador);
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
            if (enemigo.estaVivo() && distancia < distanciaMinima) {
                distanciaMinima = distancia;
                enemigoMasCercano = enemigo;
            }
        }
        return enemigoMasCercano;
    }
    
    private void atacar() {
        Enemigo enemigoCercano = obtenerEnemigoMasCercano();
        
        if (enemigoCercano != null) {
            enemigoCercano.recibirDaño(1);
            hayDisparo = true;

            posicionDisparoX = jugador.obtenerPosicionX() + jugador.obtenerAncho() / 2;
            posicionDisparoY = jugador.obtenerPosicionY() + jugador.obtenerAlto() / 2;
            
            enemigoObjetivoDisparo = enemigoCercano;
            float diferenciaX = enemigoObjetivoDisparo.obtenerPosicionX() - posicionDisparoX;
            float diferenciaY = enemigoObjetivoDisparo.obtenerPosicionY() - posicionDisparoY;
            float distancia = (float) Math.sqrt(diferenciaX * diferenciaX + diferenciaY * diferenciaY);
            direccionDisparoX = diferenciaX / distancia;
            direccionDisparoY = diferenciaY / distancia;
        }
    }
    
    private void actualizarDisparo(float delta) {
        if (!hayDisparo) {
            return;
        }
        posicionDisparoX += direccionDisparoX * velocidadDisparo * delta;
        posicionDisparoY += direccionDisparoY * velocidadDisparo * delta;
    }
    
    private void comprobarColisionDisparo() {

        if (!hayDisparo || enemigoObjetivoDisparo == null) {
            return;
        }

        Rectangle areaDisparo = new Rectangle( posicionDisparoX, posicionDisparoY, 6, 6);
        Rectangle areaEnemigo = new Rectangle( enemigoObjetivoDisparo.obtenerPosicionX(), enemigoObjetivoDisparo.obtenerPosicionY(),
            enemigoObjetivoDisparo.obtenerAncho(), enemigoObjetivoDisparo.obtenerAlto());

        if (areaDisparo.overlaps(areaEnemigo)) {
            hayDisparo = false;
            enemigoObjetivoDisparo = null;
        }

    }
    
    private void dibujarDisparo() {

        if (hayDisparo) {

            formaEnemigo.begin(ShapeRenderer.ShapeType.Filled);
            formaEnemigo.setColor(com.badlogic.gdx.graphics.Color.RED);
            formaEnemigo.rect(posicionDisparoX, posicionDisparoY, 6, 6);
            formaEnemigo.end();

        }

    }
    
    private void eliminarEnemigosMuertos() {

        Iterator<Enemigo> iterador = enemigos.iterator();
        while (iterador.hasNext()) {
            Enemigo enemigo = iterador.next();

            if (!enemigo.estaVivo()) {
                ChipEnergia chip = new ChipEnergia(enemigo.obtenerPosicionX(), enemigo.obtenerPosicionY());

                chipsEnergia.add(chip);

                jugador.registrarEnemigoEliminado();

                iterador.remove();
            }
        }
    }
    
    private void recogerChips() {

        Iterator<ChipEnergia> iterador = chipsEnergia.iterator();

        while (iterador.hasNext()) {

            ChipEnergia chip = iterador.next();

            if (chip.hayColision(jugador.obtenerPosicionX(), jugador.obtenerPosicionY(), jugador.obtenerAncho(), jugador.obtenerAlto())) {

                jugador.ganarExperiencia(chip.obtenerExperiencia());
                
                iterador.remove();
            }
        }
    }
    
    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        jugador.actualizar(delta);
        for (Enemigo enemigo : enemigos) {
            enemigo.actualizar(delta, jugador, enemigos);
        }
        
        tiempoAtaque += delta;

        if (tiempoAtaque >= intervaloAtaque) {
            atacar();
            tiempoAtaque = 0;
        }
        actualizarDisparo(delta);
        comprobarColisionDisparo();
        eliminarEnemigosMuertos();
        recogerChips();
        
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
        
        game.batch.setProjectionMatrix(camaraJuego.combined);
        
        game.batch.begin();
        for (Enemigo enemigo : enemigos) {
            enemigo.dibujar(camaraJuego, game.batch);
        }
        game.batch.end();
        
        formaEnemigo.setProjectionMatrix(camaraJuego.combined);
        dibujarDisparo();
        for (ChipEnergia chip : chipsEnergia) {
            chip.dibujar(formaEnemigo);
        }
        
        hud.actualizar(delta);

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
