package com.ditecting.honeyeye.pcap4j.extension.packet.factory;

import com.ditecting.honeyeye.pcap4j.extension.packet.IllegalJsonPacket;
import com.ditecting.honeyeye.pcap4j.extension.packet.JsonPacket;
import com.ditecting.honeyeye.pcap4j.extension.packet.UnknownJsonPacket;
import lombok.NonNull;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.NamedNumber;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/28 9:10
 */
public abstract class AbstractJsonPacketFactory<N extends NamedNumber<?, ?>>
    implements JsonPacketFactory<JsonPacket, N> {

    /** */
    protected final Map<N, JsonPacketInstantiater> instantiaters = new HashMap<N, JsonPacketInstantiater>();

    @Override
    public JsonPacket newInstance(@NonNull String rawJsonPacket, byte[] rawData, @NonNull N number){
        JsonPacketInstantiater instantiater = instantiaters.get(number);
        if(instantiater != null){
            return  instantiater.newInstance(rawJsonPacket, rawData);
        }

        return UnknownJsonPacket.newPacket(rawJsonPacket, rawData);
    }

    @Override
    public JsonPacket newInstance(byte[] rawData, int offset, int length, N number) {
        if (rawData == null || number == null) {
            StringBuilder sb = new StringBuilder(40);
            sb.append("rawData: ").append(rawData).append(" number: ").append(number);
            throw new NullPointerException(sb.toString());
        }

        JsonPacketInstantiater instantiater = instantiaters.get(number);
        if (instantiater != null) {
            try {
                return instantiater.newInstance(rawData, offset, length);
            } catch (IllegalRawDataException e) {
                return IllegalJsonPacket.newPacket(rawData, offset, length);
            }
        }

        return newInstance(rawData, offset, length);
    }

    @Override
    public JsonPacket newInstance(byte[] rawData, int offset, int length) {
        return null;
    }

    @Override
    public Class<? extends JsonPacket> getTargetClass(N number) {
        if (number == null) {
            throw new NullPointerException("number must not be null.");
        }
        JsonPacketInstantiater pi = instantiaters.get(number);
        return pi != null ? pi.getTargetClass() : getTargetClass();
    }

    @Override
    public Class<? extends JsonPacket> getTargetClass() {
        return UnknownJsonPacket.class;
    }
}
