package pantallas;

	import com.badlogic.gdx.Gdx;
	import com.badlogic.gdx.Input;
	import com.badlogic.gdx.Screen;
	import com.badlogic.gdx.graphics.GL20;
	import com.badlogic.gdx.graphics.OrthographicCamera;
	import com.badlogic.gdx.graphics.g2d.BitmapFont;
	import com.badlogic.gdx.utils.viewport.FitViewport;

	import com.manbotsurvivor.game.ManBotSurvivor;

	public class PantallaMenu implements Screen {

	    private final ManBotSurvivor juego;

	    private final OrthographicCamera camara;
	    private final FitViewport vista;

	    private final BitmapFont fuente;

	    public PantallaMenu(ManBotSurvivor juego) {
	    	this.juego = juego;
	        camara = new OrthographicCamera();
	        vista = new FitViewport(ManBotSurvivor.V_WIDTH, ManBotSurvivor.V_HEIGHT, camara);
	        fuente = new BitmapFont();
	    }

	    @Override
	    public void show() {
	    }

	    @Override
	    public void render(float delta) {

	        Gdx.gl.glClearColor(0.03f, 0.04f, 0.05f, 1);

	        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

	            juego.comenzarPartida();

	            return;
	        }

	        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {

	            Gdx.app.exit();

	            return;
	        }

	        juego.batch.setProjectionMatrix(camara.combined);

	        juego.batch.begin();

	        fuente.draw(juego.batch, "MANBOT SURVIVOR", 135, 140);

	        fuente.draw(juego.batch,"SUPERVIVENCIA 2D", 145, 115);

	        fuente.draw(juego.batch, "ENTER - JUGAR", 155, 75);

	        fuente.draw(juego.batch, "ESC - SALIR", 160, 50);

	        juego.batch.end();
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
	    }
	}