package com.ditecting.honeyeye.pcap4j.extension.utils;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/30 19:57
 */
public final class WinTsharkException extends Exception{
    private static final long serialVersionUID = -3447878783735534335L;

    private Integer status = null;

    /** */
    public WinTsharkException() {
        super();
    }

    /** @param message message */
    public WinTsharkException(String message) {
        super(message);
    }

    /**
     * @param message message
     * @param status status
     */
    public WinTsharkException(String message, Integer status) {
        super(message);
        this.status = status;
    }

    /**
     * @param message message
     * @param cause cause
     */
    public WinTsharkException(String message, Throwable cause) {
        super(message, cause);
    }

    /** @param cause cause */
    public WinTsharkException(Throwable cause) {
        super(cause);
    }

    /**
     * @return the status returned by cmd ErrorStream
     */
    public Integer getStatus() {
        return status;
    }

    void setStatus(Integer status) {
        this.status = status;
    }
}