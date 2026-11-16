package com.manbotsurvivor.game.pantallas;

import java.util.ArrayList;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.manbotsurvivor.game.NavegacionPantallas;
import com.manbotsurvivor.game.elementos.ControladorEntrada;
import com.manbotsurvivor.game.elementos.EstadoPartida;
import com.manbotsurvivor.game.elementos.Mapa;
import com.manbotsurvivor.game.elementos.SistemaCombate;
import com.manbotsurvivor.game.enemigos.ChipEnergia;
import com.manbotsurvivor.game.enemigos.DroneBasico;
import com.manbotsurvivor.game.enemigos.Enemigo;
import com.manbotsurvivor.game.enemigos.NanoBot;
import com.manbotsurvivor.game.enemigos.RobotTanque;
import com.manbotsurvivor.game.escenas.Hud;
import com.manbotsurvivor.game.escenas.PanelResultado;
import com.manbotsurvivor.game.escenas.SelectorMejoras;
import com.manbotsurvivor.game.gestores.GestorEntidades;
import com.manbotsurvivor.game.gestores.GestorOleadas;
import com.manbotsurvivor.game.gestores.GestorPartida;
import com.manbotsurvivor.game.gestores.GestorProgresion;
import com.manbotsurvivor.game.jugador.Jugador;

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
    private Jugador segundoJugador;
    private ControladorEntrada controladorSegundoJugador;
    private ArrayList<Enemigo> enemigos;
    private ArrayList<ChipEnergia> chipsEnergia;
    private ShapeRenderer formaEnemigo;
    private BitmapFont fuentePausa;
    private GestorPartida gestorPartida;
    private GestorOleadas gestorOleadas;
    private GestorProgresion gestorProgresion;
    private GestorProgresion gestorProgresion2;
    private SistemaCombate sistemaCombate2;
    private ArrayList<Jugador> jugadores;
    private static final boolean MOSTRAR_RANGO_ATAQUE = false;
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

        float limiteMapaAncho = 100 * 32;
        float limiteMapaAlto = 100 * 32;
        
        anchoMapa = 100 * 32;
        altoMapa = 100 * 32;

        jugador = new Jugador(200, 100, 100, controladorEntrada, limiteMapaAncho, limiteMapaAlto, mapa);
        
        controladorSegundoJugador = new ControladorEntrada(true);

        segundoJugador = new Jugador(260, 100, 100, controladorSegundoJugador,limiteMapaAncho,limiteMapaAlto,mapa);
        
        jugadores = new ArrayList<>();
        jugadores.add(jugador);
        jugadores.add(segundoJugador);

        sistemaCombate = new SistemaCombate(jugador, enemigos, Color.RED);
        sistemaCombate2 = new SistemaCombate(segundoJugador, enemigos, Color.CYAN);
        
        formaEnemigo = new ShapeRenderer();

        fuentePausa = new BitmapFont();

        gestorEntidades = new GestorEntidades(jugadores, enemigos, chipsEnergia);
        
        camaraJuego = new OrthographicCamera();

        gamePort = new FitViewport(anchoVista, altoVista, camaraJuego);
        
        camaraJuego.zoom = 2.0f;

        camaraJuego.position.set(anchoVista / 2, altoVista / 2, 0);

        camaraJuego.update();

        renderizadorMapa = new OrthogonalTiledMapRenderer(mapa.obtenerMapa());
        
        gestorPartida = new GestorPartida();
        
        gestorOleadas = new GestorOleadas(enemigos, jugadores, mapa, gestorPartida, anchoMapa, altoMapa);

        hud = new Hud(batch, jugador, segundoJugador, gestorPartida);
        
        gestorProgresion = new GestorProgresion(jugador, sistemaCombate, gestorPartida);
        gestorProgresion2 = new GestorProgresion(segundoJugador, sistemaCombate2, gestorPartida);

        selectorMejoras = new SelectorMejoras();
        
        panelResultado = new PanelResultado();
        
    }

    private boolean haySeleccionActiva() {
        return gestorProgresion.haySeleccionActiva() || gestorProgresion2.haySeleccionActiva();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(
            new InputMultiplexer(controladorSegundoJugador, controladorEntrada)
        );
    }

    
    
    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        if (controladorEntrada.consumirPulsacionEscape() && !haySeleccionActiva()) {
            if (gestorPartida.estaEnCurso()) {
                gestorPartida.pausar();
            } else if (gestorPartida.estaPausada()) {
                gestorPartida.reanudar();
            }
        }

        if (gestorPartida.estaPausada() && controladorEntrada.consumirPulsacionM() && !haySeleccionActiva()) {
            navegacion.mostrarMenu();
            return;
        }

        if (!gestorPartida.estaPausada()) {
            controladorEntrada.consumirPulsacionM();
        }

        gestorPartida.actualizar(delta, Math.max(jugador.obtenerVida(), segundoJugador.obtenerVida()));

        if (gestorPartida.estaEnCurso()) {
            jugador.actualizar(delta);
            segundoJugador.actualizar(delta);

            for (Enemigo enemigo : enemigos) {
                enemigo.actualizar(delta, jugadores, enemigos);
            }

            sistemaCombate.actualizar(delta);
            sistemaCombate2.actualizar(delta);

            gestorEntidades.eliminarEnemigosMuertos();
            gestorEntidades.recogerChips();
            gestorProgresion.actualizar();

            // Un jugador elige a la vez: el 2 espera a que el 1 termine
            if (!gestorProgresion.haySeleccionActiva()) {
                gestorProgresion2.actualizar();
            }
            
            gestorOleadas.actualizar(delta);
            
            gestorPartida.actualizar(0, Math.max(jugador.obtenerVida(), segundoJugador.obtenerVida()));
            
        }
        
        float centroX;
        float centroY;

        if (jugador.estaVivo() && segundoJugador.estaVivo()) {
            centroX = (jugador.obtenerPosicionX() + segundoJugador.obtenerPosicionX()) / 2f + 16f;
            centroY = (jugador.obtenerPosicionY() + segundoJugador.obtenerPosicionY()) / 2f + 16f;
        } else if (jugador.estaVivo()) {
            centroX = jugador.obtenerPosicionX() + 16f;
            centroY = jugador.obtenerPosicionY() + 16f;
        } else {
            centroX = segundoJugador.obtenerPosicionX() + 16f;
            centroY = segundoJugador.obtenerPosicionY() + 16f;
        }

        camaraJuego.position.set(centroX, centroY, 0);
        
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

        batch.setColor(0.5f, 0.8f, 1f, 1f);
        segundoJugador.dibujar(camaraJuego, batch);
        batch.setColor(Color.WHITE);
        
        for (Enemigo enemigo : enemigos) {
            enemigo.dibujar(camaraJuego, batch);
        }
        sistemaCombate.dibujarGranadas(batch);
        sistemaCombate2.dibujarGranadas(batch);
        
        batch.end();
        
        formaEnemigo.setProjectionMatrix(camaraJuego.combined);

        formaEnemigo.begin(ShapeRenderer.ShapeType.Filled);

        sistemaCombate.dibujarDisparo(formaEnemigo);
        sistemaCombate2.dibujarDisparo(formaEnemigo);

        for (ChipEnergia chip : chipsEnergia) {
            chip.dibujar(formaEnemigo);
        }

        formaEnemigo.end();

        if (MOSTRAR_RANGO_ATAQUE) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            formaEnemigo.setProjectionMatrix(camaraJuego.combined);
            formaEnemigo.begin(ShapeRenderer.ShapeType.Line);

            sistemaCombate.dibujarRango(formaEnemigo);
            sistemaCombate2.dibujarRango(formaEnemigo);

            formaEnemigo.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
        
        
        batch.setProjectionMatrix(hud.obtenerCamara().combined);
        
        if (!haySeleccionActiva() && !gestorPartida.terminoLaPartida()) {
        	
        hud.actualizar();

        hud.dibujar();
        
        }
        
        if (gestorPartida.estaPausada() && !haySeleccionActiva()) {
            batch.begin();
            
            fuentePausa.getData().setScale(1.2f);
            fuentePausa.draw(batch, "PAUSA", 176, 155);
            fuentePausa.getData().setScale(0.7f);
            fuentePausa.draw(batch, "ESC - Continuar", 170, 105);
            fuentePausa.draw(batch, "M - Volver al menu", 170, 85);
            
            batch.end();
        }
        
        if (gestorProgresion.haySeleccionActiva()) {
            int seleccion = controladorEntrada.consumirSeleccionMejora();

            if (seleccion != 0) {
                gestorProgresion.elegir(seleccion);
            }
        } else if (gestorProgresion2.haySeleccionActiva()) {
            int seleccion = controladorSegundoJugador.consumirSeleccionMejora();

            if (seleccion != 0) {
                gestorProgresion2.elegir(seleccion);
            }
        }

        if (gestorProgresion.haySeleccionActiva()) {
            selectorMejoras.dibujar(batch, fuentePausa, gestorProgresion, 1, hud.obtenerCamara().viewportWidth, hud.obtenerCamara().viewportHeight);
        } else if (gestorProgresion2.haySeleccionActiva()) {
            selectorMejoras.dibujar(batch, fuentePausa, gestorProgresion2, 2, hud.obtenerCamara().viewportWidth, hud.obtenerCamara().viewportHeight);
        }

        if (gestorPartida.terminoLaPartida()) {
            panelResultado.dibujar(batch, fuentePausa, gestorPartida, jugador, segundoJugador, hud.obtenerCamara().viewportWidth, hud.obtenerCamara().viewportHeight);

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
        segundoJugador.disponer();
        sistemaCombate.disponer();
        sistemaCombate2.disponer();

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