package com.technotroop.android_osmdroiddemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.mtp.OSMMapTilePackager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 2;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 3;

    private String fileAppendix = "";
    private String url = "http://b.tile.openstreetmap.org/%d/%d/%d.png";
    private String tempFolder = "/data/data/com.technotroop.offlinemapdemo/osmdroid/";
    private String destinationFile = "/data/data/com.technotroop.offlinemapdemo/osmdroid/";

    private int zoomMin = 8;
    private int zoomMax = 20;
    private int threadCount = 2;

    private Double north;
    private Double south;
    private Double east;
    private Double west;

    private OSMMapTilePackager.ProgressNotification progressNotification;

    private MapView mapView;

    private GeoPoint startPoint;
    private IMapController mapController;

    private CompassOverlay compassOverlay;
    private MyLocationNewOverlay locationOverlay;
    private RotationGestureOverlay rotationGestureOverlay;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        // Load default config for the mapView
        // Important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.mapView);

        // Get permission for external storage
        int permissionCheckExternalStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // Get permission for coarse location
        int permissionCheckAccessCoarseLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        // Get permission for fine location
        int permissionCheckAccessFineLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        // permission are needed in devices running Marshmallow and above.
        // Only dangerous permission needs to be checked during runtime. More on developers.google.com

        if (permissionCheckExternalStorage != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

        } else if (permissionCheckAccessCoarseLocation != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        } else if (permissionCheckAccessFineLocation != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }

        setUpMapView();
    }

    private void setUpMapView() {

        // Set tile source to MAPNIK. For more map sources visit https://github.com/osmdroid/osmdroid/wiki/Map-Sources
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // Set zoom feature
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Set min and max zoom level
        mapView.setMinZoomLevel(8);
        mapView.setMaxZoomLevel(18);


        mapController = mapView.getController();
        mapController.setZoom(12);

        //set marker of selected user location
        locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView);

        Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.ic_placeholder);
        locationOverlay.setPersonIcon(placeholder);

        locationOverlay.enableMyLocation();
        mapView.getOverlays().add(this.locationOverlay);
    }

    @Override
    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }
}
