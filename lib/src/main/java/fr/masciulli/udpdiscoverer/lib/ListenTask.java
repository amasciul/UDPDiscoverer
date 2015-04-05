package fr.masciulli.udpdiscoverer.lib;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ListenTask extends AsyncTask<byte[], Void, Void> {
    private final DatagramSocket socket;
    private final Callback callback;

    public ListenTask(DatagramSocket socket, Callback callback) {
        this.socket = socket;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(byte[]... params) {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
            callback.responseReceived(packet);
        } catch (IOException exception) {
            callback.error(exception);
        }
        return null;
    }
}
