package fr.masciulli.udpdiscoverer.lib;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Discoverer {

    private static final int STATE_IDLE = 0;
    private static final int STATE_BROADCASTING = 0;
    private static final int STATE_LISTENING = 0;

    private final Context context;
    private int localPort = -1;
    private int remotePort = -1;
    @Nullable
    private Callback callback;
    private byte[] data;
    private int currentState = STATE_IDLE;
    @Nullable
    private AsyncTask currentTask;

    public Discoverer(Context context) {
        this.context = context;
    }

    public static Discoverer from(Context context) {
        return new Discoverer(context);
    }

    public Discoverer remotePort(int port) {
        this.remotePort = port;
        return this;
    }

    public Discoverer localPort(int port) {
        this.localPort = port;
        return this;
    }

    public Discoverer data(byte[] data) {
        this.data = data;
        return this;
    }

    public Discoverer callback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public void broadcast() {
        if (localPort == -1) {
            error(new IllegalArgumentException("Local port not set"));
            return;
        }

        if (remotePort == -1) {
            error(new IllegalArgumentException("Remote port not set"));
            return;
        }

        if (data == null) {
            error(new IllegalArgumentException("No data to send"));
            return;
        }

        try {
            InetAddress address = getBroadcastAddress();
            final DatagramSocket socket = new DatagramSocket(localPort);
            socket.setBroadcast(true);

            Callback broadcastCallback = new Callback() {
                @Override
                public void error(Exception exception) {
                    Discoverer.this.error(exception);
                }

                @Override
                public void messageSent() {
                    Discoverer.this.messageSent();
                    currentState = STATE_LISTENING;
                    currentTask = new ListenTask(socket, callback).execute();
                }

                @Override
                public void responseReceived(DatagramPacket response) {
                    Discoverer.this.responseReceived(response);
                }
            };

            currentState = STATE_BROADCASTING;
            currentTask = new BroadcastTask(socket, address, remotePort, broadcastCallback).execute(data);
        } catch (IOException exception) {
            error(exception);
        }
    }

    public boolean isIdle() {
        return currentState == STATE_IDLE;
    }

    public boolean isBroadcasting() {
        return currentState == STATE_BROADCASTING;
    }

    public boolean isListening() {
        return currentState == STATE_LISTENING;
    }

    public void stop() {
        if (currentTask != null) {
            currentTask.cancel(true);
            currentState = STATE_IDLE;
        }
    }

    private InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        if (dhcp == null) {
            throw new IOException("Could not retrieve DHCP info");
        }

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++) {
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        }
        return InetAddress.getByAddress(quads);
    }

    private void error(Exception exception) {
        if (callback != null) {
            callback.error(exception);
        }
        currentState = STATE_IDLE;
    }

    private void messageSent() {
        if (callback != null) {
            callback.messageSent();
        }
    }

    private void responseReceived(DatagramPacket response) {
        if (callback != null) {
            callback.responseReceived(response);
        }
    }
}
