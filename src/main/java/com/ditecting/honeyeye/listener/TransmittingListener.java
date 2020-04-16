package com.ditecting.honeyeye.listener;

import com.ditecting.honeyeye.pcap4j.extension.packet.FullPacket;
import com.ditecting.honeyeye.pcap4j.extension.packet.pool.FullPacketPool;
import com.ditecting.honeyeye.pcap4j.extension.utils.PacketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@Slf4j
@Component
//TODO incomplete
public class TransmittingListener{

    public void gotFullPacketPool(){
        FullPacket fullPacket = FullPacketPool.currentFullPacket.get();

        /*asynchronously process fullPacketJson*/
        if(!fullPacket.isComplete()) {
            FullPacket.rebuildFullPacket(fullPacket.getPacket(), fullPacket.getPcapDataHeader(), FullPacketPool.pcapFileHeader.get());
        }
        String fullPacketJson = PacketUtil.fullPacketToJson(fullPacket);
//        log.info(fullPacketJson);


    }
}
