package com.ditecting.honeyeye.converter;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/31 10:47
 */
public enum ExcludingMode {
    ABSOLUTE(0, "Exclude All OTHERS"),
    PART(1, "INCLUDE OTHERS' NAME"),
    NONE(2, "INCLUDE ALL OTHERS");

    private ExcludingMode (int value, String message){
        this.value = value;
        this.message = message;
    }

    public int value(){
        return this.value;
    }

    public String message(){
        return this.message;
    }

    private int value;
    private String message;
}
