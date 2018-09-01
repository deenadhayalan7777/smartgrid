package com.example.balasakthi.smartgrid.Admin.Activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.balasakthi.smartgrid.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng tap = new LatLng(
                getIntent().getFloatExtra("lat",0),
                getIntent().getFloatExtra("lon",0));

        MarkerOptions options = new MarkerOptions()
                .position(tap)
                .title(getIntent().getStringExtra("tapid"))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.faucet));

        if(getIntent().getBooleanExtra("isTapOn",false))
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.faucet_enabled));

        mMap.addMarker(options);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tap,5));
    }
}
