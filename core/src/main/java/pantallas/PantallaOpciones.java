package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.manbotsurvivor.game.ManBotSurvivor;
import elementos.ControladorEntrada;

public class PantallaOpciones implements Screen{
	
	 	private ManBotSurvivor game;
	    private OrthographicCamera camara;
	    private SpriteBatch lote;
	    private BitmapFont fuente;
	    private ControladorEntrada controladorEntrada;

	    public PantallaOpciones(ManBotSurvivor game) {

	        this.game = game;
	        controladorEntrada = new ControladorEntrada();

	        camara = new OrthographicCamera();
	        camara.setToOrtho(false, ManBotSurvivor.V_WIDTH, ManBotSurvivor.V_HEIGHT);

	        lote = new SpriteBatch();
	        fuente = new BitmapFont();
	    }

	    @Override
	    public void show() {
	    	Gdx.input.setInputProcessor(controladorEntrada);
	    }

	    @Override
	    public void render(float delta) {

	        Gdx.gl.glClearColor(0, 0, 0, 1);
	        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	        camara.update();
	        
	        if (controladorEntrada.consumirPulsacionEscape()) {
	            game.setScreen(new PantallaMenu(game));
	            return;
	        }

	        lote.setProjectionMatrix(camara.combined);

	        lote.begin();

	        fuente.getData().setScale(2);
	        fuente.draw(lote, "OPCIONES", 125, 170);
	        fuente.getData().setScale(1.2f);
	        fuente.draw(lote, "VOLUMEN", 160, 110);
	        fuente.getData().setScale(0.9f);
	        fuente.draw(lote, "ESC - para volver al menu principal", 20, 30);

	        lote.end();
	    }

	    @Override
	    public void resize(int width, int height) {

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

	        lote.dispose();
	        fuente.dispose();
	    }
	    
}
