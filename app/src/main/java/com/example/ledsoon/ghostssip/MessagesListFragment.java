package com.example.ledsoon.ghostssip;


import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class MessagesListFragment extends Fragment {

    private final String serverBaseURL = "http://192.168.0.175";
    private RecyclerView messagesListRecyclerView;
    private JSONArray listOfMessages;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public MessagesListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages_list, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        messagesListRecyclerView = view.findViewById(R.id.messagesListRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        messagesListRecyclerView.setLayoutManager(linearLayoutManager);
        getListOfMessages();

        MessagesAdapter messagesAdapter = new MessagesAdapter(createRandomNewsList(30));

        messagesListRecyclerView.setAdapter(messagesAdapter);
    }

    private void getListOfMessages() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), R.string.allow_app_to_access_devices_location, Toast.LENGTH_SHORT).show();
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
            if (location != null) {
                getMessagesNearLocation(location);
            }
        });

    }

    private void getMessagesNearLocation(Location location) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(serverBaseURL.concat("/ghostssip/get_messages.php?usersLatitude=" + location.getLatitude() + "&usersLongitude=" + location.getLongitude()),
                response -> listOfMessages = response,
                error -> {if(error instanceof ParseError) Toast.makeText(getContext(), R.string.no_messages_found_in_your_area, Toast.LENGTH_SHORT).show();});
        requestQueue.add(jsonArrayRequest);
    }

    private List<SingleMessage> createRandomNewsList(int size) {
        List<SingleMessage> result = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            SingleMessage singleMessage = new SingleMessage();
            singleMessage.author = getString(R.string.author) + i;
            singleMessage.content = getString(R.string.message) + i;
            singleMessage.isLiked = false;
            singleMessage.isDisliked = false;
            result.add(singleMessage);
        }
        return result;
    }
}
