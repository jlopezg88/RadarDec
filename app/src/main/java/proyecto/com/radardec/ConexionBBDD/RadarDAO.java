package proyecto.com.radardec.ConexionBBDD;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.maps.android.clustering.ClusterManager;

import proyecto.com.radardec.CustomClusters.CustomClusterItem;

/**
 * @author Javier
 *         Clase encargada de gestionar los métodos CRUD de la BBDD interna
 */

public class RadarDAO {
    private RadarSQLiteHelper rdb;
    private SQLiteDatabase db;

    public RadarDAO() {
    }

    public RadarDAO(Context context) {
        //Apertura de la base de datos Interna
        rdb = new RadarSQLiteHelper(context, "DBRadares", null, 1);
        //Apertura DBRadares en modo escrutura
        db = rdb.getWritableDatabase();
    }

    /**
     * Rellena la BBDD interna hasta llegar al limite de registros en la BBDD externa para no repetir
     * registros
     *
     * @param denominacion
     * @param latitud
     * @param longitud
     * @param sentido
     * @param pk
     */
    public void rellenarBBDDInterna(String denominacion, double latitud,
                                    double longitud, String sentido, double pk) {
        //Comprobación si la bd está abierta correctamente
        if (db != null) {
            // Comprobación si la tabla radar existe para no rellenar la bd infinitamente
            Cursor cursor = db.rawQuery("SELECT _id FROM Radar", null);
            if (cursor.getCount() >= 30) {
                //La tabla existe
            } else {
                //Se añade los registros a la Base de datos
                db.execSQL("INSERT INTO Radar(nombre, latitud, longitud, sentido, pk)" +
                        "VALUES ('" + denominacion + "','" + latitud + "','" + longitud + "','" + sentido + "','" + pk + "')");
            }
        }

    }

    /**
     * Recupera y añade 1 al contador del radar por el que se está pasando
     *
     * @param latitud
     * @param longitud
     */
    public void rellenarContador(double latitud, double longitud) {

        Cursor cursor = db.rawQuery("SELECT cont FROM Radar WHERE latitud='" + latitud + "' AND longitud='" + longitud + "'", null);
        if (cursor.moveToFirst()) {
            do {
                int cont = cursor.getInt(cursor.getColumnIndexOrThrow("cont"));
                cont++;
                db.execSQL("UPDATE Radar SET cont='" + cont + "' WHERE latitud='" + latitud + "' AND longitud='" + longitud + "'");
            } while (cursor.moveToNext());
        }
    }

    /**
     * Rellenamos el mClusterManager con los radares obtenidos de la BBDD interna
     *
     * @param mClusterManager
     */
    public void rellenarFavoritos(ClusterManager<CustomClusterItem> mClusterManager) {
        Cursor cursor = db.rawQuery("SELECT * FROM Radar", null);
        double latitud;
        double longitud;
        String titulo;
        String sentido;
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                int fav = cursor.getInt(cursor.getColumnIndexOrThrow("fav"));
                latitud = cursor.getDouble(cursor.getColumnIndexOrThrow("latitud"));
                longitud = cursor.getDouble(cursor.getColumnIndexOrThrow("longitud"));
                titulo = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                sentido = cursor.getString(cursor.getColumnIndexOrThrow("sentido"));
                CustomClusterItem lista = new CustomClusterItem(id, latitud, longitud, "Carretera: " + titulo, "Sentido: " + sentido, fav);
                mClusterManager.addItem(lista);

            } while (cursor.moveToNext());
        }
    }

    /**
     * Devuelve el cursor para rellenar el ranking cuando el cont es 1 o mayor
     *
     * @return cursor
     */
    public Cursor mostrarRanking() {
        Cursor cursor = db.rawQuery("SELECT * FROM Radar WHERE cont BETWEEN 1 AND 999999 ORDER BY cont DESC", null);
        return cursor;
    }

    /**
     * Selecciona los radares que son destacados para mostrarlos
     *
     * @return cusor
     */
    public Cursor mostrarDestacados() {
        Cursor cursor = db.rawQuery("SELECT * FROM Radar WHERE fav BETWEEN 1 AND 3", null);
        return cursor;
    }

    /**
     * Updatea el registro de la tabla para poner los parámetros cambiados a los de por defecto
     *
     * @param valor
     * @param id
     * @return cursor
     */
    public Cursor restaurarPredeterminado(int valor, int id) {
        db.execSQL("UPDATE Radar SET fav='" + valor + "' WHERE _id='" + id + "'");
        // Se vuelve a lanzar la consulta para actualizar el lista
        Cursor cursor = db.rawQuery("SELECT * FROM Radar WHERE fav BETWEEN 1 AND 3", null);
        return cursor;
    }

    /**
     * Muestra todos los radares ordenados por el nombre
     *
     * @return cursor
     */
    public Cursor mostrarRadares() {
        Cursor cursor = db.rawQuery("SELECT * FROM Radar ORDER BY nombre", null);
        return cursor;
    }

    /**
     * Actualiza el registro con el color seleccionado
     *
     * @param color
     * @param id
     * @return cursor
     */
    public Cursor actulizarRadar(int color, int id) {
        db.execSQL("UPDATE Radar SET fav='" + color + "' WHERE _id='" + id + "'");
        Cursor cursor = db.rawQuery("SELECT * FROM Radar ORDER BY nombre", null);
        return cursor;
    }

    /**
     * Actualiza el registro con el color seleccionado
     *
     * @param color
     * @param id
     */
    public void actualizarConDialog(int color, int id) {
        db.execSQL("UPDATE Radar SET fav='" + color + "' WHERE _id='" + id + "'");
    }
}
