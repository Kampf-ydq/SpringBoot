package com.ditecting.honeyeye.listener;

import com.ditecting.honeyeye.pcap4j.extension.core.FullPacketListener;
import com.ditecting.honeyeye.pcap4j.extension.core.TsharkMappings;
import com.ditecting.honeyeye.pcap4j.extension.packet.pool.FullPacketPool;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.packet.Packet;
import org.springframework.stereotype.Component;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@Slf4j
@Component
public class LoadingListener implements FullPacketListener {
    /**
     *  observer mode, call back to gotFullPacket() to process the packet content after loading it
     *
     * @param packet packet
     * @param pcapDataHeader pcapDataHeader
     */
    @Override
    public void gotFullPacket(@NonNull Packet packet, @NonNull TsharkMappings.PcapDataHeader pcapDataHeader) {
        FullPacketPool.addToFullPacketPool(packet, pcapDataHeader);
    }

    /**
     * observer mode, call back to gotPacket() to process the packet content after capturing it
     *
     * @param packet
     */
    @Override
    public void gotPacket (Packet packet) {}

}
