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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements GPSCoordinate.LocationChangeListener {

    private TextView serverIp;
    private TextView serverPort;
    private GPSCoordinate locationTracker;
    private sendCoordinate sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverIp = findViewById(R.id.serverIP);
        serverPort = findViewById(R.id.serverPort);
        Switch switchButton = findViewById(R.id.switch1);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(MainActivity.this, "No Location Enabled", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Location Enabled", Toast.LENGTH_SHORT).show();
        }

        locationTracker = new GPSCoordinate(MainActivity.this, this);
        locationTracker.startListening(this);

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String ip = sharedPreferences.getString("ip", "");
        int port = sharedPreferences.getInt("port", 0);

        if (!TextUtils.isEmpty(ip) && port != 0) {
            serverIp.setText(ip);
            serverPort.setText(String.valueOf(port));
        }


        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ToggleScreen(isChecked);
            }
        });
    }

    private void ToggleScreen(boolean isChecked) {
        serverIp.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        serverPort.setVisibility(isChecked ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTracker.stopListening();
        if (sender != null) {
            sender.stopMessage();
        }
    }

    public void connectEvent(View view) {

        TextView button = findViewById(R.id.connect);
        String ip;
        int port;

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        ip = sharedPreferences.getString("ip", "");
        port = sharedPreferences.getInt("port", 0);

        if (!TextUtils.isEmpty(ip) || port != 0) {
            ip = serverIp.getText().toString();
            String portStr = serverPort.getText().toString();

            if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(portStr)) {
                Toast.makeText(MainActivity.this, "Server and Port Please", Toast.LENGTH_SHORT).show();
                return;
            }

            port = Integer.parseInt(portStr);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("ip", ip);
            editor.putInt("port", port);
            editor.apply();
        }

        Log.v("Connect Button", "Before Sending COordinate");
        sender = new sendCoordinate(ip, port);
        if (button.getText().toString().equals("Connect")){
        sender.start();
        button.setText("Stop !!");
        }
        else {
            button.setText("Connect");
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Toast.makeText(MainActivity.this, "latitude: " + latitude + " longitude: " + longitude, Toast.LENGTH_SHORT).show();
        if (sender != null) {
            sender.sendMessage(String.valueOf(latitude));
        }
    }
}
