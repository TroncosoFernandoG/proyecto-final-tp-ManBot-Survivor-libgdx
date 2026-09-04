package escenas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.manbotsurvivor.game.ManBotSurvivor;

import elementos.Jugador;

public class Hud {

	public Stage stage;
	private Viewport viewport;
	private BitmapFont fuente;
	private Jugador jugador;
	private float tiempo;
	
	Label vidaLabel;
	Label experienciaLabel;
	Label tiempoLabel;
	Label enemigosEliminadosLabel;
	
	public Hud(SpriteBatch sb, Jugador jugador) {
		
		this.jugador = jugador;
		
		viewport = new FitViewport(ManBotSurvivor.V_WIDTH, ManBotSurvivor.V_HEIGHT, new OrthographicCamera());
		stage = new Stage(viewport, sb);
		
		fuente = new BitmapFont();	
		this.tiempo = 0;
		
		Table table = new Table(); 
		table.top().left();
		table.setFillParent(true);
		
		vidaLabel = new Label("Vida: 100/100",  new Label.LabelStyle(fuente, Color.WHITE));
		experienciaLabel = new Label("XP: 0/100", new Label.LabelStyle(fuente, Color.WHITE));
		tiempoLabel = new Label("Tiempo: 00:00", new Label.LabelStyle(fuente, Color.WHITE));
		enemigosEliminadosLabel = new Label("Enemigos: 0", new Label.LabelStyle(fuente, Color.WHITE));
		
		table.add(vidaLabel).left().padTop(10).padLeft(10);
		table.row();
		table.add(experienciaLabel).left().padLeft(10);
		table.row();
		table.add(tiempoLabel).left().padLeft(10);
		table.row();
		table.add(enemigosEliminadosLabel).left().padLeft(10);
		
		stage.addActor(table);
		
	}
	
	public void actualizar(float delta) {

	    tiempo += delta;

	    int minutos = (int) (tiempo / 60);
	    int segundos = (int) (tiempo % 60);

	    vidaLabel.setText("Vida: " + jugador.obtenerVida() + "/" + jugador.obtenerVidaMaxima());

	    experienciaLabel.setText("XP: " + jugador.obtenerExperiencia() + "/100");

	    tiempoLabel.setText(String.format("Tiempo: %02d:%02d", minutos, segundos));

	    enemigosEliminadosLabel.setText("Enemigos: " + jugador.obtenerEnemigosEliminados());
	}
	
}
