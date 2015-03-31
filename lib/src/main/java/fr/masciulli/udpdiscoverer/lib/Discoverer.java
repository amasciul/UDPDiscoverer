package fr.masciulli.udpdiscoverer.lib;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Discoverer {

    private final Context context;
    private int localPort = -1;
    private int remotePort = -1;
    private Callback callback;
    private byte[] data;

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

        DatagramSocket socket = null;
        try {
            InetAddress address = getBroadcastAddress();
            socket = new DatagramSocket(localPort);
            socket.setBroadcast(true);
            DatagramPacket packet = new DatagramPacket(data, data.length, address, remotePort);
            socket.send(packet);
            callback.success();
        } catch (IOException exception) {
            error(exception);
        } finally {
            if (socket != null) {
                socket.close();
            }
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
    }


}
