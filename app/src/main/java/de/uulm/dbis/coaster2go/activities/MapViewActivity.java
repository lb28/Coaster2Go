package de.uulm.dbis.coaster2go.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.uulm.dbis.coaster2go.R;

public class MapViewActivity extends BaseActivity
        implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int RC_PERM_GPS = 502;
    MapView mapView;
    LatLng latLngMarker;
    String name;

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        drawerToggle.setDrawerIndicatorEnabled(false);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.show();

        name = getIntent().getStringExtra("name");
        double lat = getIntent().getDoubleExtra("lat", 0.0);
        double lon = getIntent().getDoubleExtra("lon", 0.0);

        setTitle(name);

        latLngMarker = new LatLng(lat, lon);

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        // add the marker at the park/attraction position
        map.addMarker(new MarkerOptions()
                .position(latLngMarker)
                .title(name)
        );

        // set an appropriate zoom level (range is 2.0 to 21.0)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngMarker, 16));

        // enable the 'my location' icon
        enableMyLocation();

        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);

        mapView.onResume();
        progressBar.hide();
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_PERM_GPS);
        } else if(map != null) {
            // current location accessible
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_PERM_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                enableMyLocation();
            } else {
                Toast.makeText(this,
                        "Standortberechtigung nicht erteilt", Toast.LENGTH_SHORT).show();
            }
        }

    }
}