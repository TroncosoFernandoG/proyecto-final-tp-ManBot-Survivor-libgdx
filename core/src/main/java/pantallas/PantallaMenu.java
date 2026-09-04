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

    private final ManBotSurvivor juego;

    private final OrthographicCamera camara;
    private final FitViewport vista;

    private final BitmapFont fuente;

    private Texture fondo;

    private int opcionSeleccionada;

    private final String[] opciones = {
        "NUEVA PARTIDA",
        "OPCIONES",
        "SALIR"
    };

    public PantallaMenu(ManBotSurvivor juego) {

        this.juego = juego;

        camara = new OrthographicCamera();

        vista = new FitViewport(ManBotSurvivor.V_WIDTH, ManBotSurvivor.V_HEIGHT, camara);

        fuente = new BitmapFont();

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

        camara.update();

        juego.batch.setProjectionMatrix(camara.combined);

        juego.batch.begin();

        juego.batch.draw(fondo, 0, 0, ManBotSurvivor.V_WIDTH,ManBotSurvivor.V_HEIGHT);

        fuente.getData().setScale(1.2f);

        for (int i = 0; i < opciones.length; i++) {

            float posicionY = 75 - (i * 30);

            String texto = opciones[i];

            if (i == opcionSeleccionada) {
                texto = "> " + texto + " <";
            }

            fuente.draw(juego.batch, texto, 165, posicionY);
        }

        juego.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {

            opcionSeleccionada--;

            if (opcionSeleccionada < 0) {
            		opcionSeleccionada = opciones.length - 1;
            }
        }

        
        if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {

            opcionSeleccionada++;

            if (opcionSeleccionada >= opciones.length) {
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
            juego.comenzarPartida();
            break;

        case 1:
        	juego.setScreen(new PantallaOpciones(juego));
            break;

        case 2:
            Gdx.app.exit();
            break;
        }
    }

    @Override
    public void resize(int ancho, int alto) {

        vista.update(ancho, alto, true);
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

        fuente.dispose();
        fondo.dispose();
    }
}

