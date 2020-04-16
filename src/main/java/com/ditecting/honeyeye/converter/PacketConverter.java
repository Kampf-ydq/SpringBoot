package com.ditecting.honeyeye.converter;

import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@Component
public class PacketConverter {
    private static final Logger logger = LoggerFactory.getLogger(PacketConverter.class);

    @Async("asyncConvertExecutor")
    public void convert(){
        logger.info("start PacketConverter");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("finish PacketConverter");
    }
}
