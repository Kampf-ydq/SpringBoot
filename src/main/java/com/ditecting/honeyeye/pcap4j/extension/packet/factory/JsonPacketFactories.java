package com.ditecting.honeyeye.pcap4j.extension.packet.factory;

import com.ditecting.honeyeye.pcap4j.extension.packet.factory.tsharkbased.services.TsharkBasedJsonPacketFactoryBinderProvider;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.packet.namednumber.NamedNumber;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/3 19:25
 */
@Slf4j
public class JsonPacketFactories {
    private static final JsonPacketFactoryBinder JSON_FACTORY_BINDER;

    static {
        TsharkBasedJsonPacketFactoryBinderProvider binderProvider = new TsharkBasedJsonPacketFactoryBinderProvider();
        JSON_FACTORY_BINDER = binderProvider.getJsonBinder();
    }

    public static <T, N extends NamedNumber<?, ?>> JsonPacketFactory<T, N> getJsonFactory(Class<T> targetClass, Class<N> numberClass) {
        if (numberClass != null && targetClass != null) {
            return JSON_FACTORY_BINDER.getJsonPacketFactory(targetClass, numberClass);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("numberClass: ").append(numberClass).append(" targetClass: ").append(targetClass);
            throw new NullPointerException(sb.toString());
        }
    }
}