package fr.masciulli.udpdiscoverer.lib;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ListenTask extends AsyncTask<byte[], DatagramPacket, Void> {
    private final DatagramSocket socket;
    @Nullable
    private final Callback callback;
    private Exception exception;

    public ListenTask(DatagramSocket socket, @Nullable Callback callback) {
        this.socket = socket;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(byte[]... params) {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (!isCancelled()) {
            try {
                socket.receive(packet);
                publishProgress(packet);
            } catch (IOException exception) {
                this.exception = exception;
                cancel(true);
            }
        }
        socket.close();
        return null;
    }

    @Override
    protected void onProgressUpdate(DatagramPacket... packets) {
        if (callback != null) {
            for (DatagramPacket packet : packets) {
                callback.responseReceived(packet);
            }
        }
    }

    @Override
    protected void onCancelled() {
        if (callback != null) {
            callback.error(exception);
        }
    }
}
