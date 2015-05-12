package fr.masciulli.udpdiscoverer.app;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

public class UdpDiscovererApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
