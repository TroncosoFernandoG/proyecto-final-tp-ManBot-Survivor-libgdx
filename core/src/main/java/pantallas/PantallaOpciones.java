package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.manbotsurvivor.game.NavegacionPantallas;
import elementos.ControladorEntrada;
import elementos.GestorAudio;

public class PantallaOpciones implements Screen{
	
		private final NavegacionPantallas navegacion;
		private final GestorAudio gestorAudio;
	    private OrthographicCamera camara;
	    private SpriteBatch lote;
	    private BitmapFont fuente;
	    private ControladorEntrada controladorEntrada;

	    public PantallaOpciones(GestorAudio gestorAudio, NavegacionPantallas navegacion, int anchoPantalla, int altoPantalla) {

	    	this.gestorAudio = gestorAudio;
	        this.navegacion = navegacion;
	        
	        controladorEntrada = new ControladorEntrada();

	        camara = new OrthographicCamera();
	        camara.setToOrtho(false, anchoPantalla, altoPantalla);

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
	        	navegacion.mostrarMenu();
	            return;
	        }
	        
	        if (controladorEntrada.consumirPulsacionArriba()) {
	            gestorAudio.subirVolumen();
	        }

	        if (controladorEntrada.consumirPulsacionAbajo()) {
	            gestorAudio.bajarVolumen();
	        }

	        if (controladorEntrada.consumirPulsacionM()) {
	            gestorAudio.alternarSilencio();
	        }

	        lote.setProjectionMatrix(camara.combined);

	        lote.begin();

	        fuente.getData().setScale(2);
	        fuente.draw(lote, "OPCIONES", 125, 170);

	        fuente.getData().setScale(1.2f);
	        fuente.draw(lote, "VOLUMEN", 160, 115);

	        int porcentaje = (int)(gestorAudio.obtenerVolumen() * 100);

	        int cantidadBloques = porcentaje / 10;

	        String barra = "[";

	        for (int i = 0; i < 10; i++) {

	            if (i < cantidadBloques) {
	                barra += "#";
	            } else {
	                barra += "-";
	            }
	        }

	        barra += "]";

	        fuente.getData().setScale(1.0f);
	        fuente.draw(lote, barra, 150, 90);

	        fuente.draw(lote, porcentaje + "%", 185, 70);

	        if (gestorAudio.estaSilenciado()) {
	            fuente.draw(lote, "SILENCIADO", 165, 50);
	        }

	        fuente.getData().setScale(0.8f);
	        fuente.draw(lote, "W - Subir volumen", 120, 35);
	        fuente.draw(lote, "S - Bajar volumen", 120, 22);
	        fuente.draw(lote, "M - Silenciar / Activar", 105, 9);

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
