package com.ditecting.honeyeye.converter;

import com.ditecting.honeyeye.cachepool.ConverterCachePool;
import com.ditecting.honeyeye.cachepool.OutputCachePool;
import com.ditecting.honeyeye.cachepool.PluginCachePool;
import com.ditecting.honeyeye.cachepool.TransmitterCachePool;
import com.ditecting.honeyeye.converter.datatype.PacketMeeting;
import com.ditecting.honeyeye.converter.utils.ConverterUtil;
import com.ditecting.honeyeye.pcap4j.extension.packet.FullPacket;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/6/4 19:40
 */
@Slf4j
public class Converter implements Runnable{
    private ConverterCachePool converterCachePool;
    private TransmitterCachePool transmitterCachePool;
    private OutputCachePool outputCachePool;
    private PluginCachePool pluginCachePool;
    private CountDownLatch countDownLatch;
    private int transmittingGrain;
    private int outputtingGrain;
    private int pluginGrain;
    private double transmittingTimeout;
    private double outputtingTimeout;
    private double pluginTimeout;

    public Converter(ConverterCachePool converterCachePool, TransmitterCachePool transmitterCachePool, OutputCachePool outputCachePool, PluginCachePool pluginCachePool, CountDownLatch countDownLatch, int transmittingGrain, int outputtingGrain, int pluginGrain, double transmittingTimeout, double outputtingTimeout, double pluginTimeout) {
        this.converterCachePool = converterCachePool;
        this.transmitterCachePool = transmitterCachePool;
        this.outputCachePool = outputCachePool;
        this.pluginCachePool = pluginCachePool;
        this.countDownLatch = countDownLatch;
        this.transmittingGrain = transmittingGrain;
        this.outputtingGrain = outputtingGrain;
        this.pluginGrain = pluginGrain;
        this.transmittingTimeout = transmittingTimeout;
        this.outputtingTimeout = outputtingTimeout;
        this.pluginTimeout = pluginTimeout;
    }

    @Override
    public void run() {
        log.info("Converter starts working.");
        try {
            while (true) {
                // get a PacketMeeting
                PacketMeeting pm;
                synchronized (converterCachePool) {
                    if (converterCachePool.isEmpty()) {
                        converterCachePool.wait();
                    }
                    pm = converterCachePool.remove();
                }

                // convert the PacketMeeting
                List<String> stringList;
                List<FullPacket> fullPacketList = pm.getFullPacketList();
                Map<FullPacket.Signature, List<FullPacket>> fullPacketPoolMap = ConverterUtil.fullPacketListToMap(fullPacketList);

                /* add the result to TransmitterCachePool*/
                if(transmitterCachePool != null){
                    switch (transmittingGrain){
                        case 1:
                            stringList = ConverterUtil.adaptPacket(fullPacketList);
                            break;
                        case 2:
                            stringList = ConverterUtil.adaptFlow(fullPacketPoolMap, transmittingTimeout);
                            break;
                        case 3:
                            stringList = ConverterUtil.adaptSession(fullPacketPoolMap, transmittingTimeout);
                            break;
                        default:
                            stringList = ConverterUtil.adaptRawPacket(fullPacketList);
                    }

                    synchronized (transmitterCachePool) {
                        if(converterCachePool.getStatus() && converterCachePool.isEmpty()){
                            transmitterCachePool.setStatus(true);
                        }
                        transmitterCachePool.addAll(stringList);
                        transmitterCachePool.notifyAll();
                    }
                }

                /* add the result to OutputCachePool*/
                if(outputCachePool != null){
                    switch (outputtingGrain){
                        case 1:
                            stringList = ConverterUtil.adaptPacket(fullPacketList);
                            break;
                        case 2:
                            stringList = ConverterUtil.adaptFlow(fullPacketPoolMap, outputtingTimeout);
                            break;
                        case 3:
                            stringList = ConverterUtil.adaptSession(fullPacketPoolMap, outputtingTimeout);
                            break;
                        default:
                            stringList = ConverterUtil.adaptRawPacket(fullPacketList);
                    }
                    synchronized (outputCachePool){
                        if(converterCachePool.getStatus() && converterCachePool.isEmpty()){
                            outputCachePool.setStatus(true);
                        }
                        outputCachePool.addAll(stringList);
                        outputCachePool.notifyAll();
                    }
                }

                /* add the result to PluginCachePool*/
                if(pluginCachePool != null){
                    switch (pluginGrain){
                        case 1:
                            stringList = ConverterUtil.adaptPacket(fullPacketList);
                            break;
                        case 2:
                            stringList = ConverterUtil.adaptFlow(fullPacketPoolMap, pluginTimeout);
                            break;
                        case 3:
                            stringList = ConverterUtil.adaptSession(fullPacketPoolMap, pluginTimeout);
                            break;
                        default:
                            stringList = ConverterUtil.adaptRawPacket(fullPacketList);
                    }

                    synchronized (pluginCachePool){
                        if(converterCachePool.getStatus() && converterCachePool.isEmpty()){
                            pluginCachePool.setStatus(true);
                        }
                        pluginCachePool.addAll(stringList, fullPacketList);
                        pluginCachePool.notifyAll();
                    }
                }

                if(converterCachePool.getStatus() && converterCachePool.isEmpty()){
                    countDownLatch.countDown();
                    log.info("Converter stops working.");
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}