package com.ditecting.honeyeye.listener;

import com.ditecting.honeyeye.cachepool.InputCachePool;
import com.ditecting.honeyeye.inputer.capturer.CaptureHolder;
import com.ditecting.honeyeye.pcap4j.extension.core.FullPacketListener;
import com.ditecting.honeyeye.pcap4j.extension.core.TsharkMappings;
import lombok.NonNull;
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
public class CapturingListener implements FullPacketListener {
    private boolean stopSignal = false;

    public void setStopSignal (boolean stopSignal){
        this.stopSignal = stopSignal;
    }

    @Autowired
    private InputCachePool inputCachePool;

    @Autowired
    private CaptureHolder captureHolder;

    /**
     *  observer mode, call back to gotFullPacket() to process the packet content after capturing it
     *
     * @param packet packet
     * @param pcapDataHeader pcapDataHeader
     */
    @Override
    public void gotFullPacket(@NonNull Packet packet, @NonNull TsharkMappings.PcapDataHeader pcapDataHeader) {
        inputCachePool.addFullPacket(packet, pcapDataHeader);
        if(stopSignal){
            captureHolder.breakLoop();
        }
    }

    /**
     * observer mode, call back to gotPacket() to process the packet content after capturing it
     *
     * @param packet
     */
    @Override
    public void gotPacket (Packet packet) {

    }

}
