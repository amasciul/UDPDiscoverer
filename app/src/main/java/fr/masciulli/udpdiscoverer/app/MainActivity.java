package fr.masciulli.udpdiscoverer.app;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import fr.masciulli.udpdiscoverer.lib.Callback;
import fr.masciulli.udpdiscoverer.lib.Discoverer;

public class MainActivity extends Activity implements Callback {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button sendButton;
    private EditText messageField;
    private EditText localPortField;
    private EditText remotePortField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        sendButton = (Button) findViewById(R.id.send);
        messageField = (EditText) findViewById(R.id.message);
        localPortField = (EditText) findViewById(R.id.local_port);
        remotePortField = (EditText) findViewById(R.id.remote_port);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageField.getText().toString();
                try {
                    int localPort = Integer.parseInt(localPortField.getText().toString());
                    int remotePort = Integer.parseInt(remotePortField.getText().toString());
                    sendMessage(message, localPort, remotePort);
                } catch (NumberFormatException exception) {
                    Toast.makeText(MainActivity.this, getString(R.string.port_format_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMessage(String message, int localPort, int remotePort) {
        Discoverer.from(MainActivity.this)
                .localPort(localPort)
                .remotePort(remotePort)
                .data(message.getBytes())
                .callback(this)
                .broadcast();
    }

    @Override
    public void error(Exception e) {
        Log.e(TAG, e.getMessage(), e);
        Toast.makeText(this, getString(R.string.message_notsent), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void success() {
        Toast.makeText(this, getString(R.string.message_sent), Toast.LENGTH_SHORT).show();
    }
}
