package com.example.ledsoon.ghostssip;


import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private final String serverBaseURL = "http://192.168.0.175";
    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public MapFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mapView = view.findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
            if (location != null) {
                googleMap.addCircle(new CircleOptions()
                        .center(new LatLng(location.getLatitude(), location.getLongitude()))
                        .radius(1000)
                        .fillColor(0x4D000000)
                        .strokeWidth(2));
                CameraPosition userPosition = CameraPosition.builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                        .zoom(13)
                        .build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(userPosition));
                getMessagesNearLocation(location);
            }
        });
    }

    private void getMessagesNearLocation(Location location) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(serverBaseURL.concat("/ghostssip/get_messages.php?usersLatitude=" + location.getLatitude() + "&usersLongitude=" + location.getLongitude()),
                this::viewMessagesOnMap,
                error -> {if(error instanceof ParseError) Toast.makeText(getContext(), R.string.no_messages_found_in_your_area, Toast.LENGTH_SHORT).show();});
        requestQueue.add(jsonArrayRequest);
    }


    private void viewMessagesOnMap(JSONArray jsonArrayOfMessages) {
        for (int i = 0; i < jsonArrayOfMessages.length(); i++) {
            try {
                JSONObject message = jsonArrayOfMessages.getJSONObject(i);
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(message.getDouble("latitude"), message.getDouble("longitude")))
                        .title(message.getString("author"))
                        .snippet(message.getString("message_body"))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
