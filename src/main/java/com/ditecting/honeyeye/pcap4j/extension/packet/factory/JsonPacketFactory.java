package com.ditecting.honeyeye.pcap4j.extension.packet.factory;

import org.pcap4j.packet.Packet;
import org.pcap4j.packet.factory.PacketFactory;
import org.pcap4j.packet.namednumber.NamedNumber;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/28 11:54
 */
public interface JsonPacketFactory<T, N extends NamedNumber<?, ?>>{

    /**
     * @param rawJsonPacket rawJsonPacket
     * @param number number
     * @return a new JsonPacket.
     */
    public T newInstance(String rawJsonPacket, byte[] rawData, N number);

    /**
     * @param rawData rawData
     * @param offset offset
     * @param length length
     * @param number number
     * @return a new data object.
     */
    public T newInstance(byte[] rawData, int offset, int length, N number);

    /**
     * @param rawData rawData
     * @param offset offset
     * @param length length
     * @return a new data object.
     */
    public T newInstance(byte[] rawData, int offset, int length);

    /**
     * @param number number
     * @return a {@link java.lang.Class Class} object this factory instantiates by {@link
     *     #newInstance(byte[], int, int, NamedNumber)} with the number argument.
     */
    public Class<? extends T> getTargetClass(N number);

    /**
     * @return a {@link java.lang.Class Class} object this factory instantiates by {@link
     *     #newInstance(byte[], int, int)}.
     */
    public Class<? extends T> getTargetClass();
}
