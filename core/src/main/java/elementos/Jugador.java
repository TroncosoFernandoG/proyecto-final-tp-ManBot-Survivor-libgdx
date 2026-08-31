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
	
	private float limiteMapaAncho;
	private float limiteMapaAlto;
	
	private Texture texturaJugador;
	private Animation<TextureRegion> animacionJugador;
	private float tiempoAnimacion;
	private boolean mirandoDerecha;
	private ControladorEntrada controladorEntrada;
	private Mapa mapa;
	
	public Jugador(float posicionX, float posicionY, float velocidad, ControladorEntrada controladorEntrada, float limiteMapaAncho, float limiteMapaAlto, Mapa mapa) {

	    this.posicionX = posicionX;
	    this.posicionY = posicionY;
	    this.velocidad = velocidad;
	    this.controladorEntrada = controladorEntrada;
	    this.limiteMapaAncho = limiteMapaAncho;
	    this.limiteMapaAlto = limiteMapaAlto;
	    this.mapa = mapa;
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

	    animacionJugador.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

	    tiempoAnimacion = 0;
	    mirandoDerecha = true;
	    
	}
	
	public float obtenerPosicionX() {
	    return posicionX;
	}

	public float obtenerPosicionY() {
	    return posicionY;
	}
	
	public void mover(float movimientoX, float movimientoY) {

	    float nuevaPosicionX = posicionX + movimientoX;
	    float nuevaPosicionY = posicionY + movimientoY;

	    if (!mapa.hayColision(nuevaPosicionX, posicionY, ancho, alto)) {
	        posicionX = nuevaPosicionX;
	    }

	    if (!mapa.hayColision(posicionX, nuevaPosicionY, ancho, alto)) {
	        posicionY = nuevaPosicionY;
	    }

	    if (posicionX < 0) {
	        posicionX = 0;
	    }

	    if (posicionY < 0) {
	        posicionY = 0;
	    }

	    if (posicionX + ancho > limiteMapaAncho) {
	        posicionX = limiteMapaAncho - ancho;
	    }

	    if (posicionY + alto > limiteMapaAlto) {
	        posicionY = limiteMapaAlto - alto;
	    }

	}
	
	public void actualizar(float delta) {

	    float movimientoX = 0;
	    float movimientoY = 0;

	    if (controladorEntrada.estaArriba() ||
	        controladorEntrada.consumirPulsacionArriba()) {

	        movimientoY += velocidad * delta;
	    }

	    if (controladorEntrada.estaAbajo() ||
	        controladorEntrada.consumirPulsacionAbajo()) {

	        movimientoY -= velocidad * delta;
	    }

	    if (controladorEntrada.estaIzquierda() ||
	        controladorEntrada.consumirPulsacionIzquierda()) {

	        movimientoX -= velocidad * delta;
	        mirandoDerecha = false;
	    }

	    if (controladorEntrada.estaDerecha() ||
	        controladorEntrada.consumirPulsacionDerecha()) {

	        movimientoX += velocidad * delta;
	        mirandoDerecha = true;
	    }

	    boolean estaMoviendose = movimientoX != 0 || movimientoY != 0;

	    if (estaMoviendose) {
	        tiempoAnimacion += delta;
	    } else {
	        tiempoAnimacion = 0;
	    }

	    mover(movimientoX, movimientoY);
	}
	
	public void dibujar(OrthographicCamera camaraJuego, SpriteBatch lote) {

	    lote.setProjectionMatrix(camaraJuego.combined);

	    TextureRegion cuadroActual = new TextureRegion(animacionJugador.getKeyFrame(tiempoAnimacion));

	    if (!mirandoDerecha) {
	        cuadroActual.flip(true, false);
	    }
	    
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
