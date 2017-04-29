package proyecto.com.radardec;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;


import java.util.Locale;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mapa;
    private final String TAGLOG = "PEC2";
    private DatabaseReference ref;
    private ValueEventListener eventListener;
    private Marker marker;
    private double lat = 0.0;
    private double lon = 0.0;
    private ClusterManager<CustomClusterItem> mClusterManager;
    CustomClusterRenderer renderer;
    private Circle mCircle;
    private CircleOptions circleOptions;
    private MediaPlayer mediaPlayer;
    private TextToSpeech tts;
    private static String mensaje;
    private SensorManager sensorManager;
    private Sensor sensor;
    private float cambioLuz;
    private RadarSQLiteHelper rdb;
    private SQLiteDatabase db;
    private final int CERCA = 50;
    private int idDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Añade menudrawer
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        miMenu(toolbar);
        //Apertura de la base de datos Interna
        rdb = new RadarSQLiteHelper(this, "DBRadares", null, 1);
        //Inicialización del sonido
        mediaPlayer = MediaPlayer.create(this, R.raw.radar_sound);
        // inicializamos las actividades de de los intent para navegar por la app

        // Accedemos al nodo carretera de la BBDDD
        ref = FirebaseDatabase.getInstance().getReference().child("provincia").child("carretera");
        botonLocalizacion();
        //Carga del layout y el fragment donde mostrará el mapa
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //Visualización del mapa
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        comprobarLuz();
        centrarSevilla();
        mostrarRadares();
        mostrarControlZoom();
        miUbicacion();
        clickCluster();
        adaptadorInfowindow();
    }

    // Centra el mapa en Sevilla con un Zoom de 10
    public void centrarSevilla() {
        CameraUpdate camUpd1 = CameraUpdateFactory.newLatLngZoom(new LatLng(37.3914105, -5.9591776), 10);

        mapa.moveCamera(camUpd1);
    }

    //Muesta los radares en el mapa
    public void mostrarRadares() {

        //Creamos un ClusterManager para la agrupación de Markers
        mClusterManager = new ClusterManager<CustomClusterItem>(this, mapa);
        // mapa.setOnCameraChangeListener(mClusterManager);
        mapa.setOnCameraIdleListener((GoogleMap.OnCameraIdleListener) mClusterManager);
        mapa.setOnMarkerClickListener(mClusterManager);


        //Apertura DBRadares en modo escrutura
        db = rdb.getWritableDatabase();
        // Metodo donde se recupera los datos de la BBDD externa
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Recorre todas las carreteras y su contenido
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    // Deserializamos carretera
                    Carretera carretera = data.getValue(Carretera.class);
                    //Parseo de la latitud y longitud y obtención de los datos
                    double latitud = Double.parseDouble(carretera.getRadar().getPunto_inicial().getLatitud());
                    double longitud = Double.parseDouble(carretera.getRadar().getPunto_inicial().getLongitud());
                    String denominacion = carretera.getDenominacion();
                    String sentido = carretera.getRadar().getSentido();
                    double pk = Double.parseDouble(carretera.getRadar().getPunto_inicial().getPk());

                    //Obtenemos la posición del radar
                    LatLng position = new LatLng(latitud, longitud);
                    //Obtenemos la posición actual
                    LatLng ubicacion = new LatLng(lat, lon);
                    //Añadimos los Markers a la agrupación
                    //CustomClusterItem lista = new CustomClusterItem(latitud, longitud, "Carretera: " + denominacion, "Sentido: " + sentido);
                    //mClusterManager.addItem(lista);

                    //Comprobación si la bd está abierta correctamente
                    if (db != null) {
                        // Comprobación si la tabla radar existe para no rellenar la bd infinitamente
                        Cursor cursor = db.rawQuery("SELECT _id FROM Radar", null);
                        Log.e("CURSORRRRRRRRRRRRRRRRR ", String.valueOf(cursor.getCount()));
                        if (cursor.getCount() >= 30) {
                            //La tabla existe
                        } else {
                            //Se añade los registros a la Base de datos
                            db.execSQL("INSERT INTO Radar(nombre, latitud, longitud, sentido, pk)" +
                                    "VALUES ('" + denominacion + "','" + latitud + "','" + longitud + "','" + sentido + "','" + pk + "')");
                        }
                    }

                    //Definimos color y radio del circulo que rodeará los radares
                    final double radiusInMeters = 100;
                    int strokeColor = Color.parseColor("#FFCEE3F6");
                    int shadeColor = Color.parseColor("#66CEE3F6");

                    //Creamos y añadimos el circulo a los Markers
                    circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(5);
                    mCircle = mapa.addCircle(circleOptions);
                    // Combrueba si estás dentro del radio de alguno de los circulos
                    double distaciaMetros = SphericalUtil.computeDistanceBetween(ubicacion, position);
                    if (distaciaMetros <= radiusInMeters && distaciaMetros > CERCA) {
                        //Toast.makeText(getBaseContext(), "Dentro", Toast.LENGTH_LONG).show();

                        // Reproducir Voz
                        voz();
                    }
                    if (distaciaMetros <= CERCA) {
                        //Reproduce el sonido
                        mediaPlayer.start();
                        Cursor cursor = db.rawQuery("SELECT cont FROM Radar WHERE latitud='" + latitud + "' AND longitud='" + longitud + "'", null);
                        if (cursor.moveToFirst()) {
                            do {
                                int cont = cursor.getInt(cursor.getColumnIndexOrThrow("cont"));
                                cont++;
                                Log.e("AAAAAAAAAA ", String.valueOf(cont));
                                db.execSQL("UPDATE Radar SET cont='" + cont + "' WHERE latitud='" + latitud + "' AND longitud='" + longitud + "'");
                            } while (cursor.moveToNext());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAGLOG, "Error!", databaseError.toException());
            }
        };
        ref.addValueEventListener(eventListener);
        mostrarRadaresFav();
        // Creamos un rederer customizado y lo añadimos al ClusterManager
        renderer = new CustomClusterRenderer(this, mapa, mClusterManager);
        mClusterManager.setRenderer(renderer);

    }


    //Muestra los boton de zoom
    public void mostrarControlZoom() {
        mapa.getUiSettings().setZoomControlsEnabled(true);
    }

    // Al pulsar sobre el marker obtiene el id del elemento y muestra un Dialogo con opciones
    public void clickCluster() {
        mClusterManager.setOnClusterClickListener(
                new ClusterManager.OnClusterClickListener<CustomClusterItem>() {
                    @Override
                    public boolean onClusterClick(Cluster<CustomClusterItem> cluster) {
                        //Toast.makeText(MainActivity.this, "Cluster click", Toast.LENGTH_SHORT).show();
                        // if true, do not move camera
                        return false;
                    }
                });
        mClusterManager.setOnClusterItemClickListener(
                new ClusterManager.OnClusterItemClickListener<CustomClusterItem>() {
                    @Override
                    public boolean onClusterItemClick(CustomClusterItem clusterItem) {
                        //Toast.makeText(MainActivity.this, "Cluster item click ->"+clusterItem.get_id(), Toast.LENGTH_SHORT).show();
                        // if true, click handling stops here and do not show info view, do not move camera
                        // you can avoid this by calling:
                         idDialog = clusterItem.get_id();

                        renderer.getMarker(clusterItem).showInfoWindow();


                        return false;
                    }
                });

       mapa.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
           @Override
           public void onInfoWindowClick(Marker marker) {
               //Se crea el fragmento
               FragmentManager fragmentManager = getSupportFragmentManager();
               // Se crea el diálogo con los respectivos parámetros
               Log.e("IDDDDDDDDDDDDDDD ->", String.valueOf(idDialog));
               Dialogo dialogo1 = new Dialogo(idDialog, getApplicationContext());
               //Muestra el dialogo
               dialogo1.show(fragmentManager, "Dialogos");

           }
       });

    }
    // Este método personaliza el infowindow
    public void adaptadorInfowindow(){
        mapa.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View view = getLayoutInflater().inflate(R.layout.info_windows,null);
                TextView titulo = (TextView) view.findViewById(R.id.tituloInfo);
                TextView sentido = (TextView) view.findViewById(R.id.sentidoInfo);
                ImageView imagen = (ImageView) view.findViewById(R.id.imageninfo);

                titulo.setText(marker.getTitle());
                sentido.setText(marker.getSnippet());
                imagen.setImageResource(R.drawable.menu_32);
                return view;
            }
        });
    }

    //Agregamos el marcador con nuestra localización
    public void agregarMarcador(double lat, double lon) {
        LatLng coordenadas = new LatLng(lat, lon);
        CameraUpdate ubicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 18);
        if (marker != null) {
            marker.remove();
            mCircle.remove();
        }
        marker = mapa.addMarker(new MarkerOptions()
                .position(coordenadas)
                .title("Posicion Actual")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ubicacion24))
                .visible(false));
        mapa.animateCamera(ubicacion);
    }

    //Actualizamos nuestra posicion
    public void actualizarUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            String lats = String.valueOf(lat);
            String lons = String.valueOf(lon);
            Log.e("latitud ", lats);
            Log.e("longitud ", lons);
            agregarMarcador(lat, lon);
        }
    }

    //Comprueba se hay cambios en la localización
    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
            mClusterManager.clearItems();
            mostrarRadares();

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    // Obtenemos la posicion actual
    public void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mapa.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locListener);

    }

    // Botón que te ubica en tu posición
    private void botonLocalizacion() {
        FloatingActionButton localizacion = (FloatingActionButton) findViewById(R.id.myLocationButton);
        //localizacion.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white)));
        localizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapa.isMyLocationEnabled()) {
                    miUbicacion();
                }
            }
        });

    }

    // Este metodo inicializa el mensaje que se va a decir
    public void voz() {
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                mensaje = "¡Cuidado! Radar Cercano, modere su velocidad";
                tts.setLanguage(new Locale("es", "ES"));
                tts.speak(mensaje, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    // Método que comprueba si hay un cambio de luz para mostrar el mapa del día o de noche dependiendo de la luz que capte el sensor
    public void comprobarLuz() {
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_LIGHT) {

                    if (event.values[0] < 1 || event.values[0] > 5) {
                        cambioLuz = event.values[0];

                    }
                    if (cambioLuz < 1) {
                        mapa.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.mapstyle_night));
                    } else {
                        mapa.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.mapstyle_standard));
                    }

                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //Muestra los radares de la BD dependiendo de la elección de favoritos
    public void mostrarRadaresFav() {
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
