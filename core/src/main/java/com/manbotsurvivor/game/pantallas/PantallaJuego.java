package com.manbotsurvivor.game.pantallas;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.manbotsurvivor.game.cliente.DatosEnemigo;
import com.manbotsurvivor.game.cliente.DatosJugador;
import com.manbotsurvivor.game.cliente.DatosPartida;
import com.manbotsurvivor.game.cliente.DatosSeleccion;
import com.manbotsurvivor.game.cliente.HiloCliente;
import com.manbotsurvivor.game.cliente.ProtocolosRed;
import com.manbotsurvivor.game.elementos.ControladorEntrada;
import com.manbotsurvivor.game.elementos.DibujoProyectiles;
import com.manbotsurvivor.game.elementos.EstadoPartida;
import com.manbotsurvivor.game.elementos.Mapa;
import com.manbotsurvivor.game.enemigos.Enemigo;
import com.manbotsurvivor.game.enemigos.TipoEnemigo;
import com.manbotsurvivor.game.escenas.Hud;
import com.manbotsurvivor.game.escenas.PanelResultado;
import com.manbotsurvivor.game.escenas.SelectorMejoras;
import com.manbotsurvivor.game.jugador.Jugador;

// Pantalla de juego del CLIENTE: se conecta al servidor, le manda las teclas
// y dibuja el estado de la partida que recibe. No ejecuta la logica del juego.
public class PantallaJuego implements Screen {
	private static final float ANCHO_MAPA = 100 * 32;
	private static final float ALTO_MAPA = 100 * 32;

	private static final long INTERVALO_REINTENTO = 1000;      // Buscar / conectar cada 1 segundo
	private static final long TIEMPO_ESPERA_SERVIDOR = 5000;   // Despues de 5 s avisamos que no hay servidor
	private static final long INTERVALO_COMPROBACION = 1000;   // estasAhi cada 1 segundo
	private static final long INTERVALO_TECLAS = 100;          // Reenvio de las teclas
	private static final long INTERVALO_REENVIO = 250;         // Reenvio de mejora / nueva partida
	private static final Color COLOR_SEGUNDO_JUGADOR = new Color(0.5f, 0.8f, 1f, 1f);

	private final SpriteBatch batch;
	private final NavegacionPantallas navegacion;
    private OrthographicCamera camaraJuego;
    private Viewport gamePort;
    private Hud hud;
    private Mapa mapa;
    private OrthogonalTiledMapRenderer renderizadorMapa;
    private ControladorEntrada controladorEntrada;
    private Jugador jugador;
    private Jugador segundoJugador;
    private Jugador jugadorLocal;
    private HashMap<Integer, Enemigo> enemigos;
    private Set<Integer> idsRecibidos;
    private DibujoProyectiles dibujoProyectiles;
    private ShapeRenderer formaEnemigo;
    private BitmapFont fuentePausa;
    private SelectorMejoras selectorMejoras;
    private PanelResultado panelResultado;

    private HiloCliente hiloCliente;

    private boolean pausado = false;
    private boolean servidorNoEncontrado = false;
    private long inicioIntentoConexion = 0;
    private long ultimoMensajeEnviado = 0;
    private long ultimoEnvioTeclas = 0;
    private long ultimoReenvio = 0;
    private String teclasAnteriores = "";

    private int idSeleccionEnviada = -1;
    private int opcionEnviada = 0;
    private boolean nuevaPartidaPedida = false;

    private int vidaAnterior = -1;
    private float tiempoEfectoDaño = 0;

    public PantallaJuego(SpriteBatch batch, NavegacionPantallas navegacion, int anchoVista, int altoVista) {

    	this.batch = batch;
        this.navegacion = navegacion;

        controladorEntrada = new ControladorEntrada();

        mapa = new Mapa();
        renderizadorMapa = new OrthogonalTiledMapRenderer(mapa.obtenerMapa());

        camaraJuego = new OrthographicCamera();
        gamePort = new FitViewport(anchoVista, altoVista, camaraJuego);
        camaraJuego.zoom = 2.0f;

        jugador = new Jugador();
        segundoJugador = new Jugador();

        enemigos = new HashMap<>();
        idsRecibidos = new HashSet<>();
        dibujoProyectiles = new DibujoProyectiles();
        formaEnemigo = new ShapeRenderer();
        fuentePausa = new BitmapFont();
        selectorMejoras = new SelectorMejoras();
        panelResultado = new PanelResultado();

        crearHiloCliente();

        hud = new Hud(batch, hiloCliente);
    }

