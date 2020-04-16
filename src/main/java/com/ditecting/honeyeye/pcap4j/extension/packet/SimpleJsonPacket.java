package com.ditecting.honeyeye.pcap4j.extension.packet;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/1 14:53
 */
@EqualsAndHashCode
abstract class SimpleJsonPacket extends AbstractJsonPacket{

    private static final long serialVersionUID = 543661064378159513L;

    /*store raw json packet*/
    private final String rawJsonPacket;
    private final byte[] rawData;

    protected SimpleJsonPacket(String rawJsonPacket, byte[] rawData){
        this.rawJsonPacket = rawJsonPacket;
        this.rawData = rawData;
    }

    protected SimpleJsonPacket(byte[] rawData, int offset, int length){
        rawJsonPacket = null;
        this.rawData = rawData;
    }

    protected SimpleJsonPacket(Builder builder) {
        if(builder == null){
            StringBuilder sb = new StringBuilder();
            sb.append("builder: ").append(builder);
            throw new NullPointerException(sb.toString());
        }

        this.rawJsonPacket = builder.rawJsonPacket;
        this.rawData = builder.rawData;
    }

    public String getRawJsonPacket(){
        return this.rawJsonPacket;
    }

    public abstract static class Builder extends AbstractBuilder{
        private String rawJsonPacket;
        private  byte[] rawData;

        public Builder() {
        }

        public Builder(SimpleJsonPacket packet) {
            this.rawJsonPacket = packet.rawJsonPacket;
            this.rawData = packet.rawData;
        }

        public void setRawJsonPacket(String rawJsonPacket) {
            this.rawJsonPacket = rawJsonPacket;
        }

        public void setRawData (byte[] rawData) {
            this.rawData = rawData;
        }
    }
}