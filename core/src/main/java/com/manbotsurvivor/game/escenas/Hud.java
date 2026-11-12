package com.manbotsurvivor.game.escenas;

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
import com.manbotsurvivor.game.jugador.Jugador;
import com.manbotsurvivor.game.gestores.GestorPartida;

public class Hud {

    private Stage stage;
    private Viewport viewport;
    private BitmapFont fuente;
    private BitmapFont fuenteGrande;
    private Jugador jugador;
    private GestorPartida gestorPartida;

    private Label vidaLabel;
    private Label nivelLabel;
    private Label experienciaLabel;
    private Label tiempoLabel;
    private Label enemigosEliminadosLabel;

    public Hud(SpriteBatch sb, Jugador jugador, GestorPartida gestorPartida) {
        this.jugador = jugador;
        this.gestorPartida = gestorPartida;

        viewport = new FitViewport(ManBotSurvivor.V_WIDTH, ManBotSurvivor.V_HEIGHT, new OrthographicCamera());

        stage = new Stage(viewport, sb);

        fuente = new BitmapFont();
        fuente.getData().setScale(0.3f);

        fuenteGrande = new BitmapFont();
        fuenteGrande.getData().setScale(0.45f);

        Label.LabelStyle estiloNormal = new Label.LabelStyle(fuente, Color.WHITE);
        Label.LabelStyle estiloResaltado = new Label.LabelStyle(fuenteGrande, Color.YELLOW);
        Label.LabelStyle estiloVida = new Label.LabelStyle(fuente, Color.GREEN);

        Table tablaPrincipal = new Table();
        tablaPrincipal.setFillParent(true);
        tablaPrincipal.top().pad(10);

        Table tablaSuperior = new Table();

        Table tablaIzquierda = new Table();
        nivelLabel = new Label("NIVEL 1", estiloResaltado);
        vidaLabel = new Label("Vida: 100/100", estiloVida);
        experienciaLabel = new Label("XP: 0 / 100", estiloNormal);

        tablaIzquierda.add(nivelLabel).left().padBottom(2);
        tablaIzquierda.row();
        tablaIzquierda.add(vidaLabel).left().padBottom(2);
        tablaIzquierda.row();
        tablaIzquierda.add(experienciaLabel).left();

        Table tablaCentro = new Table();
        tiempoLabel = new Label("00:00", estiloResaltado);
        tablaCentro.add(tiempoLabel).center();

        Table tablaDerecha = new Table();
        enemigosEliminadosLabel = new Label("Kills: 0", estiloResaltado);
        tablaDerecha.add(enemigosEliminadosLabel).right();

        tablaSuperior.add(tablaIzquierda).expandX().left();
        tablaSuperior.add(tablaCentro).expandX().center();
        tablaSuperior.add(tablaDerecha).expandX().right();

        tablaPrincipal.add(tablaSuperior).expandX().fillX();

        stage.addActor(tablaPrincipal);
    }

    public void actualizar() {
        int minutosJuego = (int) (gestorPartida.obtenerTiempoEnJuego() / 60);
        int segundosJuego = (int) (gestorPartida.obtenerTiempoEnJuego() % 60);

        vidaLabel.setText("Vida: " + jugador.obtenerVida() + "/" + jugador.obtenerVidaMaxima());
        nivelLabel.setText("NIVEL " + jugador.obtenerNivel());
        experienciaLabel.setText("XP: " + jugador.obtenerExperiencia() + " / " + jugador.obtenerExperienciaNecesaria());
        tiempoLabel.setText(String.format("%02d:%02d", minutosJuego, segundosJuego));
        
        enemigosEliminadosLabel.setText("Kills: " + jugador.obtenerEnemigosEliminados());
    }

    public void dibujar() {
        stage.draw();
    }

    public void resize(int ancho, int alto) {
        viewport.update(ancho, alto, true);
    }

    public OrthographicCamera obtenerCamara() {
        return (OrthographicCamera) stage.getCamera();
    }

    public void disponer() {
        stage.dispose();
        fuente.dispose();
        fuenteGrande.dispose();
    }
}