    private void crearHiloCliente() {
        hiloCliente = new HiloCliente();
        hiloCliente.start();

        inicioIntentoConexion = System.currentTimeMillis();
        ultimoMensajeEnviado = 0;
        servidorNoEncontrado = false;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(controladorEntrada);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        actualizarComunicacionServidor();

        // Alguien eligio "volver al menu" en la pantalla final
        if (hiloCliente.isFinSesion()) {
            navegacion.mostrarMenu();
            return;
        }

        if (!hiloCliente.isHayEspacio()) {
            mostrarMensajeConexion(Color.RED, "SERVIDOR COMPLETO", "Ya hay 2 jugadores conectados", "ESC - Volver al menu");
            return;
        }

        if (hiloCliente.isServidorDesconectado()) {
            if (controladorEntrada.consumirPulsacionEnter()) {
                reconectar();
                return;
            }
            mostrarMensajeConexion(Color.RED, "SERVIDOR DESCONECTADO", "ENTER - Volver a buscar el servidor", "ESC - Volver al menu");
            return;
        }

        if (!hiloCliente.isServidorConfirmado()) {
            if (servidorNoEncontrado) {
                mostrarMensajeConexion(Color.RED, "NO HAY NINGUN SERVIDOR CONECTADO", "Seguimos buscando en la red local...", "ESC - Volver al menu");
            } else {
                mostrarMensajeConexion(Color.YELLOW, "BUSCANDO SERVIDOR", "Buscando en la red local (broadcast)...", "ESC - Volver al menu");
            }
            return;
        }

        if (hiloCliente.consumirNuevaPartida()) {
            reiniciarPartida();
        }

        if (!hiloCliente.isEmpieza() || hiloCliente.getDatosPartida() == null) {
            int cuentaRegresiva = hiloCliente.getCuentaRegresiva();

            mostrarMensajeConexion(Color.YELLOW,
                    cuentaRegresiva > 0 ? "LA PARTIDA EMPIEZA EN " + cuentaRegresiva : "ESPERANDO JUGADOR",
                    "Conectado como Jugador " + hiloCliente.getNumeroJugador(),
                    "ESC - Volver al menu");
            return;
        }

        if (jugadorLocal == null) {
            asignarJugadores();
        }

        if (procesarEntradas()) {
            return; // Se cambio de pantalla
        }

        actualizarEntidades(delta);
        dibujarJuego();
    }

    // =====================================================================
    // Comunicacion con el servidor
    // =====================================================================

    private void actualizarComunicacionServidor() {

        long ahora = System.currentTimeMillis();

        if (hiloCliente.isServidorDesconectado() || !hiloCliente.isHayEspacio()) {
            return;
        }

        if (!hiloCliente.isServidorConfirmado()) {
            reintentarConexion(ahora);
            comprobarTiempoEspera(ahora);
            return;
        }

        enviarMensajeComprobacion(ahora);

        if (hiloCliente.isEmpieza()) {
            enviarTeclas(ahora);
            reenviarMensajesPendientes(ahora);
        }
    }

    private void reintentarConexion(long ahora) {

        if (ahora - ultimoMensajeEnviado >= INTERVALO_REINTENTO) {

            if (hiloCliente.isServidorEncontrado()) {
                hiloCliente.enviarMensaje(ProtocolosRed.CONEXION);
            } else {
                hiloCliente.buscarServidor();
            }

            ultimoMensajeEnviado = ahora;
        }
    }

    private void comprobarTiempoEspera(long ahora) {

        if (!servidorNoEncontrado && ahora - inicioIntentoConexion >= TIEMPO_ESPERA_SERVIDOR) {
            servidorNoEncontrado = true;
            System.out.println("No se encontró ningún servidor después de 5 segundos. Seguimos buscando...");
        }
    }

    private void enviarMensajeComprobacion(long ahora) {

        if (ahora - ultimoMensajeEnviado >= INTERVALO_COMPROBACION) {
            hiloCliente.enviarMensaje(ProtocolosRed.ESTAS_AHI);
            ultimoMensajeEnviado = ahora;
        }
    }

