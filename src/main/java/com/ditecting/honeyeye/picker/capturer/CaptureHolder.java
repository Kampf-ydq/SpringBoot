package com.ditecting.honeyeye.picker.capturer;

import com.ditecting.honeyeye.listener.CapturingListener;
import com.ditecting.honeyeye.pcap4j.extension.core.FullPcapHandle;
import com.ditecting.honeyeye.pcap4j.extension.core.TsharkMappings;
import com.ditecting.honeyeye.pcap4j.extension.packet.pool.FullPacketPool;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.Objects;

/**
 * CaptureHolder ByteOrder is consistent with Network ByteOrder.
 *
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@Slf4j
@Component
public class CaptureHolder {
    private PcapNetworkInterface nif;
    private FullPcapHandle handle = null;

    @Autowired
    private MyPcapNetworkInterface myPcapNetworkInterface;

    @Autowired
    private CapturingListener capturingListener;

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

    public void capture() throws PcapNativeException, NotOpenException, IOException {

        generateHandle();

        generatePcapFile();

        if (filter.length() != 0) {
            handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);
        }

        try {
            log.info("start capturing NIF [" + nif.getName() + "].");
            handle.loop(count, capturingListener);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        handle.close();
    }

    private void generateHandle(){
        try {
            PcapHandle ph = nif.openLive(snaplen, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout);

            if(FullPacketPool.pcapFileHeader.get() == null){
                FullPacketPool.pcapFileHeader.set(TsharkMappings.generateDefaultPcapFileHeader());
            }

            handle = new FullPcapHandle(ph.getHandle(), ph.getTimestampPrecision(), ByteOrder.LITTLE_ENDIAN);
        } catch (PcapNativeException e) {
            e.printStackTrace();
        }
    }

    private void generatePcapFile () throws IOException {
        String tempName = (new Date()).getTime() + "";
        File tempPcapFile =  File.createTempFile(tempName, ".pcap");
        FileOutputStream fos = new FileOutputStream(tempPcapFile);
        fos.write(FullPacketPool.pcapFileHeader.get().toByteArray());
        fos.flush();
        fos.close();

        if(FullPacketPool.pcapFile.get() == null){
            FullPacketPool.pcapFile.set(tempPcapFile);
        }
    }

}
