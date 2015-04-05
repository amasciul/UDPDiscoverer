package fr.masciulli.udpdiscoverer.lib;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class BroadcastTask extends AsyncTask<byte[], Void, Void> {
    private final DatagramSocket socket;
    private final InetAddress address;
    private final int remotePort;
    @Nullable
    private final Callback callback;
    private Exception exception;

    public BroadcastTask(DatagramSocket socket, InetAddress address, int remotePort, @Nullable Callback callback) {
        this.socket = socket;
        this.address = address;
        this.remotePort = remotePort;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(byte[]... data) {
        if (data.length == 0) {
            return null;
        }

        try {
            DatagramPacket packet = new DatagramPacket(data[0], data[0].length, address, remotePort);
            socket.send(packet);
        } catch (IOException exception) {
            this.exception = exception;
            cancel(true);
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        if (callback != null) {
            socket.close();
            callback.error(exception);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (callback != null) {
            callback.messageSent();
        }
    }
}
