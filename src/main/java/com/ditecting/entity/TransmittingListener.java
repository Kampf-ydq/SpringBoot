package com.ditecting.entity;

public class TransmittingListener {

    private int port;

    private String netAddress;

    public TransmittingListener(int port, String netAddress) {
        this.port = port;
        this.netAddress = netAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getNetAddress() {
        return netAddress;
    }

    public void setNetAddress(String netAddress) {
        this.netAddress = netAddress;
    }

    @Override
    public String toString() {
        return "TransmittingListener{" +
                "port=" + port +
                ", netAddress='" + netAddress + '\'' +
                '}';
    }
}
