package proyecto.com.radardec;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.util.Log;

/**
 * Created by Javier on 20/04/2017.
 */

@SuppressLint("ValidFragment")
public class Dialogo extends DialogFragment {
    private  int id;
    private Context context;
    private SQLiteDatabase db;
    private RadarSQLiteHelper rdb;
    public static final int COLOR_AZUL = 1;
    public static final int COLOR_MORADO = 2;
    public static final int COLOR_NEGRO = 3;
    @SuppressLint("ValidFragment")
    public Dialogo (int id, Context context){
        this.id = id;
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        rdb = new RadarSQLiteHelper(context,"DBRadares",null,1);
        db = rdb.getWritableDatabase();
        final String[] items = {"Azul", "Morado", "Negro"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Selecciona color")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if(items[item] == "Azul"){
                            db.execSQL("UPDATE Radar SET fav='"+COLOR_AZUL+"' WHERE _id='"+id+"'");
                        } else if(items[item] == "Morado"){
                            db.execSQL("UPDATE Radar SET fav='"+COLOR_MORADO+"' WHERE _id='"+id+"'");
                        }else if(items[item] == "Negro"){
                            db.execSQL("UPDATE Radar SET fav='"+COLOR_NEGRO+"' WHERE _id='"+id+"'");
                        }
                        Intent intent = new Intent(context,MainActivity.class);
                        startActivity(intent);


                    }
                });

        return builder.create();
    }


}
