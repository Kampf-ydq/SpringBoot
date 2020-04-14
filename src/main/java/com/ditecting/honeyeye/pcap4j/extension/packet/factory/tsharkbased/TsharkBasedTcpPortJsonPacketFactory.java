package com.ditecting.honeyeye.pcap4j.extension.packet.factory.tsharkbased;

import com.ditecting.honeyeye.pcap4j.extension.packet.JsonPacket;
import com.ditecting.honeyeye.pcap4j.extension.packet.ModbusTcpJsonPacket;
import com.ditecting.honeyeye.pcap4j.extension.packet.factory.AbstractJsonPacketFactory;
import com.ditecting.honeyeye.pcap4j.extension.packet.factory.JsonPacketInstantiater;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.TcpPort;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/28 8:27
 */
public final class TsharkBasedTcpPortJsonPacketFactory extends AbstractJsonPacketFactory<TcpPort> {

    private static final TsharkBasedTcpPortJsonPacketFactory INSTANCE = new TsharkBasedTcpPortJsonPacketFactory();

    private TsharkBasedTcpPortJsonPacketFactory(){
        instantiaters.put(
                TcpPort.MBAP,
                new JsonPacketInstantiater() {
                    @Override
                    public JsonPacket newInstance(String rawJsonPacket, byte[] rawData){
                        return ModbusTcpJsonPacket.newPacket(rawJsonPacket, rawData);
                    }

                    @Override
                    public JsonPacket newInstance(byte[] rawData, int offset, int length)
                            throws IllegalRawDataException {
                        return ModbusTcpJsonPacket.newPacket(rawData, offset, length);
                    }

                    @Override
                    public Class<ModbusTcpJsonPacket> getTargetClass() {
                        return ModbusTcpJsonPacket.class;
                    }
                });
    }

    /** @return the singleton instance of TsharkBasedTcpPortJsonPacketFactory. */
    public static TsharkBasedTcpPortJsonPacketFactory getInstance(){ return INSTANCE;}
}