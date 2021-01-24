package com.ditecting.entity;

public class SystemMode {
    private int inputingMode; // 2 # 1:capture, 2:load

    private int outputingMode; // 2 # others:plugin, 1:transmission, 2:storage, 3: transmission and storage

    public int getInputingMode() {
        return inputingMode;
    }

    public void setInputingMode(int inputingMode) {
        this.inputingMode = inputingMode;
    }

    public int getOutputingMode() {
        return outputingMode;
    }

    public void setOutputingMode(int outputingMode) {
        this.outputingMode = outputingMode;
    }

    public SystemMode(int inputingMode, int outputingMode) {
        this.inputingMode = inputingMode;
        this.outputingMode = outputingMode;
    }

    @Override
    public String toString() {
        return "SystemMode{" +
                "inputingMode=" + inputingMode +
                ", outputingMode=" + outputingMode +
                '}';
    }
}
