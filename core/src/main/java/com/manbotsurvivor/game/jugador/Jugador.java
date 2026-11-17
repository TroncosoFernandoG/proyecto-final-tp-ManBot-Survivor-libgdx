package com.manbotsurvivor.game.jugador;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

// Jugador del CLIENTE: solo se dibuja.
// La posicion, la vida y la experiencia las calcula el servidor.
public class Jugador {

	// Cuanto mas alto, mas rapido alcanza la posicion que mando el servidor
	private static final float VELOCIDAD_SUAVIZADO = 15f;
	// Si la diferencia es muy grande (por ejemplo, una reconexion) se ubica directo
	private static final float DISTANCIA_MAXIMA_SUAVIZADO = 150f;

	private float posicionX;
	private float posicionY;
	private float ancho;
	private float alto;
	private boolean posicionInicial;

	private Texture texturaJugador;
	private Animation<TextureRegion> animacionJugador;
	private float tiempoAnimacion;
	private boolean mirandoDerecha;
	private boolean moviendose;

	public Jugador() {

	    this.ancho = 32;
	    this.alto = 32;
	    this.posicionInicial = true;

	    texturaJugador = new Texture("manbot.png");

	    TextureRegion[][] cuadros = TextureRegion.split(texturaJugador, 64, 64);

	    TextureRegion[] cuadrosAnimacion = new TextureRegion[5];

	    for (int i = 0; i < cuadrosAnimacion.length; i++) {
	        cuadrosAnimacion[i] = cuadros[0][i];
	    }

	    animacionJugador = new Animation<TextureRegion>(0.15f, cuadrosAnimacion);

	    animacionJugador.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

	    tiempoAnimacion = 0;
	    mirandoDerecha = true;
	}

	// Los estados llegan cada 50 ms (o con retardo): en vez de saltar a la posicion nueva,
	// el jugador se acerca de a poco y el movimiento se ve fluido.
	public void asignarPosicion(float x, float y, float delta) {

	    float diferenciaX = x - posicionX;
	    float diferenciaY = y - posicionY;

	    boolean muyLejos = diferenciaX * diferenciaX + diferenciaY * diferenciaY
	            > DISTANCIA_MAXIMA_SUAVIZADO * DISTANCIA_MAXIMA_SUAVIZADO;

	    if (posicionInicial || muyLejos) {
	        posicionX = x;
	        posicionY = y;
	        posicionInicial = false;
	        return;
	    }

	    float factor = Math.min(1f, delta * VELOCIDAD_SUAVIZADO);

	    posicionX += diferenciaX * factor;
	    posicionY += diferenciaY * factor;
	}

	public void setMirandoDerecha(boolean mirandoDerecha) {
	    this.mirandoDerecha = mirandoDerecha;
	}

	public void setMoviendose(boolean moviendose) {
	    this.moviendose = moviendose;
	}

	public void animar(float delta) {
	    if (moviendose) {
	        tiempoAnimacion += delta;
	    } else {
	        tiempoAnimacion = 0;
	    }
	}

	public void reiniciar() {
	    posicionInicial = true;
	    tiempoAnimacion = 0;
	    moviendose = false;
	}

	public float obtenerPosicionX() {
	    return posicionX;
	}

	public float obtenerPosicionY() {
	    return posicionY;
	}

	public void dibujar(OrthographicCamera camaraJuego, SpriteBatch lote) {

	    TextureRegion cuadroActual = animacionJugador.getKeyFrame(tiempoAnimacion);

	    if (mirandoDerecha) {
	        lote.draw(cuadroActual, posicionX, posicionY, ancho, alto);
	    } else {
	        lote.draw(cuadroActual, posicionX + ancho, posicionY, -ancho, alto); // espejado
	    }
	}

	public void disponer() {
	    texturaJugador.dispose();
	}
}
