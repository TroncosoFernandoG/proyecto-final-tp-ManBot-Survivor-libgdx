package elementos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class GestorAudio {

    private Music musicaFondo;
    private float volumen = 0.5f;
    private boolean silenciado = false;

    public GestorAudio() {

        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("Boss Battle.wav"));

        musicaFondo.setLooping(true);

        musicaFondo.setVolume(volumen);

        musicaFondo.play();
    }

    public void subirVolumen() {
        volumen += 0.1f;

        if (volumen > 1f) {
            volumen = 1f;
        }

        if (!silenciado) {
            musicaFondo.setVolume(volumen);
        }
    }
    
    public void bajarVolumen() {
        volumen -= 0.1f;

        if (volumen < 0f) {
            volumen = 0f;
        }

        if (!silenciado) {
            musicaFondo.setVolume(volumen);
        }
    }
    
    public float obtenerVolumen() {
        return volumen;
    }
    
    public boolean estaSilenciado() {
        return silenciado;
    }
    
    public void alternarSilencio() {
        silenciado = !silenciado;

        if (silenciado) {
            musicaFondo.setVolume(0);
        } else {
            musicaFondo.setVolume(volumen);
        }
    }
    
    public void detenerMusica() {

        musicaFondo.stop();
    }

    public void reproducirMusica() {

        if (!musicaFondo.isPlaying()) {
            musicaFondo.play();
        }
    }

    public void disponer() {

        musicaFondo.dispose();
    }
}