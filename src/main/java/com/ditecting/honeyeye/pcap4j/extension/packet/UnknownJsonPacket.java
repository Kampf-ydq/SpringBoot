package com.ditecting.honeyeye.pcap4j.extension.packet;

import org.pcap4j.packet.Packet;
import org.pcap4j.util.ByteArrays;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/1 15:56
 */
public class UnknownJsonPacket extends SimpleJsonPacket {
    private static final long serialVersionUID = 4226422843712827418L;

    public static UnknownJsonPacket newPacket(String rawJsonPacket, byte[] rawData){
        return new UnknownJsonPacket(rawJsonPacket, rawData);
    }

    private UnknownJsonPacket(String rawJsonPacket, byte[] rawData){
        super(rawJsonPacket, rawData);
    }

    public static UnknownJsonPacket newPacket(byte[] rawData, int offset, int length) {
        ByteArrays.validateBounds(rawData, offset, length);
        return new UnknownJsonPacket(rawData, offset, length);
    }

    private UnknownJsonPacket(byte[] rawData, int offset, int length) {
        super(rawData, offset, length);
    }

    private UnknownJsonPacket(Builder builder) {
        super(builder);
    }

    public String toString (){
        return  "\"Unknown\": " + super.getRawJsonPacket();
    }

    @Override
    public Builder getBuilder() {
        return new Builder(this);
    }

    public static final class Builder extends SimpleJsonPacket.Builder {

        public Builder() {}

        private Builder(UnknownJsonPacket packet) {
            super(packet);
        }

        public Builder rawJsonPacket(String val) {
            setRawJsonPacket(val);
            return this;
        }

        @Override
        public UnknownJsonPacket build() {
            return new UnknownJsonPacket(this);
        }
    }
}