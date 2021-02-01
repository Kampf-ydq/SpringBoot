package com.ditecting.honeyeye.listener;

import com.ditecting.honeyeye.cachepool.TransmitterCachePool;
import com.ditecting.honeyeye.outputer.transmitter.Transmitter;
import com.ditecting.honeyeye.pcap4j.extension.core.TsharkMappings;
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
@Slf4j
@Component
public class TransmittingListener{

    @Value("${honeyeye.system.inputingMode}")
    private int inputingMode;// 1:capture, 2:load

    @Value("${honeyeye.listener.transmittingListener.port}")
    private int port;

    @Value("${honeyeye.listener.transmittingListener.netAddress}")
    private String netAddress;

    /* outputing grain, including: -1:do not output, 0:rawPacket, 1:packet, 2:flow, 3:session */
    @Value("${honeyeye.listener.transmittingGrain}")
    private int transmittingGrain;

    @Autowired
    private TransmitterCachePool transmitterCachePool;

    public void gotTransmitterCachePool(TsharkMappings.PcapFileHeader pcapFileHeader, CountDownLatch countDownLatch){
        Transmitter transmitter = new Transmitter(port, netAddress, pcapFileHeader, transmittingGrain, transmitterCachePool, countDownLatch);
        Thread thread = new Thread(transmitter);
        thread.start();
    }

    //添加getter、setter
    public int getInputingMode() {
        return inputingMode;
    }

    public void setInputingMode(int inputingMode) {
        this.inputingMode = inputingMode;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getNetAddress() {
        return netAddress;
    }

    public void setNetAddress(String netAddress) {
        this.netAddress = netAddress;
    }

    public int getTransmittingGrain() {
        return transmittingGrain;
    }

    public void setTransmittingGrain(int transmittingGrain) {
        this.transmittingGrain = transmittingGrain;
    }

    @Override
    public String toString() {
        return "TransmittingListener{" +
                "inputingMode=" + inputingMode +
                ", port=" + port +
                ", netAddress='" + netAddress + '\'' +
                ", transmittingGrain=" + transmittingGrain +
                '}';
    }
}
