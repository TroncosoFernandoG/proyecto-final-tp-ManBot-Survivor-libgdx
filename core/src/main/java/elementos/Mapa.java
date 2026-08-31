package elementos;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class Mapa {

    private TiledMap mapa;

    public Mapa() {

        TmxMapLoader cargadorMapa = new TmxMapLoader();

        mapa = cargadorMapa.load("mapa.tmx");
    }

    public TiledMap obtenerMapa() {

        return mapa;
    }

    public void dispose() {

        mapa.dispose();
    }
}