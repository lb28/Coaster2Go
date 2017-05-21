package de.uulm.dbis.coaster2go.activities;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.gms.maps.GoogleMap;
import android.support.v4.app.FragmentManager;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.uulm.dbis.coaster2go.R;

public class MapViewActivity extends Activity implements OnMapReadyCallback{

    private MapFragment mapFragment;
    private GoogleMap map;
    double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lat = getIntent().getDoubleExtra("lat", 0.0);
        lon = getIntent().getDoubleExtra("lon", 0.0);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_view_mapview);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        setUpMap();

    }

    private void setUpMap(){

        Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)));

    }

}