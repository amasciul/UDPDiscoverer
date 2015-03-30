package fr.masciulli.udpdiscoverer.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;

import fr.masciulli.udpdiscoverer.lib.Discoverer;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            InetAddress address = Discoverer.from(this).getBroadcastAddress();
            Log.d(TAG, address.getHostAddress());
        } catch (IOException e) {
            Log.e(TAG, "error", e);
        }
    }
}
