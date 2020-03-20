package com.ditecting.honeyeye.convert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RawPacketConverter {
    private static final Logger logger = LoggerFactory.getLogger(RawPacketConverter.class);

    @Async("asyncConvertExecutor")
    public void executeAsync(){
        logger.info("start executeAsync");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("finish executeAsync");
    }
}
