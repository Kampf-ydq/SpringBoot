package com.ditecting.honeyeye.pcap4j.extension.packet;

import org.pcap4j.packet.AbstractPacket;
import org.pcap4j.util.ByteArrays;

import java.util.Arrays;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/28 9:10
 */
public final class IllegalJsonPacket extends AbstractJsonPacket {

    private static final long serialVersionUID = 286254263012393821L;

    private final byte[] rawData;

    /**
     * A static factory method. This method validates the arguments by {@link
     * ByteArrays#validateBounds(byte[], int, int)}, which may throw exceptions undocumented here.
     *
     * @param rawData rawData
     * @param offset offset
     * @param length length
     * @return a new IllegalJsonPacket object.
     */
    public static IllegalJsonPacket newPacket(byte[] rawData, int offset, int length) {
        ByteArrays.validateBounds(rawData, offset, length);
        return new IllegalJsonPacket(rawData, offset, length);
    }

    private IllegalJsonPacket(byte[] rawData, int offset, int length) {
        this.rawData = new byte[length];
        System.arraycopy(rawData, offset, this.rawData, 0, length);
    }

    private IllegalJsonPacket(Builder builder) {
        if (builder == null || builder.rawData == null) {
            throw new NullPointerException();
        }

        this.rawData = new byte[builder.rawData.length];
        System.arraycopy(builder.rawData, 0, this.rawData, 0, builder.rawData.length);
    }

    @Override
    public int length() {
        return rawData.length;
    }

    @Override
    public byte[] getRawData() {
        byte[] copy = new byte[rawData.length];
        System.arraycopy(rawData, 0, copy, 0, copy.length);
        return copy;
    }

    /** */
    @Override
    public Builder getBuilder() {
        return new Builder(this);
    }

    @Override
    protected String buildString() {
        StringBuilder sb = new StringBuilder();
        String ls = System.getProperty("line.separator");

        sb.append("[Illegal JsonPacket (").append(length()).append(" bytes)]").append(ls);
        sb.append("  Hex stream: ").append(ByteArrays.toHexString(rawData, " ")).append(ls);

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!this.getClass().isInstance(obj)) {
            return false;
        }
        IllegalJsonPacket other = (IllegalJsonPacket) obj;
        return Arrays.equals(rawData, other.rawData);
    }

    @Override
    protected int calcHashCode() {
        return Arrays.hashCode(rawData);
    }

    public static final class Builder extends AbstractBuilder {

        private byte[] rawData = new byte[0];

        /** */
        public Builder() {}

        private Builder(IllegalJsonPacket packet) {
            rawData = packet.rawData;
        }

        /**
         * @param rawData rawData
         * @return this Builder object for method chaining.
         */
        public Builder rawData(byte[] rawData) {
            this.rawData = rawData;
            return this;
        }

        @Override
        public IllegalJsonPacket build() {
            return new IllegalJsonPacket(this);
        }
    }
}
