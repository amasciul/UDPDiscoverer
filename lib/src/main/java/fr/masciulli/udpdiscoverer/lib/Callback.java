package fr.masciulli.udpdiscoverer.lib;

import java.net.DatagramPacket;

public interface Callback {
    void error(Exception exception);
    void messageSent();
    void responseReceived(DatagramPacket response);
}
