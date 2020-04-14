package com.ditecting.honeyeye.pcap4j.extension.packet.factory.tsharkbased.services;

import com.ditecting.honeyeye.pcap4j.extension.packet.factory.JsonPacketFactoryBinder;
import com.ditecting.honeyeye.pcap4j.extension.packet.factory.JsonPacketFactoryBinderProvider;
import org.pcap4j.packet.factory.PacketFactoryBinder;
import org.springframework.stereotype.Component;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/1 10:34
 */
@Component
public class TsharkBasedJsonPacketFactoryBinderProvider implements JsonPacketFactoryBinderProvider {

    @Override
    public PacketFactoryBinder getBinder() {
        return (PacketFactoryBinder)TsharkBasedJsonPacketFactoryBinder.getInstance();
    }

    @Override
    public JsonPacketFactoryBinder getJsonBinder() {
        return (JsonPacketFactoryBinder) TsharkBasedJsonPacketFactoryBinder.getInstance();
    }
}