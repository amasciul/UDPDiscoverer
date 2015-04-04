package fr.masciulli.udpdiscoverer.lib;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class BroadcastTask extends AsyncTask<byte[], Void, Void> {
    private final InetAddress address;
    private final int localPort;
    private final int remotePort;
    private final Callback callback;
    private Exception exception;

    public BroadcastTask(InetAddress address, int localPort, int remotePort, @Nullable Callback callback) {
        this.address = address;
        this.localPort = localPort;
        this.remotePort = remotePort;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(byte[]... data) {
        if (data.length == 0) {
            return null;
        }
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(localPort);
            socket.setBroadcast(true);
            DatagramPacket packet = new DatagramPacket(data[0], data[0].length, address, remotePort);
            socket.send(packet);
        } catch (IOException exception) {
            this.exception = exception;
            cancel(true);
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        if (callback != null) {
            callback.error(this.exception);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (callback != null) {
            callback.success();
        }
    }
}
