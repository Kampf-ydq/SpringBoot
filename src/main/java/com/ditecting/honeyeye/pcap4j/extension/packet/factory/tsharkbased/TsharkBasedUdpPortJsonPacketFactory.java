package com.ditecting.honeyeye.pcap4j.extension.packet.factory.tsharkbased;

import com.ditecting.honeyeye.pcap4j.extension.packet.JsonPacket;
import com.ditecting.honeyeye.pcap4j.extension.packet.ModbusUdpJsonPacket;
import com.ditecting.honeyeye.pcap4j.extension.packet.factory.AbstractJsonPacketFactory;
import com.ditecting.honeyeye.pcap4j.extension.packet.factory.JsonPacketInstantiater;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.namednumber.UdpPort;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/28 8:27
 */
public final class TsharkBasedUdpPortJsonPacketFactory extends AbstractJsonPacketFactory<UdpPort> {

    private static final TsharkBasedUdpPortJsonPacketFactory INSTANCE = new TsharkBasedUdpPortJsonPacketFactory();

    private TsharkBasedUdpPortJsonPacketFactory(){
        instantiaters.put(
                UdpPort.MBAP,
                new JsonPacketInstantiater() {
                    @Override
                    public JsonPacket newInstance(String rawJsonPacket, byte[] rawData){
                        return ModbusUdpJsonPacket.newPacket(rawJsonPacket, rawData);
                    }

                    @Override
                    public JsonPacket newInstance(byte[] rawData, int offset, int length)
                            throws IllegalRawDataException {
                        return ModbusUdpJsonPacket.newPacket(rawData, offset, length);
                    }

                    @Override
                    public Class<ModbusUdpJsonPacket> getTargetClass() {
                        return ModbusUdpJsonPacket.class;
                    }
                });
    }

    /** @return the singleton instance of TsharkBasedUdpPortJsonPacketFactory. */
    public static TsharkBasedUdpPortJsonPacketFactory getInstance(){ return INSTANCE;}
}