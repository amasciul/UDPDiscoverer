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
    private static final int LOCAL_PORT = 8887;
    private static final int REMOTE_PORT = 8888;

    private Button sendButton;
    private EditText messageField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        sendButton = (Button) findViewById(R.id.send);
        messageField = (EditText) findViewById(R.id.message);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(messageField.getText().toString());
            }
        });
    }

    private void sendMessage(String message) {
        Discoverer.from(MainActivity.this)
                .localPort(LOCAL_PORT)
                .remotePort(REMOTE_PORT)
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
