package com.example.anurag.forecast;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends AppCompatActivity {
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        latitude = Double.parseDouble(getIntent().getExtras().getString("lat"));
        longitude = Double.parseDouble(getIntent().getExtras().getString("lng"));
        setContentView(R.layout.activity_map);

    }

    public LatLng getLocation() {
        return new LatLng(latitude, longitude);
    }
}
