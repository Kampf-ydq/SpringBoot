package com.ditecting.entity;

public class Inputer {

    private Capturer capturer;

    private Loader loader;

    public Capturer getCapturer() {
        return capturer;
    }

    public void setCapturer(Capturer capturer) {
        this.capturer = capturer;
    }

    public Loader getLoader() {
        return loader;
    }

    public void setLoader(Loader loader) {
        this.loader = loader;
    }

    public Inputer(Capturer capturer, Loader loader) {
        this.capturer = capturer;
        this.loader = loader;
    }

    @Override
    public String toString() {
        return "Inputer{" +
                "capturer=" + capturer +
                ", loader=" + loader +
                '}';
    }
}
