package elementos;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Jugador {
	
	private float posicionX;
	private float posicionY;
	private float velocidad;
	private float ancho;
	private float alto;
	private ShapeRenderer formaJugador;
	private ControladorEntrada controladorEntrada;
	
	public Jugador(float posicionX, float posicionY, float velocidad, ControladorEntrada controladorEntrada) {

	    this.posicionX = posicionX;
	    this.posicionY = posicionY;
	    this.velocidad = velocidad;
	    this.controladorEntrada = controladorEntrada;
	    this.ancho = 32;
	    this.alto = 32;
	    formaJugador = new ShapeRenderer();
	    
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
	    mover(movimientoX, movimientoY);
	}
	
	public void dibujar(OrthographicCamera camaraJuego) {
		formaJugador.setProjectionMatrix(camaraJuego.combined);
	    formaJugador.begin(ShapeRenderer.ShapeType.Filled);
	    formaJugador.setColor(1, 1, 1, 1);
	    formaJugador.rect(posicionX, posicionY, ancho, alto);
	    formaJugador.end();
	}
	
	public void disponer() {
	    formaJugador.dispose();
	}
	
}
