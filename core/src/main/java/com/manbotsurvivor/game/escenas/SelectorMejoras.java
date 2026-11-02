package com.manbotsurvivor.game.escenas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.manbotsurvivor.game.elementos.GestorProgresion;
import com.manbotsurvivor.game.elementos.Mejora;

public class SelectorMejoras {
	
	 public void dibujar(SpriteBatch batch, BitmapFont fuente, GestorProgresion progresion, float ancho, float alto) {

	        batch.begin();

	        fuente.setColor(Color.YELLOW);
	        fuente.draw( batch, "SUBISTE DE NIVEL", ancho * 0.35f, alto * 0.68f);

	        fuente.setColor(Color.WHITE);
	        fuente.draw( batch, "Elegí una mejora:", ancho * 0.35f, alto * 0.61f);

	        for (int i = 0; i < progresion.obtenerOpciones().size();i++) {

	            Mejora mejora = progresion.obtenerOpciones().get(i);

	            fuente.draw(batch, (i + 1) + " - " + mejora.obtenerDescripcion(), ancho * 0.25f, alto * (0.53f - i * 0.07f));
	        }

	        fuente.setColor(Color.WHITE);
	        batch.end();
	    }
	 
}
