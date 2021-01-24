package com.ditecting.entity;

public class Loader {
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Loader(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "Loader{" +
                "filePath='" + filePath + '\'' +
                '}';
    }
}
