package fr.masciulli.udpdiscoverer.lib;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ListenTask extends AsyncTask<byte[], Void, DatagramPacket> {
    private final DatagramSocket socket;
    @Nullable
    private final Callback callback;
    private Exception exception;

    public ListenTask(DatagramSocket socket, @Nullable Callback callback) {
        this.socket = socket;
        this.callback = callback;
    }

    @Override
    protected DatagramPacket doInBackground(byte[]... params) {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
            return packet;
        } catch (IOException exception) {
            this.exception = exception;
            cancel(true);
        } finally {
            socket.close();
        }
        return null;
    }

    @Override
    protected void onPostExecute(DatagramPacket packet) {
        if (callback != null) {
            callback.responseReceived(packet);
        }
    }

    @Override
    protected void onCancelled() {
        if (callback != null) {
            callback.error(exception);
        }
    }
}
