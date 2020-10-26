package com.ditecting.honeyeye.listener;

import com.ditecting.honeyeye.cachepool.ConverterCachePool;
import com.ditecting.honeyeye.cachepool.OutputCachePool;
import com.ditecting.honeyeye.cachepool.PluginCachePool;
import com.ditecting.honeyeye.cachepool.TransmitterCachePool;
import com.ditecting.honeyeye.converter.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@Component
@Slf4j
public class ConvertingListener {

    @Value("${honeyeye.listener.transmittingGrain}")
    private int transmittingGrain;
    @Value("${honeyeye.listener.transmittingTimeout}")
    private double transmittingTimeout;

    @Value("${honeyeye.listener.outputtingGrain}")
    private int outputtingGrain;
    @Value("${honeyeye.listener.outputtingTimeout}")
    private double outputtingTimeout;

    @Value("${honeyeye.listener.pluginGrain}")
    private int pluginGrain;
    @Value("${honeyeye.listener.pluginTimeout}")
    private double pluginTimeout;

    @Value("${honeyeye.system.inputingMode}")
    private int inputingMode;// 1:capture, 2:load

    @Value("${honeyeye.system.outputingMode}")
    private int outputingMode;// 1:transmission, 2:storage, 3: double

    @Autowired
    private ConverterCachePool converterCachePool;

    @Autowired
    private TransmitterCachePool transmitterCachePool;

    @Autowired
    private OutputCachePool outputCachePool;

    @Autowired
    private PluginCachePool pluginCachePool;

    public void gotPacketMeeting (CountDownLatch countDownLatch) {
        Converter converter;
        Thread thread;
        switch (outputingMode) {
            case 1:
                converter = new Converter(converterCachePool, transmitterCachePool, null, null, countDownLatch, transmittingGrain, outputtingGrain, pluginGrain, transmittingTimeout, outputtingTimeout, pluginTimeout);
                thread = new Thread(converter);
                thread.start();
                break;
            case 2:
                converter = new Converter(converterCachePool, null, outputCachePool, null, countDownLatch, transmittingGrain, outputtingGrain, pluginGrain, transmittingTimeout, outputtingTimeout, pluginTimeout);
                thread = new Thread(converter);
                thread.start();
                break;
            case 3:
                converter = new Converter(converterCachePool, transmitterCachePool, outputCachePool, null, countDownLatch, transmittingGrain, outputtingGrain, pluginGrain, transmittingTimeout, outputtingTimeout, pluginTimeout);
                thread = new Thread(converter);
                thread.start();
                break;
            default:
                converter = new Converter(converterCachePool, null, null, pluginCachePool, countDownLatch, transmittingGrain, outputtingGrain, pluginGrain, transmittingTimeout, outputtingTimeout, pluginTimeout);
                thread = new Thread(converter);
                thread.start();
                log.warn("No Transmitter or Outputer is started.");
        }
    }
}
