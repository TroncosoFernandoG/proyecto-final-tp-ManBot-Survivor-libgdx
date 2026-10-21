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
import com.manbotsurvivor.game.elementos.GestorPartida;
import com.manbotsurvivor.game.elementos.Jugador;
import com.manbotsurvivor.game.elementos.Mapa;
import com.manbotsurvivor.game.elementos.SistemaCombate;
import com.manbotsurvivor.game.escenas.Hud;
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
    private int oleadaActual;
    private float tiempoEntreOleadas;
    private boolean esperandoSiguienteOleada;
    
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

        hud = new Hud(batch, jugador, gestorPartida);
        
        iniciarOleada(1);
    }

    @Override
    public void show() {
    	Gdx.input.setInputProcessor(controladorEntrada);
    }
    

    private void iniciarOleada(int numeroOleada) {

    oleadaActual = numeroOleada;
    esperandoSiguienteOleada = false;
    tiempoEntreOleadas = 0;

    int cantidadEnemigos = 5 + oleadaActual * 2;

    float factorDificultad = 1 + (oleadaActual - 1) * 0.1f;

    for (int i = 0; i < cantidadEnemigos; i++) {

        float[] posicion = buscarPosicionEnemigo();

        Enemigo enemigo;

        if (i % 7 == 6) {
            enemigo = new RobotTanque(
                posicion[0], posicion[1], 20, mapa
            );
        } else if (i % 2 == 0) {
            enemigo = new NanoBot(
                posicion[0], posicion[1], mapa
            );
        } else {
            enemigo = new DroneBasico(
                posicion[0], posicion[1], 50, mapa
            );
        }

        enemigo.aumentarDificultad(factorDificultad);
        enemigos.add(enemigo);
    }
}

	private float[] buscarPosicionEnemigo() {

    float x = 0;
    float y = 0;

    for (int intento = 0; intento < 100; intento++) {

        x = com.badlogic.gdx.math.MathUtils.random(
            0, anchoMapa - 32
        );

        y = com.badlogic.gdx.math.MathUtils.random(
            0, altoMapa - 32
        );

        boolean colisionMapa = mapa.hayColision(x, y, 32, 32);

        float diferenciaX = x - jugador.obtenerPosicionX();
        float diferenciaY = y - jugador.obtenerPosicionY();

        float distancia = (float) Math.sqrt(
            diferenciaX * diferenciaX + diferenciaY * diferenciaY
        );

        if (!colisionMapa && distancia >= 150) {
            return new float[] { x, y };
        }
    }

    return new float[] { x, y };
}

	private void actualizarOleadas(float delta) {

    if (!enemigos.isEmpty()) {
        esperandoSiguienteOleada = false;
        tiempoEntreOleadas = 0;
        return;
    }

    if (!esperandoSiguienteOleada) {
        esperandoSiguienteOleada = true;
        tiempoEntreOleadas = 0;
        return;
    }

    tiempoEntreOleadas += delta;

    if (tiempoEntreOleadas >= 2f) {
        iniciarOleada(oleadaActual + 1);
    }
}

    
    
    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        if (controladorEntrada.consumirPulsacionEscape()) {
            if (gestorPartida.estaEnCurso()) {
                gestorPartida.pausar();
            } else if (gestorPartida.estaPausada()) {
                gestorPartida.reanudar();
            }
        }

        if (gestorPartida.estaPausada()
                && controladorEntrada.consumirPulsacionM()) {
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
            
            actualizarOleadas(delta);
            
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
        
        
        hud.actualizar();

        batch.setProjectionMatrix(hud.obtenerCamara().combined);

        hud.dibujar();
        
        if (gestorPartida.estaPausada()) {
            batch.begin();
            
            fuentePausa.getData().setScale(2);
            fuentePausa.draw(batch, "PAUSA", 150, 155);
            fuentePausa.getData().setScale(0.9f);
            fuentePausa.draw(batch, "ESC - Continuar", 150, 105);
            fuentePausa.draw(batch, "M - Volver al menu", 150, 85);
            
            batch.end();
        }
        
		
		if (gestorPartida.terminoLaPartida()) {
		    batch.begin();
		
		    fuentePausa.getData().setScale(2);
		
		    if (gestorPartida.obtenerEstado() == EstadoPartida.DERROTA) {
		        fuentePausa.draw(batch, "DERROTA", 145, 125);
		    } else if (gestorPartida.obtenerEstado() == EstadoPartida.VICTORIA) {
		        fuentePausa.draw(batch, "VICTORIA", 140, 125);
		    }
		
		    fuentePausa.getData().setScale(1);
		    batch.end();
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
