package com.ditecting.entity;

public class Capturer {
    private int count; //maximum number of captured packet, -1 means infinite

    private int readTimeout; //read timeout [ms]

    private int snaplen; //number of bytes captured for each packet [bytes]

    private String filter; //filter condition for capture, which is consistent with filter rules in wireshark

    private int interval;

    private boolean enableAutoFind;// 1:auto find, 0:not allow

    public Capturer(int count, int readTimeout, int snaplen, String filter, int interval, boolean enableAutoFind) {
        this.count = count;
        this.readTimeout = readTimeout;
        this.snaplen = snaplen;
        this.filter = filter;
        this.interval = interval;
        this.enableAutoFind = enableAutoFind;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getSnaplen() {
        return snaplen;
    }

    public void setSnaplen(int snaplen) {
        this.snaplen = snaplen;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean getEnableAutoFind() {
        return enableAutoFind;
    }

    public void setEnableAutoFind(boolean enableAutoFind) {
        this.enableAutoFind = enableAutoFind;
    }

    @Override
    public String toString() {
        return "Capturer{" +
                "count=" + count +
                ", readTimeout=" + readTimeout +
                ", snaplen=" + snaplen +
                ", filter='" + filter + '\'' +
                ", interval=" + interval +
                ", enableAutoFind=" + enableAutoFind +
                '}';
    }
}
