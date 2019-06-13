package com.alkemic.howplace.Activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.alkemic.howplace.Define;
import com.alkemic.howplace.Item.Item;
import com.alkemic.howplace.Singletone.ItemManager;
import com.alkemic.howplace.Singletone.LocationChecker;
import com.alkemic.howplace.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    Item item;
    TextView nameText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        int index = intent.getIntExtra("itemID",0);
        item = ItemManager.getInstance().items.get(index);
        nameText = findViewById(R.id.textView_map_name);
        nameText.setText(item.getName());
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Location myLocation = LocationChecker.getInstance().GetLocation();
        if(myLocation != null) {
            LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            map.addMarker(new MarkerOptions()
                    .position(myLatLng)
                    .title("ME"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,17));
        }
        if(item != null)
        {
            LatLng itemLatLng = item.getLatLng();
            if(itemLatLng != null)
            {
                map.addMarker(new MarkerOptions()
                        .position(itemLatLng)
                        .title(item.getName()));
            }
        }




    }
}
