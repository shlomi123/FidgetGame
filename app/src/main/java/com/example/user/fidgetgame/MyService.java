package com.example.user.fidgetgame;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyService extends Service{
    private DatabaseReference root;
    private int clicks;
    private String user;
    private String country;

    public MyService() {
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        saveClicks(intent);

        return START_STICKY;
    }

    private void saveClicks(Intent intent)
    {
        clicks = intent.getExtras().getInt("Clicks");
        user = intent.getExtras().getString("Username");
        country = intent.getExtras().getString("Country");

        if (Helper.isNetworkAvailable(getApplicationContext()))
        {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            root = database.getReference();
            root.child("Countries").child(country).child(user).child("Clicks").setValue(clicks);
            root.child("World").child(user).child("Clicks").setValue(clicks);
            stopSelf();
        }
        else
        {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            intentFilter.addAction("android.net.wifi.STATE_CHANGE");
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getExtras() != null)
            {
                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if(ni != null && ni.getState() == NetworkInfo.State.CONNECTED)
                {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    root = database.getReference();
                    root.child("Countries").child(country).child(user).child("Clicks").setValue(clicks);
                    root.child("World").child(user).child("Clicks").setValue(clicks);
                    stopSelf();
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
