package fr.masciulli.udpdiscoverer.lib;

import java.net.DatagramPacket;

public interface Callback {
    void error(Exception e);
    void messageSent();
    void responseReceived(DatagramPacket response);
}
