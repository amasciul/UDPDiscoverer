package fr.masciulli.udpdiscoverer.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import fr.masciulli.udpdiscoverer.lib.Callback;
import fr.masciulli.udpdiscoverer.lib.Discoverer;

public class MainActivity extends ActionBarActivity implements Callback {
    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText messageField;
    private EditText localPortField;
    private EditText remotePortField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        messageField = (EditText) findViewById(R.id.message);
        localPortField = (EditText) findViewById(R.id.local_port);
        remotePortField = (EditText) findViewById(R.id.remote_port);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_send) {
            String message = messageField.getText().toString();
            try {
                int localPort = Integer.parseInt(localPortField.getText().toString());
                int remotePort = Integer.parseInt(remotePortField.getText().toString());
                sendMessage(message, localPort, remotePort);
            } catch (NumberFormatException exception) {
                Toast.makeText(MainActivity.this, getString(R.string.port_format_error), Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void sendMessage(String message, int localPort, int remotePort) {
        Discoverer.from(MainActivity.this)
                .localPort(localPort)
                .remotePort(remotePort)
                .data(message.getBytes())
                .callback(this)
                .broadcast();
    }
}