    // Mandamos el estado de las 4 teclas cuando cambia y ademas cada 100 ms:
    // si un paquete UDP se pierde, el siguiente corrige el movimiento.
    private void enviarTeclas(long ahora) {

        DatosPartida partida = hiloCliente.getDatosPartida();

        boolean puedeMoverse = !pausado && partida != null && partida.getEstado() == EstadoPartida.EN_CURSO;

        String teclas = ProtocolosRed.TECLAS
                + ProtocolosRed.SEPARADOR + (puedeMoverse && controladorEntrada.estaArriba() ? 1 : 0)
                + ProtocolosRed.SEPARADOR + (puedeMoverse && controladorEntrada.estaAbajo() ? 1 : 0)
                + ProtocolosRed.SEPARADOR + (puedeMoverse && controladorEntrada.estaIzquierda() ? 1 : 0)
                + ProtocolosRed.SEPARADOR + (puedeMoverse && controladorEntrada.estaDerecha() ? 1 : 0);

        if (!teclas.equals(teclasAnteriores) || ahora - ultimoEnvioTeclas >= INTERVALO_TECLAS) {
            hiloCliente.enviarMensaje(teclas);
            teclasAnteriores = teclas;
            ultimoEnvioTeclas = ahora;
        }
    }

    // UDP no garantiza la entrega: la mejora y el pedido de nueva partida se reenvian hasta que el servidor los procesa
    private void reenviarMensajesPendientes(long ahora) {

        if (ahora - ultimoReenvio < INTERVALO_REENVIO) {
            return;
        }

        ultimoReenvio = ahora;

        DatosSeleccion seleccion = hiloCliente.getDatosSeleccion(hiloCliente.getNumeroJugador());

        if (seleccion != null && seleccion.isActiva() && seleccion.getIdSeleccion() == idSeleccionEnviada) {
            enviarMejora();
        }

        DatosPartida partida = hiloCliente.getDatosPartida();

        if (nuevaPartidaPedida && partida != null && partida.terminoLaPartida()) {
            hiloCliente.enviarMensaje(ProtocolosRed.JUGAR_OTRA);
        }
    }

    private void enviarMejora() {
        hiloCliente.enviarMensaje(ProtocolosRed.MEJORA
                + ProtocolosRed.SEPARADOR + idSeleccionEnviada
                + ProtocolosRed.SEPARADOR + opcionEnviada);
    }

    private void reconectar() {
        hiloCliente.finalizarHilo();
        crearHiloCliente();

        hud.disponer();
        hud = new Hud(batch, hiloCliente);
        hud.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        reiniciarPartida();
    }

    // =====================================================================
    // Entradas
    // =====================================================================

    // Devuelve true si se cambio de pantalla
    private boolean procesarEntradas() {

        DatosPartida partida = hiloCliente.getDatosPartida();

        boolean apretoEscape = controladorEntrada.consumirPulsacionEscape();
        boolean apretoM = controladorEntrada.consumirPulsacionM();
        boolean apretoEnter = controladorEntrada.consumirPulsacionEnter();

        if (partida.terminoLaPartida()) {
            pausado = false;

            if (apretoEnter && !nuevaPartidaPedida) {
                nuevaPartidaPedida = true;
                hiloCliente.enviarMensaje(ProtocolosRed.JUGAR_OTRA);
            }

            if (apretoM) {
                hiloCliente.enviarMensaje(ProtocolosRed.VOLVER_MENU);
                navegacion.mostrarMenu();
                return true;
            }

            return false;
        }

        if (apretoEscape) {
            pausado = !pausado;
        }

        if (pausado && apretoM) {
            hiloCliente.enviarMensaje(ProtocolosRed.SE_SALIO_DEL_JUEGO);
            navegacion.mostrarMenu();
            return true;
        }

        DatosSeleccion seleccion = hiloCliente.getDatosSeleccion(hiloCliente.getNumeroJugador());
        int opcion = controladorEntrada.consumirSeleccionMejora();

        if (seleccion != null && seleccion.isActiva() && seleccion.getIdSeleccion() != idSeleccionEnviada
                && opcion >= 1 && opcion <= seleccion.getOpciones().size()) {
            idSeleccionEnviada = seleccion.getIdSeleccion();
            opcionEnviada = opcion;
            enviarMejora();
        }

        return false;
    }

    // =====================================================================
    // Sincronizacion de lo que se dibuja con lo que mando el servidor
    // =====================================================================

