package elementos;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ChipEnergia {

	 	private float posicionX;
	    private float posicionY;
	    private float ancho;
	    private float alto;
	    private int experiencia;
	    
	    public ChipEnergia(float posicionX, float posicionY) {

	        this.posicionX = posicionX;
	        this.posicionY = posicionY;
	        this.ancho = 16;
	        this.alto = 16;
	        this.experiencia = 10;
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
	    
	    public int obtenerExperiencia() {
	        return experiencia;
	    }
	    
	    public boolean hayColision(float posicionJugadorX, float posicionJugadorY, float anchoJugador, float altoJugador) {

	    	return posicionX < posicionJugadorX + anchoJugador &&
	    			posicionX + ancho > posicionJugadorX &&
	    			posicionY < posicionJugadorY + altoJugador &&
	    			posicionY + alto > posicionJugadorY;
	    	}
	    
	    public void dibujar(ShapeRenderer forma) {
	    	
	    	forma.begin(ShapeRenderer.ShapeType.Filled);
	    	
	        forma.setColor(Color.BLUE);
	        forma.rect( posicionX, posicionY, ancho, alto);
	        
	        forma.end();
	    }
}
