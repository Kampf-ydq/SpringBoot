package com.ditecting.honeyeye.outputer.transmitter;

import com.ditecting.honeyeye.cachepool.TransmitterCachePool;
import com.ditecting.honeyeye.pcap4j.extension.core.TsharkMappings;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

import static java.lang.System.currentTimeMillis;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/6/4 14:35
 */
@Slf4j
public class Transmitter implements Runnable{
    private TransmitterCachePool transmitterCachePool;
    private int port;
    private String netAddress;
    private TsharkMappings.PcapFileHeader pcapFileHeader;
    private int transmittingGrain;
    private boolean start = false;
    private CountDownLatch countDownLatch;
    private NettyClient nettyClient;

    public Transmitter(int port, String netAddress, TsharkMappings.PcapFileHeader pcapFileHeader, int transmittingGrain, TransmitterCachePool transmitterCachePool, CountDownLatch countDownLatch) {
        this.port = port;
        this.netAddress = netAddress;
        this.pcapFileHeader = pcapFileHeader;
        this.transmittingGrain = transmittingGrain;
        this.transmitterCachePool = transmitterCachePool;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {//TODO test
        log.info("Transmitter starts working.");
        try {
            nettyClient = new NettyClient(netAddress, port);
            while (true) {
                String string;
                synchronized (transmitterCachePool) {
                    if (transmitterCachePool.isEmpty()) {
                        if(!transmitterCachePool.getStatus()){
                            transmitterCachePool.wait();
                        }else {
                            log.info("Transmitter stops working.");
                            break;
                        }
                    }
                    string = transmitterCachePool.remove();
                }
                /* transmit strings*/
                if (string != null) {
                    if(transmittingGrain == 0) {
                        StringBuilder rawDataSB = new StringBuilder();
                        rawDataSB.append("{");
                        if(!start){
                            log.info("Start transmitting ["+ currentTimeMillis() +"].");
                            if(pcapFileHeader == null){
                                throw new NullPointerException("pcapFileHeader is null.");
                            }
                            byte[] pfhArray = pcapFileHeader.toByteArray();
                            rawDataSB.append("\"pfhArray\": \"" + new String(pfhArray) + "\"");
                            rawDataSB.append(", \"dataArray\": \"" + string + "\"");
                            rawDataSB.append("}");
                            nettyClient.start(rawDataSB.toString());
                            start = true;
                        }else {
                            rawDataSB.append("\"dataArray\": \"" + string + "\"");
                            rawDataSB.append("}");
                            nettyClient.start(rawDataSB.toString());
                        }
                    }else {
                        nettyClient.start(string);
                    }
                }
            }
            nettyClient.close();
            log.info("Finish transmitting ["+ currentTimeMillis() +"].");
            if(countDownLatch != null){
                countDownLatch.countDown();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}