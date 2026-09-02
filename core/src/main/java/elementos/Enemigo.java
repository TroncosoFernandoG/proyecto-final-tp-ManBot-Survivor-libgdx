package elementos;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Enemigo {

	    private float posicionX;
	    private float posicionY;

	    private float velocidad;

	    private float ancho;
	    private float alto;
	    
	    private Mapa mapa;

	    public Enemigo(float posicionX, float posicionY, float velocidad, Mapa mapa) {

	        this.posicionX = posicionX;
	        this.posicionY = posicionY;
	        this.velocidad = velocidad;
	        this.ancho = 32;
	        this.alto = 32;
	        this.mapa = mapa;
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
	    }
	    
	    public void perseguir(Jugador jugador, float delta) {

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

	        mover(movimientoX, movimientoY);
	    }
	    
	    public void dibujar(ShapeRenderer forma) {

	        forma.begin(ShapeRenderer.ShapeType.Filled);

	        forma.rect(posicionX, posicionY, ancho, alto);

	        forma.end();
	    }
	    
	    public void actualizar(float delta, Jugador jugador) {

	        perseguir(jugador, delta);
	    }
}

