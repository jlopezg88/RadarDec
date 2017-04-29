package proyecto.com.radardec;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Javier on 30/03/2017.
 */

public class RadarSQLiteHelper extends SQLiteOpenHelper {

    String radarSql = "CREATE TABLE Radar(_id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, latitud REAL, longitud REAL, sentido TEXT, pk REAL, cont INTEGER DEFAULT '0', fav INTEGER DEFAULT '0')";

    public RadarSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(radarSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Radar");

        sqLiteDatabase.execSQL(radarSql);
    }


}
