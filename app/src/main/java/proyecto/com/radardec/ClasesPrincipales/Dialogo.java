package proyecto.com.radardec.ClasesPrincipales;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import proyecto.com.radardec.ConexionBBDD.RadarDAO;

/**
 * @author Javier
 *         Clase que se encarga de cambiar el radar al pulsar sobre el dialogo sobre el radar
 */

@SuppressLint("ValidFragment")
public class Dialogo extends DialogFragment {
    private int id;
    private Context context;
    public static final int COLOR_AZUL = 1;
    public static final int COLOR_MORADO = 2;
    public static final int COLOR_NEGRO = 3;
    RadarDAO radarDAO;

    @SuppressLint("ValidFragment")
    public Dialogo(int id, Context context) {
        this.id = id;
        this.context = context;
        radarDAO = new RadarDAO(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String[] items = {"Azul", "Morado", "Negro"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Selecciona color")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item] == "Azul") {
                            radarDAO.actualizarConDialog(COLOR_AZUL, id);
                        } else if (items[item] == "Morado") {
                            radarDAO.actualizarConDialog(COLOR_MORADO, id);
                        } else if (items[item] == "Negro") {
                            radarDAO.actualizarConDialog(COLOR_NEGRO, id);
                        }
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);


                    }
                });

        return builder.create();
    }


}
