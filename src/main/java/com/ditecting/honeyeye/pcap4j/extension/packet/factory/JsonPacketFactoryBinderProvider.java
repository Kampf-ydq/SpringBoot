package com.ditecting.honeyeye.pcap4j.extension.packet.factory;

import org.pcap4j.packet.factory.PacketFactoryBinderProvider;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/1 11:17
 */
public interface JsonPacketFactoryBinderProvider extends PacketFactoryBinderProvider {

    /**
     * The instance of the {@link JsonPacketFactoryBinder} to use to build the packets.
     *
     * @return a {@link JsonPacketFactoryBinder}
     */
    public JsonPacketFactoryBinder getJsonBinder();
}