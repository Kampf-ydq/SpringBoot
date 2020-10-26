package com.ditecting.honeyeye.cachepool;

import com.ditecting.honeyeye.converter.utils.MeetingUtil;
import com.ditecting.honeyeye.converter.datatype.PacketMeeting;
import com.ditecting.honeyeye.inputer.PacketCounter;
import com.ditecting.honeyeye.pcap4j.extension.core.TsharkMappings;
import com.ditecting.honeyeye.pcap4j.extension.packet.FullPacket;
import com.ditecting.honeyeye.pcap4j.extension.utils.FullPacketUtil;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.packet.Packet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.currentTimeMillis;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/6/3 10:28
 */
@Slf4j
@Component
public class InputCachePool {
    private PacketMeeting waitingMeeting = null;
    private List<FullPacket> incompleteWaitingFullPacketList = null;
    private TsharkMappings.PcapFileHeader pcapFileHeader = null;
    private long globalNumber = 1;
    private boolean status = false; //false:continue, true:end

    @Value("${honeyeye.listener.meetingTimeout}")
    private double meetingTimeout; //[s]

    @Value("${honeyeye.listener.segmentMax}")
    private int segmentMax;

    @Autowired
    private ConverterCachePool converterCachePool;

    @Autowired
    private PacketCounter packetCounter;

    /**
     * reset the globalNumber to 1
     */
    public void resetGlobalNumber (){
        this.globalNumber = 1;
    }

    public TsharkMappings.PcapFileHeader getPcapFileHeader() {
        return pcapFileHeader;
    }

    public void setPcapFileHeader (TsharkMappings.PcapFileHeader pfh) {
        pcapFileHeader = pfh;
    }

    public void addFullPacket (Packet packet, TsharkMappings.PcapDataHeader pcapDataHeader) {
        FullPacket fullPacket = new FullPacket(globalNumber++, packet, pcapDataHeader);

        if(!fullPacket.isComplete()){
            if(incompleteWaitingFullPacketList == null){
                incompleteWaitingFullPacketList = new ArrayList<>();
            }
            incompleteWaitingFullPacketList.add(fullPacket);
        }else {
            packetCounter.count(packet);
        }

        if(waitingMeeting == null) {
            waitingMeeting = MeetingUtil.generatePacketMeeting(fullPacket);
            return;
        }

        double myTime = fullPacket.getPcapDataHeader().getTime();

        if((myTime-waitingMeeting.getEndTime()) < meetingTimeout){//continue current meeting
            MeetingUtil.extendPacketMeeting(waitingMeeting, fullPacket);
        }else{//break current meeting
            closeWaitingMeeting();

            waitingMeeting = MeetingUtil.generatePacketMeeting(fullPacket);
        }
    }

    public synchronized int monitorWaitingMeeting () {
        if(waitingMeeting == null){
            return 0;
        } else if ((System.currentTimeMillis() - waitingMeeting.getEndTime()*1000) > meetingTimeout*1000){
            closeWaitingMeeting();
            return 1;
        }else {
            return -1;
        }
    }

    /**
     * end the last WaitingMeeting
     */
    public void endLastWaitingMeeting () {
        status = true;
        if(waitingMeeting != null){
            closeWaitingMeeting();
        }
    }

    /**
     * close a WaitingMeeting
     */
    public void closeWaitingMeeting () {
        if(incompleteWaitingFullPacketList != null){
            waitingMeeting.setAppCount(incompleteWaitingFullPacketList.size());
            if (pcapFileHeader == null) {
                throw new NullPointerException("pcapFileHeader is null.");
            }
            FullPacketUtil.riffleFullPacketPool(incompleteWaitingFullPacketList, pcapFileHeader.toByteArray(), segmentMax, packetCounter);
            log.info("A PacketMeeting has been parsed ["+ currentTimeMillis() +"].");
        }

//        log.info("Output a new PacketMeeting");
        synchronized (converterCachePool) {
            if(status){
                converterCachePool.setStatus(true);
            }
            converterCachePool.add(waitingMeeting);
            converterCachePool.notify();
        }

        waitingMeeting = null;
        incompleteWaitingFullPacketList = null;
    }
}