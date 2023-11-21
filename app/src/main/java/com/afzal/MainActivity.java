package com.afzal;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.afzal.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "ESP-WIFI" ;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    ConnectivityManager connectivityManager ;
    ConnectivityManager.NetworkCallback  networkCallback ;

    private VedioClient mVedioClient = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        connectToWifi("esp32_AP", "pass1234");





    }



    private void connectToWifi(String ssid, String password)
    {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            try {
                Log.e(TAG,"connection wifi pre Q");
                WifiConfiguration wifiConfig = new WifiConfiguration();
                wifiConfig.SSID = "\"" + ssid + "\"";
                wifiConfig.preSharedKey = "\"" + password + "\"";
                int netId = wifiManager.addNetwork(wifiConfig);
                wifiManager.disconnect();
                wifiManager.enableNetwork(netId, true);
                wifiManager.reconnect();

            } catch ( Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG,"connection wifi  Q");

            NetworkSpecifier networkSpecifier  = new WifiNetworkSpecifier.Builder()
                    .setSsid(ssid)
                    .setWpa2Passphrase(password)
                    //.setIsHiddenSsid(true) //specify if the network does not broadcast itself and OS must perform a forced scan in order to connect
                    .build();
            NetworkRequest networkRequest  = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .setNetworkSpecifier(networkSpecifier)
                    .build();


            //connectivityManager.requestNetwork(networkRequest, mNetworkCallback);


            Intent intent = new Intent(MainActivity.this, VedioService.class);
            startService(intent);


        }
    }


    private ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback(){
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            Log.e(TAG,"onAvailable"); //phone is connected to wifi network

            Intent intent = new Intent(MainActivity.this, VedioService.class);
            startService(intent);


        }

        @Override
        public void onLosing(@NonNull Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
            //phone is about to lose connection to network
            Log.e(TAG,"onLosing");
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            //phone lost connection to network
            Log.e(TAG, "losing active connection");
        }

        @Override
        public void onUnavailable() {
            super.onUnavailable();
            //user cancelled wifi connection
            Log.e(TAG,"onUnavailable");
        }
    };


    @Override
    protected void onDestroy()
    {

        super.onDestroy();
    }


}
