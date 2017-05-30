package proyecto.com.radardec.Beans;

/**
 * @author Javier
 *         Clase modelo PuntoIni
 */

public class PuntoIni {
    private String latitud;
    private String longitud;
    private String pk;

    public PuntoIni() {
    }

    public PuntoIni(String latitud, String longitud, String pk) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.pk = pk;
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
        return "PuntoIni{" +
                "latitud=" + latitud +
                ", longitud=" + longitud +
                ", pk=" + pk +
                '}';
    }
}

