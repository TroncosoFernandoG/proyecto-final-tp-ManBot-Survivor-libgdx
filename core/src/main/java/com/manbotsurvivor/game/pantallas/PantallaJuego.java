package com.manbotsurvivor.game.pantallas;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.manbotsurvivor.game.NavegacionPantallas;
import com.manbotsurvivor.game.elementos.ChipEnergia;
import com.manbotsurvivor.game.elementos.ControladorEntrada;
import com.manbotsurvivor.game.elementos.Enemigo;
import com.manbotsurvivor.game.elementos.EstadoPartida;
import com.manbotsurvivor.game.elementos.GestorEntidades;
import com.manbotsurvivor.game.elementos.GestorOleadas;
import com.manbotsurvivor.game.elementos.GestorPartida;
import com.manbotsurvivor.game.elementos.GestorProgresion;
import com.manbotsurvivor.game.elementos.Jugador;
import com.manbotsurvivor.game.elementos.Mapa;
import com.manbotsurvivor.game.elementos.SistemaCombate;
import com.manbotsurvivor.game.escenas.Hud;
import com.manbotsurvivor.game.escenas.PanelResultado;
import com.manbotsurvivor.game.escenas.SelectorMejoras;
import com.manbotsurvivor.game.elementos.DroneBasico;
import com.manbotsurvivor.game.elementos.RobotTanque;
import com.manbotsurvivor.game.elementos.NanoBot;

public class PantallaJuego implements Screen {
	private final SpriteBatch batch;
	private final NavegacionPantallas navegacion;
	private final int anchoVista;
	private final int altoVista;
	private SistemaCombate sistemaCombate;
	private GestorEntidades gestorEntidades;
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
    private BitmapFont fuentePausa;
    private GestorPartida gestorPartida;
    private GestorOleadas gestorOleadas;
    private GestorProgresion gestorProgresion;
    private SelectorMejoras selectorMejoras;
    private PanelResultado panelResultado;
    
    public PantallaJuego(SpriteBatch batch, NavegacionPantallas navegacion, int anchoVista, int altoVista) {

    	this.batch = batch;
        this.navegacion = navegacion;
        this.anchoVista = anchoVista;
        this.altoVista = altoVista;

        enemigos = new ArrayList<>();
        chipsEnergia = new ArrayList<>();
        controladorEntrada = new ControladorEntrada();
        

        mapa = new Mapa();

        float limiteMapaAncho = 30 * 32;
        float limiteMapaAlto = 20 * 32;
        
        anchoMapa = 30 * 32;
        altoMapa = 20 * 32;

        jugador = new Jugador(200, 100, 100, controladorEntrada, limiteMapaAncho, limiteMapaAlto, mapa);
        
        sistemaCombate = new SistemaCombate(jugador, enemigos);
        
        formaEnemigo = new ShapeRenderer();

        fuentePausa = new BitmapFont();

        gestorEntidades = new GestorEntidades(jugador, enemigos, chipsEnergia);
        
        camaraJuego = new OrthographicCamera();

        gamePort = new FitViewport(anchoVista, altoVista, camaraJuego);

        camaraJuego.position.set(anchoVista / 2, altoVista / 2, 0);

        camaraJuego.update();

        renderizadorMapa = new OrthogonalTiledMapRenderer(mapa.obtenerMapa());
        
        gestorPartida = new GestorPartida();
        
        gestorOleadas = new GestorOleadas(enemigos, jugador, mapa, gestorPartida, anchoMapa, altoMapa);

        hud = new Hud(batch, jugador, gestorPartida);
        
        gestorProgresion = new GestorProgresion(jugador, sistemaCombate, gestorPartida);

        selectorMejoras = new SelectorMejoras();
        
        panelResultado = new PanelResultado();
        
    }

    @Override
    public void show() {
    	Gdx.input.setInputProcessor(controladorEntrada);
    }

    
    
    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        if (controladorEntrada.consumirPulsacionEscape() && !gestorProgresion.haySeleccionActiva()) {
            if (gestorPartida.estaEnCurso()) {
                gestorPartida.pausar();
            } else if (gestorPartida.estaPausada()) {
                gestorPartida.reanudar();
            }
        }

        if (gestorPartida.estaPausada() && controladorEntrada.consumirPulsacionM() && !gestorProgresion.haySeleccionActiva()) {
            navegacion.mostrarMenu();
            return;
        }

        if (!gestorPartida.estaPausada()) {
            controladorEntrada.consumirPulsacionM();
        }

        gestorPartida.actualizar(delta, jugador.obtenerVida());

        if (gestorPartida.estaEnCurso()) {
            jugador.actualizar(delta);

            for (Enemigo enemigo : enemigos) {
                enemigo.actualizar(delta, jugador, enemigos);
            }

            sistemaCombate.actualizar(delta);

            gestorEntidades.eliminarEnemigosMuertos();
            gestorEntidades.recogerChips();
            gestorProgresion.actualizar();
            
            gestorOleadas.actualizar(delta);
            
            gestorPartida.actualizar(0, jugador.obtenerVida());
            
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

        batch.setProjectionMatrix(camaraJuego.combined);

        
        
        batch.begin();

        jugador.dibujar(camaraJuego, batch);
        
        for (Enemigo enemigo : enemigos) {
            enemigo.dibujar(camaraJuego, batch);
        }
        batch.end();
        
        formaEnemigo.setProjectionMatrix(camaraJuego.combined);

        formaEnemigo.begin(ShapeRenderer.ShapeType.Filled);

        sistemaCombate.dibujarDisparo(formaEnemigo);

        for (ChipEnergia chip : chipsEnergia) {
            chip.dibujar(formaEnemigo);
        }

        formaEnemigo.end();
        
        
        batch.setProjectionMatrix(hud.obtenerCamara().combined);
        
        if (!gestorProgresion.haySeleccionActiva() && !gestorPartida.terminoLaPartida()) {
        	
        hud.actualizar();

        hud.dibujar();
        
        }
        
        if (gestorPartida.estaPausada() && !gestorProgresion.haySeleccionActiva()) {
            batch.begin();
            
            fuentePausa.getData().setScale(2);
            fuentePausa.draw(batch, "PAUSA", 150, 155);
            fuentePausa.getData().setScale(0.9f);
            fuentePausa.draw(batch, "ESC - Continuar", 150, 105);
            fuentePausa.draw(batch, "M - Volver al menu", 150, 85);
            
            batch.end();
        }
        
        if (gestorProgresion.haySeleccionActiva()) {
            int seleccion = controladorEntrada.consumirSeleccionMejora();
            
            if (seleccion != 0) {
                gestorProgresion.elegir(seleccion);
            }
        }
        
        if (gestorProgresion.haySeleccionActiva()) {
            selectorMejoras.dibujar(batch, fuentePausa, gestorProgresion, hud.obtenerCamara().viewportWidth,hud.obtenerCamara().viewportHeight);
        }
		
        if (gestorPartida.terminoLaPartida()) {
            panelResultado.dibujar(batch, fuentePausa, gestorPartida, jugador, hud.obtenerCamara().viewportWidth, hud.obtenerCamara().viewportHeight);

            if (controladorEntrada.consumirPulsacionEnter()) {
                navegacion.mostrarMenu();
                return;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    	
    	gamePort.update(width, height);
    	hud.resize(width, height);
    	
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

        DroneBasico.disponerTextura();
        RobotTanque.disponerTextura();
        NanoBot.disponerTextura();

        hud.disponer();
        fuentePausa.dispose();

        mapa.dispose();
        renderizadorMapa.dispose();
        formaEnemigo.dispose();
    }
}
