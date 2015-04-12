package fr.masciulli.udpdiscoverer.app;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;

import fr.masciulli.udpdiscoverer.lib.Callback;
import fr.masciulli.udpdiscoverer.lib.Discoverer;

public class MainActivity extends ActionBarActivity implements Callback {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final long UPDATE_PERIOD = 500;

    private TextView stateTextView;
    private EditText messageField;
    private EditText localPortField;
    private EditText remotePortField;
    @Nullable
    private Discoverer discoverer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        stateTextView = (TextView) findViewById(R.id.state);
        messageField = (EditText) findViewById(R.id.message);
        localPortField = (EditText) findViewById(R.id.local_port);
        remotePortField = (EditText) findViewById(R.id.remote_port);

        updateState();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateState();
                handler.postDelayed(this, UPDATE_PERIOD);
            }
        }, UPDATE_PERIOD);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                String message = messageField.getText().toString();
                try {
                    int localPort = Integer.parseInt(localPortField.getText().toString());
                    int remotePort = Integer.parseInt(remotePortField.getText().toString());
                    sendMessage(message, localPort, remotePort);
                } catch (NumberFormatException exception) {
                    Toast.makeText(MainActivity.this, getString(R.string.port_format_error), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_stop:
                stopListening();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void error(Exception e) {
        Log.e(TAG, e.getMessage(), e);
        Toast.makeText(this, getString(R.string.message_notsent), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void messageSent() {
        Toast.makeText(this, getString(R.string.message_sent), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void responseReceived(DatagramPacket response) {
        Toast.makeText(this, getString(R.string.message_received, new String(response.getData())), Toast.LENGTH_SHORT).show();
    }

    private void sendMessage(String message, int localPort, int remotePort) {
        discoverer = Discoverer.from(this)
                .localPort(localPort)
                .remotePort(remotePort)
                .data(message.getBytes())
                .callback(this);
        discoverer.broadcast();
    }

    private void stopListening() {
        if (discoverer != null) {
            discoverer.stop();
        }
    }

    private void updateState() {
        if (discoverer != null) {
            if (discoverer.isIdle()) {
                stateTextView.setText(getString(R.string.idle));
            } else if (discoverer.isBroadcasting()) {
                stateTextView.setText(getString(R.string.broadcasting));
            } else if (discoverer.isListening()){
                stateTextView.setText(getString(R.string.listening));
            }
        }
    }
}
