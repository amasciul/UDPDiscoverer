package fr.masciulli.udpdiscoverer.lib;

public interface Callback {
    void error(Exception e);
    void messageSent();
}
