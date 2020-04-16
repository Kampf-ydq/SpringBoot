package com.ditecting.honeyeye.listener;

import com.ditecting.honeyeye.pcap4j.extension.core.FullPacketListener;
import com.ditecting.honeyeye.pcap4j.extension.core.TsharkMappings;
import com.ditecting.honeyeye.pcap4j.extension.packet.FullPacket;
import com.ditecting.honeyeye.pcap4j.extension.packet.pool.FullPacketPool;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.packet.Packet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@Slf4j
@Component
public class CapturingListener implements FullPacketListener {

    @Autowired
    OutputingListener outputingListener;

    @Autowired
    TransmittingListener transmittingListener;

    /**
     *  observer mode, call back to gotFullPacket() to process the packet content after loading it
     *
     * @param packet packet
     * @param pcapDataHeader pcapDataHeader
     */
    @Override
    public void gotFullPacket(@NonNull Packet packet, @NonNull TsharkMappings.PcapDataHeader pcapDataHeader) {
        FullPacketPool.addToCurrentFullPacket(packet, pcapDataHeader);

        outputingListener.gotFullPacketPool(true, 1);
        transmittingListener.gotFullPacketPool();
    }

    /**
     * observer mode, call back to gotPacket() to process the packet content after capturing it
     *
     * @param packet
     */
    @Override
    public void gotPacket (Packet packet) {}

}
