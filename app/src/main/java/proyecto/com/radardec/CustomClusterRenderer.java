package proyecto.com.radardec;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

/**
 * Created by Javier on 15/02/2017.
 */

public class CustomClusterRenderer extends DefaultClusterRenderer<CustomClusterItem>{
    private IconGenerator mClusterIconGenerator;
    private final Context mContext;
    BitmapDescriptor markerDescriptor;
    public static final int COLOR_AZUL = 1;
    public static final int COLOR_MORADO = 2;
    public static final int COLOR_NEGRO = 3;
    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<CustomClusterItem> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        mClusterIconGenerator = new IconGenerator(mContext.getApplicationContext());
    }

    @Override
    protected void onBeforeClusterItemRendered(CustomClusterItem item, MarkerOptions markerOptions) {

        //Muestra el icono dependiendo de la elección de favorito
        if (item.getFav() == COLOR_AZUL){
            markerDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.azul);
        }else if (item.getFav() == COLOR_MORADO){
            markerDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.morado);
        }else if (item.getFav() == COLOR_NEGRO){
            markerDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.negro);
        }else {
            markerDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.rojo);
        }
        markerOptions.icon(markerDescriptor).title(item.getTitulo()).snippet(item.getSnitperr());
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<CustomClusterItem> cluster, MarkerOptions markerOptions) {
        //super.onBeforeClusterRendered(cluster, markerOptions);
        mClusterIconGenerator.setBackground(
                ContextCompat.getDrawable(mContext, R.drawable.background_circle));
        mClusterIconGenerator.setTextAppearance(R.style.AppTheme_WhiteTextAppearance);
        final Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<CustomClusterItem> cluster) {
        //return super.shouldRenderAsCluster(cluster);
        return  cluster.getSize() > 1;
    }

}
