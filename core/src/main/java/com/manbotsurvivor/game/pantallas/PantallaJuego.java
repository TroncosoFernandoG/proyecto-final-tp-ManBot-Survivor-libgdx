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
import java.net.SocketException;
import com.manbotsurvivor.game.red.EstadoRed;
import com.manbotsurvivor.game.red.HiloCliente;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import com.manbotsurvivor.game.enemigos.JefeMecanico;

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
    private HiloCliente hiloCliente;
    private boolean posicionesInicialesAplicadas;
    private long ultimoEnvioMovimiento;
    private HashMap<Integer, Enemigo> enemigosRed = new HashMap<>();
    private long ultimoEnvioMejora;
    private int mejoraSeleccionada;
    private long ultimoEnvioReinicio;
    private boolean reinicioSolicitado;
    
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
        
        hud = new Hud(batch, jugador, segundoJugador, gestorPartida);
        
        gestorProgresion = new GestorProgresion(jugador, sistemaCombate, gestorPartida);
        gestorProgresion2 = new GestorProgresion(segundoJugador, sistemaCombate2, gestorPartida);

        selectorMejoras = new SelectorMejoras();
        
        panelResultado = new PanelResultado();
        
        EstadoRed.reiniciar();

        posicionesInicialesAplicadas = false;

        try {

            hiloCliente = new HiloCliente();

            hiloCliente.start();

        } catch (SocketException e) {

            System.out.println("No se pudo iniciar la comunicacion UDP del cliente." );
        }
    }

    private boolean haySeleccionActiva() {
        return gestorProgresion.haySeleccionActiva() || gestorProgresion2.haySeleccionActiva();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputMultiplexer(controladorSegundoJugador, controladorEntrada));
    }

    
    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!EstadoRed.conectado || !EstadoRed.empieza || !EstadoRed.estadoInicialRecibido) {

            batch.setProjectionMatrix(camaraJuego.combined);

            batch.begin();

            fuentePausa.getData().setScale(0.7f);

            if (EstadoRed.servidorLleno) {

                fuentePausa.draw(batch, "El servidor ya tiene dos jugadores.", 20, altoVista - 20);

            } else if (EstadoRed.perdioConexion) {

                fuentePausa.draw(batch, "Se perdio la conexion. Buscando servidor...", 20, altoVista - 20);

            } else if (EstadoRed.conectado) {

                fuentePausa.draw(batch, "Esperando al otro jugador...", 20, altoVista - 20);

            } else {

                fuentePausa.draw(batch, "Buscando servidor...", 20, altoVista - 20);
            }

            batch.end();

            return;
        }

        enviarMovimientoServidor();

        actualizarDesdeServidor();

        jugador.actualizarAnimacionRed(delta);
        segundoJugador.actualizarAnimacionRed(delta);

        for (Enemigo enemigo : enemigos) {
            enemigo.actualizarAnimacionRed(delta);
        }

        float centroX;
        float centroY;

        if (EstadoRed.conectadoJugador1 && EstadoRed.conectadoJugador2) {

            centroX = (jugador.obtenerPosicionX() + segundoJugador.obtenerPosicionX()) / 2f + 16f;

            centroY = (jugador.obtenerPosicionY() + segundoJugador.obtenerPosicionY()) / 2f + 16f;

        } else if (EstadoRed.conectadoJugador1) {

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

        if (EstadoRed.conectadoJugador1) {
            jugador.dibujar(camaraJuego, batch);
        }

        if (EstadoRed.conectadoJugador2) {

            batch.setColor(0.5f, 0.8f, 1f, 1f);

            segundoJugador.dibujar(camaraJuego, batch);

            batch.setColor(Color.WHITE);
        }

        for (Enemigo enemigo : enemigos) {
            enemigo.dibujar(camaraJuego, batch);
        }

        batch.end();

        formaEnemigo.setProjectionMatrix(camaraJuego.combined);

        formaEnemigo.begin(ShapeRenderer.ShapeType.Filled);

        for (ChipEnergia chip : chipsEnergia) {
            chip.dibujar(formaEnemigo);
        }

        for (EstadoRed.ProyectilRed proyectil : EstadoRed.proyectiles) {

            if (proyectil.tipo.equals("granada")) {

                formaEnemigo.setColor(Color.ORANGE);

                if (proyectil.explotado) {

                    formaEnemigo.circle(proyectil.x, proyectil.y, proyectil.radio);

                } else {

                    formaEnemigo.circle(proyectil.x, proyectil.y, 6);
                }

            } else {

                formaEnemigo.setColor(Color.CYAN);

                formaEnemigo.circle(proyectil.x, proyectil.y, 3);
            }
        }

        formaEnemigo.end();

        batch.setProjectionMatrix(hud.obtenerCamara().combined);

        hud.actualizar();
        hud.dibujar();

        if (EstadoRed.esperandoMejora) {

            mostrarMejorasRed();

        } else {

            mejoraSeleccionada = 0;
            ultimoEnvioMejora = 0;
        }

        if (gestorPartida.terminoLaPartida()) {

            panelResultado.dibujar(batch, fuentePausa, gestorPartida, jugador, segundoJugador, hud.obtenerCamara().viewportWidth, hud.obtenerCamara().viewportHeight);

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {

                reinicioSolicitado = true;
            }

            if (reinicioSolicitado && hiloCliente != null && System.currentTimeMillis() - ultimoEnvioReinicio >= 1000) {

                hiloCliente.enviarMensaje("reiniciar");

                ultimoEnvioReinicio = System.currentTimeMillis();
            }

        } else {

            reinicioSolicitado = false;
            ultimoEnvioReinicio = 0;
        }
    }

    private void enviarMovimientoServidor() {

        if (hiloCliente == null || !EstadoRed.conectado) {
            return;
        }

        long ahora = System.currentTimeMillis();

        if (ahora - ultimoEnvioMovimiento < 100) {
            return;
        }

        ultimoEnvioMovimiento = ahora;

        int direccionX = 0;
        int direccionY = 0;

        if (controladorEntrada.estaIzquierda()) {
            direccionX--;
        }

        if (controladorEntrada.estaDerecha()) {
            direccionX++;
        }

        if (controladorEntrada.estaAbajo()) {
            direccionY--;
        }

        if (controladorEntrada.estaArriba()) {
            direccionY++;
        }

        hiloCliente.enviarMensaje("mover-" + (direccionX + 1) + "-" + (direccionY + 1));
    }
    
    private void actualizarDesdeServidor() {

        jugador.establecerEstadoRed(EstadoRed.posicionX1, EstadoRed.posicionY1, EstadoRed.vida, EstadoRed.experiencia, EstadoRed.nivel, EstadoRed.experienciaNecesaria, EstadoRed.enemigosEliminados1);

        segundoJugador.establecerEstadoRed(EstadoRed.posicionX2, EstadoRed.posicionY2, EstadoRed.vida, EstadoRed.experiencia, EstadoRed.nivel, EstadoRed.experienciaNecesaria, EstadoRed.enemigosEliminados2);

        gestorPartida.actualizarDesdeRed(EstadoRed.tiempoEnJuego, EstadoRed.estadoPartida);

        Set<Integer> identificadores = new HashSet<>();

        ArrayList<Enemigo> listaEnemigos = new ArrayList<>();

        for (EstadoRed.EnemigoRed dato : EstadoRed.enemigos) {

            identificadores.add(dato.id);

            Enemigo enemigo = enemigosRed.get(dato.id);

            if (enemigo == null) {

                if (dato.tipo.equals("drone")) {

                    enemigo = new DroneBasico(dato.x, dato.y, 50, mapa);

                } else if (dato.tipo.equals("nano")) {

                    enemigo = new NanoBot(dato.x, dato.y, mapa);

                } else if (dato.tipo.equals("tanque")) {

                    enemigo = new RobotTanque(dato.x, dato.y, 20, mapa);

                } else if (dato.tipo.equals("jefe")) {

                    enemigo = new JefeMecanico(dato.x, dato.y, mapa);
                }

                if (enemigo != null) {
                    enemigosRed.put(dato.id, enemigo);
                }
            }

            if (enemigo != null) {

                enemigo.establecerEstadoRed(dato.x, dato.y, dato.vida);

                listaEnemigos.add(enemigo);
            }
        }

        Iterator<Integer> iterador = enemigosRed.keySet().iterator();

        while (iterador.hasNext()) {

            if (!identificadores.contains(iterador.next())) {
                iterador.remove();
            }
        }

        enemigos.clear();
        enemigos.addAll(listaEnemigos);

        chipsEnergia.clear();

        for (EstadoRed.ChipRed chip : EstadoRed.chips) {

            chipsEnergia.add(new ChipEnergia(chip.x, chip.y));
        }
    }
    
    private void mostrarMejorasRed() {

        batch.setProjectionMatrix(hud.obtenerCamara().combined);

        batch.begin();

        fuentePausa.getData().setScale(0.8f);
        fuentePausa.setColor(Color.YELLOW);

        fuentePausa.draw( batch,"ELEGÍ UNA MEJORA", 120, 150);

        fuentePausa.setColor(Color.WHITE);
        fuentePausa.getData().setScale(0.6f);

        for (int i = 0; i < 3; i++) {

            String descripcion = EstadoRed.opcionesMejora[i];

            if (descripcion.equals("LASER")) {
                descripcion = "Mejorar Laser";
            }

            if (descripcion.equals("GRANADA")) {
                descripcion = "Mejorar Granada";
            }

            if (descripcion.equals("VIDA")) {
                descripcion = "Recuperar 30 puntos de vida";
            }

            if (descripcion.equals("VELOCIDAD")) {
                descripcion = "Aumentar velocidad";
            }

            fuentePausa.draw(batch, (i + 1) + " - " + descripcion, 95, 115 - i * 25);
        }

        if (mejoraSeleccionada > 0) {

            fuentePausa.draw(batch, "Esperando confirmacion del servidor...", 75, 25);
        }

        batch.end();

        if (mejoraSeleccionada == 0) {

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_1)) {
                mejoraSeleccionada = 1;
            }

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_2)) {
                mejoraSeleccionada = 2;
            }

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_3)) {
                mejoraSeleccionada = 3;
            }
        }

        if (mejoraSeleccionada > 0 && hiloCliente != null && System.currentTimeMillis() - ultimoEnvioMejora >= 1000) {

            hiloCliente.enviarMensaje("mejora-" + mejoraSeleccionada);

            ultimoEnvioMejora = System.currentTimeMillis();
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

    	if (hiloCliente != null) {
    	    hiloCliente.cerrar();
    	}

    	EstadoRed.reiniciar();
    	
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