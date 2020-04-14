package com.ditecting.honeyeye.pcap4j.extension.packet.factory;

import org.pcap4j.packet.factory.PacketFactoryBinder;
import org.pcap4j.packet.namednumber.NamedNumber;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/1 11:25
 */
public interface JsonPacketFactoryBinder extends PacketFactoryBinder {
    /**
     * Provides a {@link JsonPacketFactory} to build the received packets.
     *
     * @param targetClass targetClass
     * @param numberClass numberClass
     * @param <T> the type of object the factory method returns.
     * @param <N> the type of object that is given to the factory method.
     * @return the factory
     */
    public <T, N extends NamedNumber<?, ?>> JsonPacketFactory<T, N> getJsonPacketFactory(
            Class<T> targetClass, Class<N> numberClass);
}
