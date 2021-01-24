package com.ditecting.entity;

/**
 * TODO
 *
 * @author ydq
 * @version 1.0
 * @date 2021/1/11 10:53
 */
public class Honeyeye {
    private SystemMode system;

    private Inputer inputer;

    private Listener listener;

    private Outputer outputer;

    public SystemMode getSystem() {
        return system;
    }

    public void setSystem(SystemMode system) {
        this.system = system;
    }

    public Inputer getInputer() {
        return inputer;
    }

    public void setInputer(Inputer inputer) {
        this.inputer = inputer;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public Outputer getOutputer() {
        return outputer;
    }

    public void setOutputer(Outputer outputer) {
        this.outputer = outputer;
    }

    @Override
    public String toString() {
        return "Honeyeye{" +
                "system=" + system +
                ", inputer=" + inputer +
                ", listener=" + listener +
                ", outputer=" + outputer +
                '}';
    }
}