    public void asignarJugadores() {
        jugadorLocal = hiloCliente.getNumeroJugador() == 2 ? segundoJugador : jugador;
    }

    private void actualizarEntidades(float delta) {

        asignarPosicionJugador(jugador, hiloCliente.getDatosJugador(1), delta);
        asignarPosicionJugador(segundoJugador, hiloCliente.getDatosJugador(2), delta);

        List<DatosEnemigo> datosEnemigos = hiloCliente.getEnemigos();
        idsRecibidos.clear();

        for (DatosEnemigo datos : datosEnemigos) {

            idsRecibidos.add(datos.getId());

            Enemigo enemigo = enemigos.get(datos.getId());

            if (enemigo == null) {
                enemigo = TipoEnemigo.desdeCodigo(datos.getTipo()).crearEnemigo(datos.getX(), datos.getY());
                enemigos.put(datos.getId(), enemigo);
            }

            enemigo.asignarPosicion(datos.getX(), datos.getY(), delta);
            enemigo.animar(delta);
        }

        // Los que ya no vinieron en el mensaje murieron en el servidor
        enemigos.keySet().retainAll(idsRecibidos);

        dibujoProyectiles.animar(delta);

        DatosPartida partida = hiloCliente.getDatosPartida();

        if (vidaAnterior != -1 && partida.getVida() < vidaAnterior) {
            tiempoEfectoDaño = 0.2f;
        }

        vidaAnterior = partida.getVida();
        tiempoEfectoDaño = Math.max(0, tiempoEfectoDaño - delta);
    }

    public void asignarPosicionJugador(Jugador jugadorActual, DatosJugador datos, float delta) {
        if (datos != null) {
            jugadorActual.asignarPosicion(datos.getX(), datos.getY(), delta);
            jugadorActual.setMirandoDerecha(datos.isMirandoDerecha());
            jugadorActual.setMoviendose(datos.isMoviendose());
            jugadorActual.animar(delta);
        }
    }

    private void reiniciarPartida() {
        pausado = false;
        nuevaPartidaPedida = false;
        idSeleccionEnviada = -1;
        vidaAnterior = -1;
        tiempoEfectoDaño = 0;
        jugadorLocal = null;
        enemigos.clear();
        jugador.reiniciar();
        segundoJugador.reiniciar();
    }

    // =====================================================================
    // Dibujo
    // =====================================================================

    private void dibujarJuego() {

        DatosPartida partida = hiloCliente.getDatosPartida();

        // La camara sigue al jugador de esta computadora
        float mitadAnchoCamara = camaraJuego.viewportWidth * camaraJuego.zoom / 2;
        float mitadAltoCamara = camaraJuego.viewportHeight * camaraJuego.zoom / 2;

        float centroX = Math.max(mitadAnchoCamara, Math.min(ANCHO_MAPA - mitadAnchoCamara, jugadorLocal.obtenerPosicionX() + 16f));
        float centroY = Math.max(mitadAltoCamara, Math.min(ALTO_MAPA - mitadAltoCamara, jugadorLocal.obtenerPosicionY() + 16f));

        camaraJuego.position.set(centroX, centroY, 0);
        camaraJuego.update();

        gamePort.apply();
        renderizadorMapa.setView(camaraJuego);
        renderizadorMapa.render();

        batch.setProjectionMatrix(camaraJuego.combined);
        batch.begin();

        if (partida.getVida() > 0) {
            dibujarJugador(jugador, hiloCliente.getDatosJugador(1), Color.WHITE);
            dibujarJugador(segundoJugador, hiloCliente.getDatosJugador(2), COLOR_SEGUNDO_JUGADOR);
        }

        for (Enemigo enemigo : enemigos.values()) {
            enemigo.dibujar(camaraJuego, batch);
        }

        dibujoProyectiles.dibujarGranadas(batch, hiloCliente.getProyectiles());

        batch.end();

        formaEnemigo.setProjectionMatrix(camaraJuego.combined);
        formaEnemigo.begin(ShapeRenderer.ShapeType.Filled);

        dibujoProyectiles.dibujarDisparos(formaEnemigo, hiloCliente.getProyectiles());

        formaEnemigo.setColor(Color.BLUE);

        for (float[] chip : hiloCliente.getChips()) {
            formaEnemigo.rect(chip[0], chip[1], 16, 16);
        }

        formaEnemigo.end();

        dibujarInterfaz(partida);
    }

