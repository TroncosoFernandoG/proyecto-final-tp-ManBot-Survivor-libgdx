package elementos;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

public class Mapa {

    private TiledMap mapa;
    
    public boolean hayColision(float posicionX, float posicionY, float ancho, float alto) {

        Rectangle jugador = new Rectangle(posicionX, posicionY, ancho, alto);

        MapLayer capaObstaculos = mapa.getLayers().get("Obtaculos");

        if (capaObstaculos == null) {
            return false;
        }

        for (MapObject objeto : capaObstaculos.getObjects()) {

            if (objeto instanceof RectangleMapObject) {

                Rectangle obstaculo = ((RectangleMapObject) objeto).getRectangle();

                if (jugador.overlaps(obstaculo)) {
                    return true;
                }
            }
        }

        return false;
    }

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