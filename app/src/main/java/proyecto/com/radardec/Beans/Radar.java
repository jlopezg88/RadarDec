package proyecto.com.radardec.Beans;

/**
 * @author Javier
 *         Clase modelo de un Radar
 */

public class Radar {
    PuntoFin punto_final;
    PuntoIni punto_inicial;
    private String sentido;

    public Radar() {
        punto_final = new PuntoFin();
        punto_inicial = new PuntoIni();

    }

    public Radar(PuntoFin punto_final, PuntoIni punto_inicial, String sentido) {
        this.punto_final = punto_final;
        this.punto_inicial = punto_inicial;
        this.sentido = sentido;
    }

    public PuntoFin getPunto_final() {
        return punto_final;
    }

    public void setPunto_final(PuntoFin punto_final) {
        this.punto_final = punto_final;
    }

    public PuntoIni getPunto_inicial() {
        return punto_inicial;
    }

    public void setPunto_inicial(PuntoIni punto_inicial) {
        this.punto_inicial = punto_inicial;
    }

    public String getSentido() {
        return sentido;
    }

    public void setSentido(String sentido) {
        this.sentido = sentido;
    }

    @Override
    public String toString() {
        return "Radar{" +
                "punto_final=" + punto_final +
                ", punto_inicial=" + punto_inicial +
                ", sentido='" + sentido + '\'' +
                '}';
    }
}