    private void dibujarJugador(Jugador jugadorActual, DatosJugador datos, Color color) {

        if (datos == null || !datos.isConectado()) {
            return;
        }

        batch.setColor(tiempoEfectoDaño > 0 ? Color.RED : color); // Rojo = recibieron daño
        jugadorActual.dibujar(camaraJuego, batch);
        batch.setColor(Color.WHITE);
    }

    private void dibujarInterfaz(DatosPartida partida) {

        OrthographicCamera camaraHud = hud.obtenerCamara();
        float ancho = camaraHud.viewportWidth;
        float alto = camaraHud.viewportHeight;

        batch.setProjectionMatrix(camaraHud.combined);
        fuentePausa.getData().setScale(0.7f);

        int numeroJugador = hiloCliente.getNumeroJugador();
        DatosSeleccion seleccion = hiloCliente.getDatosSeleccion(numeroJugador);

        boolean debeElegir = seleccion != null && seleccion.isActiva() && seleccion.getIdSeleccion() != idSeleccionEnviada;
        boolean esperandoCompañero = partida.getEstado() == EstadoPartida.PAUSADA && !debeElegir;

        if (partida.terminoLaPartida()) {
            panelResultado.dibujar(batch, fuentePausa, hiloCliente, ancho, alto);
            return;
        }

        if (debeElegir) {
            selectorMejoras.dibujar(batch, fuentePausa, seleccion, numeroJugador, ancho, alto);
        } else if (esperandoCompañero) {
            selectorMejoras.dibujarEspera(batch, fuentePausa, ancho, alto);
        } else {
            hud.actualizar();
            hud.dibujar();
        }

        batch.begin();

        if (partida.isHayJefe()) {
            fuentePausa.setColor(Color.ORANGE);
            fuentePausa.draw(batch, "JEFE MECANICO", ancho * 0.40f, 20);
        } else if (hiloCliente.isSeDesconectoUnJugador()) {
            fuentePausa.setColor(Color.ORANGE);
            fuentePausa.draw(batch, "Tu compañero se desconectó: seguís solo", ancho * 0.22f, 20);
        }

        fuentePausa.setColor(Color.WHITE);

        if (pausado) {
            fuentePausa.getData().setScale(1.2f);
            fuentePausa.draw(batch, "PAUSA", 176, 155);
            fuentePausa.getData().setScale(0.7f);
            fuentePausa.draw(batch, "(la partida sigue en el servidor)", 130, 130);
            fuentePausa.draw(batch, "ESC - Continuar", 170, 105);
            fuentePausa.draw(batch, "M - Salir al menu", 170, 85);
        }

        batch.end();
    }

    private void mostrarMensajeConexion(Color colorTitulo, String titulo, String detalle, String opciones) {

        if (controladorEntrada.consumirPulsacionEscape()) {
            navegacion.mostrarMenu();
            return;
        }

        batch.setProjectionMatrix(hud.obtenerCamara().combined);
        batch.begin();

        fuentePausa.getData().setScale(1.0f);
        fuentePausa.setColor(colorTitulo);
        fuentePausa.draw(batch, titulo, 40, 140);

        fuentePausa.getData().setScale(0.7f);
        fuentePausa.setColor(Color.WHITE);
        fuentePausa.draw(batch, detalle, 40, 110);
        fuentePausa.draw(batch, opciones, 40, 40);

        batch.end();
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

        if (hiloCliente != null) {
            // Avisamos que nos vamos (si el paquete se pierde, el servidor lo detecta por timeout)
            if (hiloCliente.isServidorConfirmado() && !hiloCliente.isServidorDesconectado()) {
                hiloCliente.enviarMensaje(ProtocolosRed.SE_SALIO_DEL_JUEGO);
            }
            hiloCliente.finalizarHilo();
        }
    }

    @Override
    public void dispose() {

        if (hiloCliente != null) {
            hiloCliente.finalizarHilo();
        }

        jugador.disponer();
        segundoJugador.disponer();
        dibujoProyectiles.disponer();

        TipoEnemigo.disponerTexturas();

        hud.disponer();
        fuentePausa.dispose();

        mapa.dispose();
        renderizadorMapa.dispose();
        formaEnemigo.dispose();
    }
}