package com.ditecting.entity;

public class Listener {
    private int transmittingGrain; // 1 # others:rawPacket, 1:packet, 2:flow, 3:session

    private int transmittingTimeout; // 1  # in [s]

    private int outputtingGrain; // 1 # others:rawPacket, 1:packet, 2:flow, 3:session

    private int outputtingTimeout; // 1  # in [s]

    private int pluginGrain; // 1 # others:rawPacket, 1:packet, 2:flow, 3:session

    private int pluginTimeout; // 1  # in [s]

    private double meetingTimeout; // 10  # in [s]

    private int segmentMax; // 10000

    private TransmittingListener transmittingListener;

    public Listener(int transmittingGrain, int transmittingTimeout, int outputtingGrain, int outputtingTimeout, int pluginGrain, int pluginTimeout, double meetingTimeout, int segmentMax, TransmittingListener transmittingListener) {
        this.transmittingGrain = transmittingGrain;
        this.transmittingTimeout = transmittingTimeout;
        this.outputtingGrain = outputtingGrain;
        this.outputtingTimeout = outputtingTimeout;
        this.pluginGrain = pluginGrain;
        this.pluginTimeout = pluginTimeout;
        this.meetingTimeout = meetingTimeout;
        this.segmentMax = segmentMax;
        this.transmittingListener = transmittingListener;
    }

    public int getTransmittingGrain() {
        return transmittingGrain;
    }

    public void setTransmittingGrain(int transmittingGrain) {
        this.transmittingGrain = transmittingGrain;
    }

    public int getTransmittingTimeout() {
        return transmittingTimeout;
    }

    public void setTransmittingTimeout(int transmittingTimeout) {
        this.transmittingTimeout = transmittingTimeout;
    }

    public int getOutputtingGrain() {
        return outputtingGrain;
    }

    public void setOutputtingGrain(int outputtingGrain) {
        this.outputtingGrain = outputtingGrain;
    }

    public int getOutputtingTimeout() {
        return outputtingTimeout;
    }

    public void setOutputtingTimeout(int outputtingTimeout) {
        this.outputtingTimeout = outputtingTimeout;
    }

    public int getPluginGrain() {
        return pluginGrain;
    }

    public void setPluginGrain(int pluginGrain) {
        this.pluginGrain = pluginGrain;
    }

    public int getPluginTimeout() {
        return pluginTimeout;
    }

    public void setPluginTimeout(int pluginTimeout) {
        this.pluginTimeout = pluginTimeout;
    }

    public double getMeetingTimeout() {
        return meetingTimeout;
    }

    public void setMeetingTimeout(double meetingTimeout) {
        this.meetingTimeout = meetingTimeout;
    }

    public int getSegmentMax() {
        return segmentMax;
    }

    public void setSegmentMax(int segmentMax) {
        this.segmentMax = segmentMax;
    }

    public TransmittingListener getTransmittingListener() {
        return transmittingListener;
    }

    public void setTransmittingListener(TransmittingListener transmittingListener) {
        this.transmittingListener = transmittingListener;
    }

    @Override
    public String toString() {
        return "Listener{" +
                "transmittingGrain=" + transmittingGrain +
                ", transmittingTimeout=" + transmittingTimeout +
                ", outputtingGrain=" + outputtingGrain +
                ", outputtingTimeout=" + outputtingTimeout +
                ", pluginGrain=" + pluginGrain +
                ", pluginTimeout=" + pluginTimeout +
                ", meetingTimeout=" + meetingTimeout +
                ", segmentMax=" + segmentMax +
                ", transmittingListener=" + transmittingListener +
                '}';
    }
}
