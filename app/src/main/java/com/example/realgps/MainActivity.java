package com.example.realgps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;

import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements GPSCoordinate.LocationChangeListener {

    private TextView serverIp;
    private TextView serverPort;
    private GPSCoordinate locationTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(MainActivity.this, "No Location Enabled", Toast.LENGTH_SHORT).show();
            Log.v("GPS Error", "GPS is not enabled! Go to Settings and enable a location mode with GPS");
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        Toast.makeText(MainActivity.this, " Location Enabled", Toast.LENGTH_SHORT).show();
        locationTracker = new GPSCoordinate(MainActivity.this, this);
        locationTracker.startListening(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTracker.stopListening();
    }



    public void connectEvent(View view) {
        serverIp = findViewById(R.id.serverIP);
        serverPort = findViewById(R.id.serverPort);
        TextView button = findViewById(R.id.connect);
        if (TextUtils.isEmpty(serverIp.getText()) || TextUtils.isEmpty(serverPort.getText())) {
            Toast.makeText(MainActivity.this, "Server and Port Please", Toast.LENGTH_SHORT).show();
            return;
        }
        String IP = serverIp.getText().toString();
        int Port = Integer.parseInt(serverPort.getText().toString());
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("ip", IP);
        myEdit.putInt("port", Port);
        myEdit.apply();
        button.setText("Stop !!");
        // get and send location coordinate periodically until stoped

    }


    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Toast.makeText(MainActivity.this, "latitude: " + latitude +" longitude: "+ longitude, Toast.LENGTH_SHORT).show();

    }
}