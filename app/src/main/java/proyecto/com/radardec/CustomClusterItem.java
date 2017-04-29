package proyecto.com.radardec;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Javier on 14/02/2017.
 */

public class CustomClusterItem implements ClusterItem {
    private final LatLng coordenadas;
    String titulo;
    String snitperr;
    int fav;
    int _id;

    public CustomClusterItem(int _id, double lat, double lon, String titulo, String snitperr, int fav) {
        this._id = _id;
        coordenadas = new LatLng(lat, lon);
        this.titulo = titulo;
        this.snitperr = snitperr;
        this.fav = fav;
    }

    @Override
    public LatLng getPosition() {
        return coordenadas;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSnitperr() {
        return snitperr;
    }

    public void setSnitperr(String snitperr) {
        this.snitperr = snitperr;
    }

    public int getFav() {
        return fav;
    }

    public void setFav(int fav) {
        this.fav = fav;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}
