package com.ditecting.honeyeye.pcap4j.extension.packet.factory;

import com.ditecting.honeyeye.pcap4j.extension.packet.JsonPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/28 9:10
 */
public interface JsonPacketInstantiater {

    public JsonPacket newInstance(String rawJsonPacket, byte[] rawData);

    public JsonPacket newInstance(byte[] rawData, int offset, int length) throws IllegalRawDataException;

    public Class<? extends JsonPacket> getTargetClass();
}
