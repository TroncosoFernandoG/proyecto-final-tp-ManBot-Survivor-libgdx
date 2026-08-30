package elementos;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Jugador {
	
	private float posicionX;
	private float posicionY;
	private float velocidad;
	private float ancho;
	private float alto;
	private Texture texturaJugador;
	private Animation<TextureRegion> animacionJugador;
	private float tiempoAnimacion;
	private ControladorEntrada controladorEntrada;
	
	public Jugador(float posicionX, float posicionY, float velocidad, ControladorEntrada controladorEntrada) {

	    this.posicionX = posicionX;
	    this.posicionY = posicionY;
	    this.velocidad = velocidad;
	    this.controladorEntrada = controladorEntrada;
	    this.ancho = 32;
	    this.alto = 32;
	    
	    texturaJugador = new Texture("manbot.png");

	    TextureRegion[][] cuadros = TextureRegion.split(
	            texturaJugador,
	            64,
	            64
	    );

	    TextureRegion[] cuadrosAnimacion = new TextureRegion[5];

	    for (int i = 0; i < 5; i++) {
	        cuadrosAnimacion[i] = cuadros[0][i];
	    }

	    animacionJugador = new Animation<TextureRegion>(
	            0.15f,
	            cuadrosAnimacion
	    );

	    animacionJugador.setPlayMode(Animation.PlayMode.LOOP);

	    tiempoAnimacion = 0;
	    
	}
	
	public float obtenerPosicionX() {
	    return posicionX;
	}

	public float obtenerPosicionY() {
	    return posicionY;
	}
	
	public void mover(float movimientoX, float movimientoY) {

	    posicionX += movimientoX;
	    posicionY += movimientoY;

	    if (posicionX < 0) {
	        posicionX = 0;
	    }
	    if (posicionY < 0) {
	        posicionY = 0;
	    }
	    if (posicionX + ancho > com.manbotsurvivor.game.ManBotSurvivor.V_WIDTH) {
	        posicionX = com.manbotsurvivor.game.ManBotSurvivor.V_WIDTH - ancho;
	    }
	    if (posicionY + alto > com.manbotsurvivor.game.ManBotSurvivor.V_HEIGHT) {
	        posicionY = com.manbotsurvivor.game.ManBotSurvivor.V_HEIGHT - alto;
	    }
	}
	
	public void actualizar(float delta) {

	    float movimientoX = 0;
	    float movimientoY = 0;

	    if (controladorEntrada.estaArriba()) {
	        movimientoY += velocidad * delta;
	    }
	    if (controladorEntrada.estaAbajo()) {
	        movimientoY -= velocidad * delta;
	    }
	    if (controladorEntrada.estaIzquierda()) {
	        movimientoX -= velocidad * delta;
	    }
	    if (controladorEntrada.estaDerecha()) {
	        movimientoX += velocidad * delta;
	    }
	    
	    tiempoAnimacion += delta;
	    
	    mover(movimientoX, movimientoY);
	}
	
	public void dibujar(OrthographicCamera camaraJuego, SpriteBatch lote) {

	    lote.setProjectionMatrix(camaraJuego.combined);

	    TextureRegion cuadroActual = animacionJugador.getKeyFrame(tiempoAnimacion);

	    lote.draw(
	        cuadroActual,
	        posicionX,
	        posicionY,
	        ancho,
	        alto
	    );
	}
	
	public void disponer() {
	    texturaJugador.dispose();
	}
	
}
