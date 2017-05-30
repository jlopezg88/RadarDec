package proyecto.com.radardec.ClasesPrincipales;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import proyecto.com.radardec.CursoresAdaptados.ItemListarCursor;
import proyecto.com.radardec.R;

/**
 * @author Javier
 *         Clase que se encarga de mostrar todos los radares almacenados en la BBDD interna
 */
public class ListarRadares extends AppCompatActivity {

    private ListView listRadares;
    private ItemListarCursor itemAdapter;
    public static final int COLOR_AZUL = 1;
    public static final int COLOR_MORADO = 2;
    public static final int COLOR_NEGRO = 3;
    RadarDAO radarDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listar_radares);
        //Definición del menú
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        miMenu(toolbar);
        //Apertura de la base de datos Interna
        radarDAO = new RadarDAO(getApplicationContext());

        listRadares = (ListView) findViewById(R.id.lista);

        listaRadares();

    }

    /**
     * Se recorre la BD
     */
    public void listaRadares() {
        Cursor cursor = radarDAO.mostrarRadares();

        itemAdapter = new ItemListarCursor(this, cursor);
        listRadares.setAdapter(itemAdapter);
        //itemAdapter.changeCursor(newCursor);

        //Registramos la lista para el menu contextual
        registerForContextMenu(listRadares);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
        menu.setHeaderTitle("Elija Color para el radar");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id;
        switch (item.getItemId()) {
            case R.id.MnuOpc1:
                id = (int) info.id;
                Cursor cursor = radarDAO.actulizarRadar(COLOR_AZUL, id);
                itemAdapter.swapCursor(cursor);
                itemAdapter.notifyDataSetChanged();
                Toast.makeText(getBaseContext(), "Radar Azul añadido a favoritos", Toast.LENGTH_LONG).show();
                return true;
            case R.id.MnuOpc2:
                id = (int) info.id;
                Cursor cursor2 = radarDAO.actulizarRadar(COLOR_MORADO, id);
                ;
                itemAdapter.swapCursor(cursor2);
                itemAdapter.notifyDataSetChanged();
                Toast.makeText(getBaseContext(), "Radar Morado añadido a favoritos", Toast.LENGTH_LONG).show();
                return true;
            case R.id.MnuOpc3:
                id = (int) info.id;
                Cursor cursor3 = radarDAO.actulizarRadar(COLOR_NEGRO, id);
                itemAdapter.swapCursor(cursor3);
                itemAdapter.notifyDataSetChanged();
                Toast.makeText(getBaseContext(), "Radar Negro añadido a favoritos", Toast.LENGTH_LONG).show();
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
