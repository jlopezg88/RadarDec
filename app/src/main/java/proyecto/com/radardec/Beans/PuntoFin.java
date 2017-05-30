package proyecto.com.radardec.Beans;

/**
 * @author Javier
 *         Clase modelo de PuntoFin
 */

public class PuntoFin {
    private String latitud;
    private String longitud;
    private String pk;

    public PuntoFin() {
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    @Override
    public String toString() {
        return "PuntoFin{" +
                "latitud=" + latitud +
                ", longitud=" + longitud +
                ", pk=" + pk +
                '}';
    }
}
