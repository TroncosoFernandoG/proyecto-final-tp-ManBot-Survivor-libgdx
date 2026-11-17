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
import com.manbotsurvivor.game.cliente.DatosJugador;
import com.manbotsurvivor.game.cliente.DatosPartida;
import com.manbotsurvivor.game.cliente.HiloCliente;

public class Hud {

    private Stage stage;
    private Viewport viewport;
    private BitmapFont fuente;
    private BitmapFont fuenteGrande;
    private HiloCliente hiloCliente;

    private Label vidaLabel;
    private Label nivelLabel;
    private Label experienciaLabel;
    private Label tiempoLabel;
    private Label enemigosEliminadosLabel;

    private Label nivelLabel2;
    private Label vidaLabel2;
    private Label experienciaLabel2;
    private Label enemigosEliminadosLabel2;

    // El HUD muestra los datos que manda el servidor (vida y experiencia compartidas)
    public Hud(SpriteBatch sb, HiloCliente hiloCliente) {
        this.hiloCliente = hiloCliente;

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
        nivelLabel = new Label("J1 - NIVEL 1", estiloResaltado);
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

        enemigosEliminadosLabel = new Label("Kills: 0", estiloNormal);
        tablaIzquierda.row();
        tablaIzquierda.add(enemigosEliminadosLabel).left();

        Table tablaDerecha = new Table();
        nivelLabel2 = new Label("J2 - NIVEL 1", estiloResaltado);
        vidaLabel2 = new Label("Vida: 100/100", estiloVida);
        experienciaLabel2 = new Label("XP: 0 / 100", estiloNormal);
        enemigosEliminadosLabel2 = new Label("Kills: 0", estiloNormal);

        tablaDerecha.add(nivelLabel2).right().padBottom(2);
        tablaDerecha.row();
        tablaDerecha.add(vidaLabel2).right().padBottom(2);
        tablaDerecha.row();
        tablaDerecha.add(experienciaLabel2).right();
        tablaDerecha.row();
        tablaDerecha.add(enemigosEliminadosLabel2).right();

        tablaSuperior.add(tablaIzquierda).expandX().left();
        tablaSuperior.add(tablaCentro).expandX().center();
        tablaSuperior.add(tablaDerecha).expandX().right();

        tablaPrincipal.add(tablaSuperior).expandX().fillX();

        stage.addActor(tablaPrincipal);
    }

    public void actualizar() {
        DatosPartida partida = hiloCliente.getDatosPartida();

        if (partida == null) {
            return;
        }

        int minutosJuego = (int) (partida.getTiempoEnJuego() / 60);
        int segundosJuego = (int) (partida.getTiempoEnJuego() % 60);

        tiempoLabel.setText(String.format("%02d:%02d", minutosJuego, segundosJuego));

        actualizarJugador(1, nivelLabel, vidaLabel, experienciaLabel, enemigosEliminadosLabel, partida);
        actualizarJugador(2, nivelLabel2, vidaLabel2, experienciaLabel2, enemigosEliminadosLabel2, partida);
    }

    private void actualizarJugador(int numero, Label nivel, Label vida, Label experiencia, Label eliminados, DatosPartida partida) {
        DatosJugador datos = hiloCliente.getDatosJugador(numero);

        String nombre = "J" + numero + (numero == hiloCliente.getNumeroJugador() ? " (VOS)" : "");

        if (datos != null && !datos.isConectado()) {
            nombre += " - DESCONECTADO";
        }

        nivel.setText(nombre + " - NIVEL " + partida.getNivel());
        vida.setText("Vida: " + partida.getVida() + "/" + partida.getVidaMaxima());
        experiencia.setText("XP: " + partida.getExperiencia() + " / " + partida.getExperienciaNecesaria());
        eliminados.setText("Kills: " + (datos == null ? 0 : datos.getEnemigosEliminados()));
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