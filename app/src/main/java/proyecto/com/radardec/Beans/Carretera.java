package proyecto.com.radardec.Beans;

/**
 * @author Javier
 *         Clase modelo Carretera
 */

public class Carretera {
    private String denominacion;
    Radar radar;


    public Carretera() {
        radar = new Radar();
    }

    public Carretera(String denominacion, Radar radar) {
        this.denominacion = denominacion;
        this.radar = radar;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public Radar getRadar() {
        return radar;
    }

    public void setRadar(Radar radar) {
        this.radar = radar;
    }

    @Override
    public String toString() {
        return "Carretera{" +
                "denominacion='" + denominacion + '\'' +
                ", radar=" + radar +
                '}';
    }
}
