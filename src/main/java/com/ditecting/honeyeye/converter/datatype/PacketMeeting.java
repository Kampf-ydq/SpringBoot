package com.ditecting.honeyeye.converter.datatype;

import com.ditecting.honeyeye.pcap4j.extension.packet.FullPacket;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/6/2 10:51
 */
@Builder
@Setter
@Getter
public class PacketMeeting {
    private double startTime;
    private double endTime;
    private long startNumber;
    private long endNumber;
    private double duration;
    private long appCount; // number of application-layer items
    private List<FullPacket> fullPacketList;

    public void addFullPacket (FullPacket fullPacket){
        if(fullPacketList == null){
            fullPacketList = new ArrayList<>();
        }
        fullPacketList.add(fullPacket);
    }

}