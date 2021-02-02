package com.ditecting.honeyeye.listener;

import com.ditecting.honeyeye.cachepool.InputCachePool;
import com.ditecting.honeyeye.pcap4j.extension.core.FullPacketListener;
import com.ditecting.honeyeye.pcap4j.extension.core.TsharkMappings;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.packet.Packet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@Slf4j
@Component
@Setter
public class LoadingListener implements FullPacketListener {

    @Autowired
    private InputCachePool inputCachePool;

    /**
     *  observer mode, call back to gotFullPacket() to process the packet content after loading it
     *
     * @param packet packet
     * @param pcapDataHeader pcapDataHeader
     */
    @Override
    public void gotFullPacket(@NonNull Packet packet, @NonNull TsharkMappings.PcapDataHeader pcapDataHeader) {
        inputCachePool.addFullPacket(packet, pcapDataHeader);
    }

    /**
     * observer mode, call back to gotPacket() to process the packet content after loading it
     *
     * @param packet
     */
    @Override
    public void gotPacket (Packet packet) {}

    public void setInputCachePool(InputCachePool inputCachePool) {
        this.inputCachePool = inputCachePool;
    }

    @Override
    public String toString() {
        return "LoadingListener{" +
                "inputCachePool=" + inputCachePool +
                '}';
    }
}
