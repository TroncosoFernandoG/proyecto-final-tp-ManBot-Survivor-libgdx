package elementos;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Enemigo {

	    private float posicionX;
	    private float posicionY;

	    private float velocidad;

	    private float ancho;
	    private float alto;
	    private int vida;
	    private Rectangle hitbox;
	    private final int daño;
	    private float tiempoDesdeUltimoDaño;
	    private final float tiempoEntreDaños;
	    
	    private Texture texturaEnemigo;
	    private Animation<TextureRegion> animacionEnemigo;
	    private float tiempoAnimacion;
	    
	    private Mapa mapa;

	    public Enemigo(float posicionX, float posicionY, float velocidad, Mapa mapa) {

	        this.posicionX = posicionX;
	        this.posicionY = posicionY;
	        this.velocidad = velocidad;
	        this.ancho = 32;
	        this.alto = 32;
	        this.vida = 3;
	        this.mapa = mapa;
	        this.hitbox = new Rectangle(posicionX, posicionY, ancho, alto);
	        this.daño = 10;
	        this.tiempoDesdeUltimoDaño = 1;
	        this.tiempoEntreDaños = 1;
	        
	        texturaEnemigo = new Texture("dronebasico.png");

	        TextureRegion[][] cuadros = TextureRegion.split(texturaEnemigo, 32, 32);

	        TextureRegion[] cuadrosAnimacion = new TextureRegion[4];

	        for (int i = 0; i < cuadrosAnimacion.length; i++) {
	            cuadrosAnimacion[i] = cuadros[0][i];
	        }

	        animacionEnemigo = new Animation<TextureRegion>(0.15f, cuadrosAnimacion);

	        animacionEnemigo.setPlayMode(Animation.PlayMode.LOOP);

	        tiempoAnimacion = 0;
	    }
	    
	    public float obtenerPosicionX() {
	        return posicionX;
	    }

	    public float obtenerPosicionY() {
	        return posicionY;
	    }
	    
	    public float obtenerAncho() {
		    return ancho;
		}

		public float obtenerAlto() {
		    return alto;
		}
		
		public Rectangle obtenerHitbox() {
		    return hitbox;
		}

		public int obtenerDaño() {
		    return daño;
		}

	    public void recibirDaño(int daño) {
	    	vida-=daño;
	    	
	    	if(vida<0) {
	    		vida=0;
	    	}
	    }
	    
	    public boolean estaVivo() {
	    	return vida>0;
	    }
	    
	    public void intentarHacerDaño(Jugador jugador, float delta) {

	        tiempoDesdeUltimoDaño += delta;

	        Rectangle hitboxJugador = new Rectangle(jugador.obtenerPosicionX(), jugador.obtenerPosicionY(), jugador.obtenerAncho(), jugador.obtenerAlto());

	        if (hitbox.overlaps(hitboxJugador)
	                && tiempoDesdeUltimoDaño >= tiempoEntreDaños) {

	            jugador.recibirDaño(daño);
	            tiempoDesdeUltimoDaño = 0;
	        }
	    }
	    
	    public void mover(float movimientoX, float movimientoY, Jugador jugador, ArrayList<Enemigo> enemigos) {

	    	float nuevaPosicionX = posicionX + movimientoX;
	    	float nuevaPosicionY = posicionY + movimientoY;

	    	Rectangle futuraHitboxX = new Rectangle(nuevaPosicionX, posicionY, ancho, alto);

	    	if (!mapa.hayColision(nuevaPosicionX, posicionY, ancho, alto) && !hayColisionConJugador(nuevaPosicionX, posicionY, jugador) 
	    			&& !hayColisionConOtroEnemigo(futuraHitboxX, enemigos)) {

	    	    posicionX = nuevaPosicionX;
	    	}

	    	Rectangle futuraHitboxY = new Rectangle(posicionX,nuevaPosicionY,ancho,alto);

	    	if (!mapa.hayColision(posicionX, nuevaPosicionY, ancho, alto) && !hayColisionConJugador(posicionX, nuevaPosicionY, jugador)
	    	        && !hayColisionConOtroEnemigo(futuraHitboxY, enemigos)) {

	    	    posicionY = nuevaPosicionY;
	    	}

	    	hitbox.setPosition(posicionX, posicionY);
	    }
	    
	    public void perseguir(Jugador jugador, float delta, ArrayList<Enemigo> enemigos) {

	        float diferenciaX = jugador.obtenerPosicionX() - posicionX;
	        float diferenciaY = jugador.obtenerPosicionY() - posicionY;

	        float movimientoX = 0;
	        float movimientoY = 0;

	        if (diferenciaX > 0) {
	            movimientoX = velocidad * delta;
	        }

	        if (diferenciaX < 0) {
	            movimientoX = -velocidad * delta;
	        }

	        if (diferenciaY > 0) {
	            movimientoY = velocidad * delta;
	        }

	        if (diferenciaY < 0) {
	            movimientoY = -velocidad * delta;
	        }

	        mover(movimientoX, movimientoY, jugador, enemigos);
	    }
	    
	    private boolean hayColisionConJugador(float nuevaPosicionX, float nuevaPosicionY, Jugador jugador) {
	        Rectangle areaEnemigo = new Rectangle( nuevaPosicionX, nuevaPosicionY, ancho, alto);
	        Rectangle areaJugador = new Rectangle( jugador.obtenerPosicionX(), jugador.obtenerPosicionY(), jugador.obtenerAncho(), jugador.obtenerAlto());
	        return areaEnemigo.overlaps(areaJugador);
	    }
	    
	    private boolean hayColisionConOtroEnemigo(
	    	Rectangle futuraHitbox,
	    	ArrayList<Enemigo> enemigos) {

	        for (Enemigo enemigo : enemigos) {

	            if (enemigo == this) {
	                continue;
	            }

	            if (futuraHitbox.overlaps(enemigo.obtenerHitbox())) {
	                return true;
	            }
	        }

	        return false;
	    }
	    
	    public void dibujar(OrthographicCamera camaraJuego, SpriteBatch lote) {

	    	lote.setProjectionMatrix(camaraJuego.combined);

	        TextureRegion cuadroActual = animacionEnemigo.getKeyFrame(tiempoAnimacion);

	        lote.draw(cuadroActual, posicionX, posicionY, ancho, alto);
	    }
	    
	    public void actualizar(float delta, Jugador jugador, ArrayList<Enemigo> enemigos) {

	        perseguir(jugador, delta, enemigos);
	        intentarHacerDaño(jugador, delta);
	        tiempoAnimacion += delta;
	    }
}

