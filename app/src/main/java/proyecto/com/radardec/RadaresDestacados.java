package proyecto.com.radardec;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class RadaresDestacados extends AppCompatActivity {

    private RadarSQLiteHelper rdb;
    private SQLiteDatabase db;
    private ListView radaresFav;
    private ItemFavCursor itemFavAdapter;
    public static final int VALOR_PREDETERMINADO = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radares_destacados);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        miMenu(toolbar);
        //Apertura de la base de datos Interna
        rdb = new RadarSQLiteHelper(this,"DBRadares",null,1);

        radaresFav = (ListView) findViewById(R.id.listaFav);

        mostrarFav();

    }

    public void mostrarFav(){
        db = rdb.getWritableDatabase();
        Cursor  cursor = db.rawQuery("SELECT * FROM Radar WHERE fav BETWEEN 1 AND 3",null);

        itemFavAdapter = new ItemFavCursor(this,cursor);
        radaresFav.setAdapter(itemFavAdapter);

        //Registramos la lista para el menu contextual
        registerForContextMenu(radaresFav);


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu_fav, menu);
        menu.setHeaderTitle("Restablecer Predeterminado");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id;
        switch (item.getItemId()) {
            case R.id.MnuFav:
                id= (int) info.id;
                db.execSQL("UPDATE Radar SET fav='"+VALOR_PREDETERMINADO+"' WHERE _id='"+id+"'");
                // Se vuelve a lanzar la consulta para actualizar el lista
                Cursor cursor1 = db.rawQuery("SELECT * FROM Radar WHERE fav BETWEEN 1 AND 3",null);
                itemFavAdapter.swapCursor(cursor1);
                itemFavAdapter.notifyDataSetChanged();
                Toast.makeText(getBaseContext(), "Eliminado correctamente", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //Opciones del menu
    public void miMenu(Toolbar toolbar) {

        Drawer builder = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIcon(R.drawable.drawer_map).withIdentifier(1).withName(R.string.drawer_titulo1),
                        new PrimaryDrawerItem().withIcon(R.drawable.drawer_lista).withIdentifier(2).withName(R.string.drawer_titulo2),
                        new PrimaryDrawerItem().withIcon(R.drawable.drawer_favoritos).withIdentifier(3).withName(R.string.drawer_titulo3),
                        new PrimaryDrawerItem().withIcon(R.drawable.drawer_ranking).withIdentifier(4).withName(R.string.drawer_titulo4)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            int id = (int) drawerItem.getIdentifier();
                            switch (id) {
                                case 1:
                                    Intent mapa = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(mapa);
                                    //Snackbar.make(view, "You Click " + id + "th button", Snackbar.LENGTH_LONG).show();
                                    return true;
                                case 2:
                                    Intent listarRadares = new Intent(getApplicationContext(),ListarRadares.class);
                                    startActivity(listarRadares);
                                    // Toast.makeText(getBaseContext(), "Lista Radares", Toast.LENGTH_LONG).show();
                                    return true;
                                case 3:
                                    Intent radaresDes = new Intent(getApplicationContext(), RadaresDestacados.class);
                                    startActivity(radaresDes);
                                    //Toast.makeText(getBaseContext(), "Favoritos", Toast.LENGTH_LONG).show();
                                    return true;
                                case 4:
                                    Intent ranking = new Intent(getApplicationContext(), RankingRadar.class);
                                    startActivity(ranking);
                                    //Toast.makeText(getBaseContext(), "Ranking", Toast.LENGTH_LONG).show();
                                    return true;
                            }
                        }

                        return true;
                    }

                }).build();

    }
}
