package com.ditecting.honeyeye.pcap4j.extension.packet.factory.tsharkbased.services;

import com.ditecting.honeyeye.pcap4j.extension.packet.JsonPacket;
import com.ditecting.honeyeye.pcap4j.extension.packet.factory.JsonPacketFactory;
import com.ditecting.honeyeye.pcap4j.extension.packet.factory.JsonPacketFactoryBinder;
import com.ditecting.honeyeye.pcap4j.extension.packet.factory.tsharkbased.TsharkBasedTcpPortJsonPacketFactory;
import com.ditecting.honeyeye.pcap4j.extension.packet.factory.tsharkbased.TsharkBasedUdpPortJsonPacketFactory;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.factory.PacketFactory;
import org.pcap4j.packet.namednumber.NamedNumber;
import org.pcap4j.packet.namednumber.TcpPort;
import org.pcap4j.packet.namednumber.UdpPort;

import java.util.HashMap;
import java.util.Map;


/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/1 10:36
 */
public class TsharkBasedJsonPacketFactoryBinder implements JsonPacketFactoryBinder {
    private static final TsharkBasedJsonPacketFactoryBinder INSTANCE = new TsharkBasedJsonPacketFactoryBinder();

    private final Map<Class<? extends NamedNumber<?, ?>>, PacketFactory<?, ?>> packetFactories =
            new HashMap<Class<? extends NamedNumber<?, ?>>, PacketFactory<?, ?>>();

    private final Map<Class<? extends NamedNumber<?, ?>>, JsonPacketFactory<?, ?>> jsonPacketFactories =
            new HashMap<Class<? extends NamedNumber<?, ?>>, JsonPacketFactory<?, ?>>();

    private TsharkBasedJsonPacketFactoryBinder(){
        jsonPacketFactories.put(TcpPort.class, TsharkBasedTcpPortJsonPacketFactory.getInstance());
        jsonPacketFactories.put(UdpPort.class, TsharkBasedUdpPortJsonPacketFactory.getInstance());
    }

    @Override
    public <T, N extends NamedNumber<?, ?>> JsonPacketFactory<T, N> getJsonPacketFactory(Class<T> targetClass, Class<N> numberClass) {
        if (JsonPacket.class.isAssignableFrom(targetClass)) {
            JsonPacketFactory<T, N> factory = (JsonPacketFactory<T, N>) jsonPacketFactories.get(numberClass);
            if (factory != null) {
                return factory;
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public <T, N extends NamedNumber<?, ?>> PacketFactory<T, N> getPacketFactory(Class<T> targetClass, Class<N> numberClass) {
        if (Packet.class.isAssignableFrom(targetClass)) {
            PacketFactory<T, N> factory = (PacketFactory<T, N>) packetFactories.get(numberClass);
            if (factory != null) {
                return factory;
            } else {
                return null;
            }
        }
        return null;
    }


    public static TsharkBasedJsonPacketFactoryBinder getInstance() {
        return INSTANCE;
    }

}