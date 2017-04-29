package proyecto.com.radardec;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Javier on 15/04/2017.
 */

public class ItemRankingCursor extends CursorAdapter {
    public ItemRankingCursor(Context context, Cursor c) {
        super(context, c, 0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_listar,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvNombre = (TextView) view.findViewById(R.id.nombre);
        TextView tvLatitud = (TextView) view.findViewById(R.id.latitud);
        TextView tvLongitud = (TextView) view.findViewById(R.id.longitud);
        TextView tvRanking = (TextView) view.findViewById(R.id.pk);


        String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
        double latitud = cursor.getDouble(cursor.getColumnIndexOrThrow("latitud"));
        double longitud = cursor.getDouble(cursor.getColumnIndexOrThrow("longitud"));
        int ranking = cursor.getInt(cursor.getColumnIndexOrThrow("cont"));

        tvNombre.setText(nombre);
        tvLatitud.setText(String.valueOf(latitud));
        tvLongitud.setText(String.valueOf(longitud));
        tvRanking.setText(String.valueOf(ranking));

    }
}
