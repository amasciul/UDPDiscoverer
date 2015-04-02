package fr.masciulli.udpdiscoverer.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import fr.masciulli.udpdiscoverer.lib.Callback;
import fr.masciulli.udpdiscoverer.lib.Discoverer;

public class MainActivity extends Activity implements Callback {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Discoverer.from(MainActivity.this)
                .localPort(8887)
                .remotePort(8888)
                .data("Hello !".getBytes())
                .callback(this)
                .broadcast();
    }

    @Override
    public void error(Exception e) {
        Log.e(TAG, e.getMessage(), e);
    }

    @Override
    public void success() {
        Log.d(TAG, "success !");
    }
}
