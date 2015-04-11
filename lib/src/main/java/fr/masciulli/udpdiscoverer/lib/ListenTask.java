package fr.masciulli.udpdiscoverer.lib;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ListenTask extends AsyncTask<byte[], DatagramPacket, Void> {
    private static final int SOCKET_TIMEOUT = 500;

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

        try {
            socket.setSoTimeout(SOCKET_TIMEOUT);
        } catch (SocketException exception) {
            this.exception = exception;
            socket.close();
            return null;
        }

        while (!isCancelled()) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                publishProgress(packet);
            } catch (SocketTimeoutException exception) {
                // no-op
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
            if (exception != null) {
                callback.error(exception);
            }
        }
    }
}
