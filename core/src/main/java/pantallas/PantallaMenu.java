package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.FitViewport;

import com.manbotsurvivor.game.ManBotSurvivor;

public class PantallaMenu implements Screen {

    private final ManBotSurvivor JUEGO;

    private final OrthographicCamera CAMARA;
    private final FitViewport VISTA;

    private final BitmapFont FUENTE;

    private Texture fondo;

    private int opcionSeleccionada;

    private final String[] OPCIONES = {
        "NUEVA PARTIDA",
        "OPCIONES",
        "SALIR"
    };

    public PantallaMenu(ManBotSurvivor juego) {

        this.JUEGO = juego;

        CAMARA = new OrthographicCamera();

        VISTA = new FitViewport(ManBotSurvivor.V_WIDTH, ManBotSurvivor.V_HEIGHT, CAMARA);

        FUENTE = new BitmapFont();

        fondo = new Texture("fondo_menu.png");

        opcionSeleccionada = 0;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        CAMARA.update();

        JUEGO.batch.setProjectionMatrix(CAMARA.combined);

        JUEGO.batch.begin();

        JUEGO.batch.draw(fondo, 0, 0, ManBotSurvivor.V_WIDTH,ManBotSurvivor.V_HEIGHT);

        FUENTE.getData().setScale(1.2f);

        for (int i = 0; i < OPCIONES.length; i++) {

            float posicionY = 75 - (i * 30);

            String texto = OPCIONES[i];

            if (i == opcionSeleccionada) {
                texto = "> " + texto + " <";
            }

            FUENTE.draw(JUEGO.batch, texto, 165, posicionY);
        }

        JUEGO.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {

            opcionSeleccionada--;

            if (opcionSeleccionada < 0) {
            		opcionSeleccionada = OPCIONES.length - 1;
            }
        }

        
        if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {

            opcionSeleccionada++;

            if (opcionSeleccionada >= OPCIONES.length) {
                opcionSeleccionada = 0;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

            seleccionarOpcion();
        }
    }

    private void seleccionarOpcion() {

        switch (opcionSeleccionada) {

        case 0:
            JUEGO.comenzarPartida();
            break;

        case 1:
        	JUEGO.setScreen(new PantallaOpciones(JUEGO));
            break;

        case 2:
            Gdx.app.exit();
            break;
        }
    }

    @Override
    public void resize(int ancho, int alto) {

        VISTA.update(ancho, alto, true);
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

        FUENTE.dispose();
        fondo.dispose();
    }
}

