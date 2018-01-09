package com.example.ledsoon.ghostssip;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private final String serverBaseURL = "http://192.168.0.175";
    private TextView title;
    private EditText newName, newMessage;
    private SpaceNavigationView bottomMenu;
    private ViewPager viewPager;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_layout);
        title = getSupportActionBar().getCustomView().findViewById(R.id.title);
        bottomMenu = findViewById(R.id.bottomMenu);
        bottomMenu.initWithSaveInstanceState(savedInstanceState);
        viewPager = findViewById(R.id.viewPager);
        setUpBottomMenu();
        setUpViewPager();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocationPermission();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bottomMenu.onSaveInstanceState(outState);
    }

    private void requestLocationPermission() {
        if(ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COARSE_LOCATION );
        }
    }

    private void setUpBottomMenu() {
        bottomMenu.addSpaceItem(new SpaceItem("MESSAGES", R.drawable.ic_question_answer_black_24dp));
        bottomMenu.addSpaceItem(new SpaceItem("MAP", R.drawable.ic_map_black_24dp));
        bottomMenu.setCentreButtonIconColorFilterEnabled(false);
        bottomMenu.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View newMessageDialogHeaderView = layoutInflater.inflate(R.layout.new_message_dialog_header_layout, null, false);
                newMessage = newMessageDialogHeaderView.findViewById(R.id.newMessage);
                Map <String, String> newMessageParameters = new HashMap<>();
                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission( MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, R.string.allow_app_to_access_devices_location, Toast.LENGTH_SHORT).show();
                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(MainActivity.this, location -> {
                            if (location != null) {
                                newMessageParameters.put("latitude", String.valueOf(location.getLatitude()));
                                newMessageParameters.put("longitude", String.valueOf(location.getLongitude()));
                            }
                        });

                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(MainActivity.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                        .setHeaderView(newMessageDialogHeaderView)
                        .addButton(getString(R.string.send), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                            if (newMessage.getText().length() <= 0) {
                                Toast.makeText(MainActivity.this, R.string.message_cant_be_empty, Toast.LENGTH_SHORT).show();
                            } else {
                                newMessageParameters.put("author", title.getText().toString());
                                newMessageParameters.put("message_body", newMessage.getText().toString());
                                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                                gregorianCalendar.add(Calendar.DATE, 1);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String expiryDate = simpleDateFormat.format(gregorianCalendar.getTime());
                                newMessageParameters.put("expiry_date", expiryDate);
                                sendToRemoteDatabase(newMessageParameters);
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                viewPager.setCurrentItem(itemIndex, true);
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
            }
        });
    }

    private void setUpViewPager() {
        Fragment[] fragments = {
                Fragment.instantiate(this, MessagesListFragment.class.getName()),
                Fragment.instantiate(this, MapFragment.class.getName()),
        };
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                bottomMenu.changeCurrentItem(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_edit:
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View nameChangeDialogHeaderView = layoutInflater.inflate(R.layout.name_change_dialog_header_layout, null, false);
                newName = nameChangeDialogHeaderView.findViewById(R.id.newName);
                newName.setText(title.getText());
                newName.setSelection(newName.getText().length());

                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                        .setHeaderView(nameChangeDialogHeaderView)
                        .addButton(getString(R.string.save), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                            if(newName.getText().length() <= 0){
                                Toast.makeText(MainActivity.this, R.string.name_cant_be_empty, Toast.LENGTH_SHORT).show();
                            }else {
                                title.setText(newName.getText().toString());
                                Toast.makeText(MainActivity.this, getString(R.string.name_changed_to).concat(newName.getText().toString()), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                builder.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void sendToRemoteDatabase(Map<String, String> message) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverBaseURL.concat("/ghostssip/put_message.php"),
                response -> {
                    if(response.equals("success")) {
                        refreshAllFragments();
                        Toast.makeText(MainActivity.this, R.string.message_sent, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(MainActivity.this, R.string.error_the_message_wasnt_sent, Toast.LENGTH_SHORT).show()){
            @Override
            protected Map<String,String> getParams(){
                return message;
            }

        };
        requestQueue.add(stringRequest);
    }

    private void refreshAllFragments() {
        for(Fragment fragment: getSupportFragmentManager().getFragments()){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.detach(fragment);
            fragmentTransaction.attach(fragment);
            fragmentTransaction.commit();
        }
    }
}
