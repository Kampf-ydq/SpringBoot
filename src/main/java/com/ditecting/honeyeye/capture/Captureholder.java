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

@Component
public class Captureholder {
    private static final Logger logger = LoggerFactory.getLogger(Captureholder.class);
    private PcapNetworkInterface nif;

    @Autowired
    private MyPcapNetworkInterface myPcapNetworkInterface;

    @Autowired
    private PacketListener convertListener;

    @Value("${capture.Captureholder.count}")
    private int count; //maximum number of captured packet, -1 means infinite

    @Value("${capture.Captureholder.readTimeout}")
    private int readTimeout; //read timeout [ms]

    @Value("${capture.Captureholder.snaplen}")
    private int snaplen; //number of bytes captured for each packet [bytes]

    @Value("${capture.Captureholder.filter}")
    String filter; //filter condition for capture, which is consistent with filter rules in wireshark

    public Captureholder(){}

    @PostConstruct
    private void init() throws IOException {
        nif = Objects.requireNonNull(myPcapNetworkInterface.getMyPcapNetworkInterface(), "NIF cannot be NULL");
    }

    public void capture() throws PcapNativeException, NotOpenException{
        //instantiate a packet capturing object with length, promiscuous mode and timeout
        final PcapHandle handle = nif.openLive(snaplen, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout);

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
