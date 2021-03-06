package proyecto.com.radardec.CursoresAdaptados;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import proyecto.com.radardec.R;

/**
 * @author Javier
 *         Clase que adapta el cursor de listar para personalizar el listview
 */

public class ItemListarCursor extends CursorAdapter {
    public ItemListarCursor(Context context, Cursor c) {
        super(context, c, 0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_lista, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvNombre = (TextView) view.findViewById(R.id.nombre);
        TextView tvLatitud = (TextView) view.findViewById(R.id.latitud);
        TextView tvLongitud = (TextView) view.findViewById(R.id.longitud);
        TextView tvPk = (TextView) view.findViewById(R.id.pk);
        ImageView imagen = (ImageView) view.findViewById(R.id.imagen);


        String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
        double latitud = cursor.getDouble(cursor.getColumnIndexOrThrow("latitud"));
        double longitud = cursor.getDouble(cursor.getColumnIndexOrThrow("longitud"));
        double pk = cursor.getDouble(cursor.getColumnIndexOrThrow("pk"));
        int fav = cursor.getInt(cursor.getColumnIndexOrThrow("fav"));

        tvNombre.setText(nombre);
        tvLatitud.setText(String.valueOf(latitud));
        tvLongitud.setText(String.valueOf(longitud));
        tvPk.setText(String.valueOf(pk));
        if (fav > 0) {
            imagen.setImageResource(R.drawable.chincheta_32);
        }

    }
}
