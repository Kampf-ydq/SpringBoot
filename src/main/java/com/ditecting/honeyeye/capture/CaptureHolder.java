package com.ditecting.honeyeye.capture;

import org.pcap4j.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Objects;

/**
 * CaptureHolder ByteOrder is consistent with Network ByteOrder.
 *
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@Component
public class CaptureHolder {
    private static final Logger logger = LoggerFactory.getLogger(CaptureHolder.class);
    private PcapNetworkInterface nif;
    private PcapHandle handle = null;

    @Autowired
    private MyPcapNetworkInterface myPcapNetworkInterface;

    @Autowired
    private PacketListener convertListener;

    @Value("${capture.captureHolder.count}")
    private int count; //maximum number of captured packet, -1 means infinite

    @Value("${capture.captureHolder.readTimeout}")
    private int readTimeout; //read timeout [ms]

    @Value("${capture.captureHolder.snaplen}")
    private int snaplen; //number of bytes captured for each packet [bytes]

    @Value("${capture.captureHolder.filter}")
    String filter; //filter condition for capture, which is consistent with filter rules in wireshark

    public CaptureHolder(){}

    @PostConstruct
    private void init() throws IOException {
        nif = Objects.requireNonNull(myPcapNetworkInterface.getMyPcapNetworkInterface(), "NIF cannot be NULL");
    }

    public PcapHandle getHandle() {
        return handle;
    }

    public void capture() throws PcapNativeException, NotOpenException{
        //instantiate a packet capturing object with length, promiscuous mode and timeout
        handle = nif.openLive(snaplen, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout);

        //create filter which is consistent with filter rules in wireshark
        if (filter.length() != 0) {
            handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);
        }

        // use loop() to cycle to capture packet with  COUNT, -1 means infinite loop
        try {
            logger.info("start capturing NIF [" + nif.getName() + "].");
            handle.loop(count, convertListener);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handle.close();
    }
}
