package proyecto.com.radardec.ClasesPrincipales;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

import proyecto.com.radardec.ConexionBBDD.RadarDAO;
import proyecto.com.radardec.CursoresAdaptados.ItemFavCursor;
import proyecto.com.radardec.R;

/**
 * @author Javier
 *         Clase que muestra los radares añadidos a favoritos mostrando una chincheta con el color definido
 */
public class RadaresDestacados extends AppCompatActivity {


    private ListView radaresFav;
    private ItemFavCursor itemFavAdapter;
    public static final int VALOR_PREDETERMINADO = 0;
    private RadarDAO radarDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radares_destacados);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        miMenu(toolbar);
        //Apertura de la base de datos Interna
        radarDAO = new RadarDAO(getApplicationContext());

        radaresFav = (ListView) findViewById(R.id.listaFav);

        mostrarFav();

    }

    /**
     * Muestra los radares favoritos
     */
    public void mostrarFav() {
        Cursor cursor = radarDAO.mostrarDestacados();

        itemFavAdapter = new ItemFavCursor(this, cursor);
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
                id = (int) info.id;
                Cursor cursor = radarDAO.restaurarPredeterminado(VALOR_PREDETERMINADO, id);
                itemFavAdapter.swapCursor(cursor);
                itemFavAdapter.notifyDataSetChanged();
                Toast.makeText(getBaseContext(), "Eliminado correctamente", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Opciones del menu
     *
     * @param toolbar
     */
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
                                    Intent listarRadares = new Intent(getApplicationContext(), ListarRadares.class);
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
