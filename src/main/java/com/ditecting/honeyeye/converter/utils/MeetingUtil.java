package com.ditecting.honeyeye.converter.utils;

import com.ditecting.honeyeye.converter.datatype.PacketMeeting;
import com.ditecting.honeyeye.pcap4j.extension.packet.FullPacket;
import com.ditecting.honeyeye.pcap4j.extension.utils.PacketUtil;
import lombok.NonNull;
import org.pcap4j.packet.IpV4Packet;

import java.util.Map;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/6/2 11:42
 */
public class MeetingUtil {

    public static PacketMeeting generatePacketMeeting (FullPacket fp) {
        PacketMeeting packetMeeting = PacketMeeting.builder()
                .startTime(fp.getPcapDataHeader().getTime())
                .endTime(fp.getPcapDataHeader().getTime())
                .startNumber(fp.getNumber())
                .endNumber(fp.getNumber())
                .duration(0)
                .appCount(0).build();

        IpV4Packet ipV4Packet = PacketUtil.getIpV4Packet(fp);
        Map<String, String> topProtocol = PacketUtil.getTopProtocolFromPacket(ipV4Packet);
        int layer = Integer.parseInt(topProtocol.get("layer"));
        String name = topProtocol.get("name");
        if(layer > 4){
            packetMeeting.setAppCount(1);
        }

        packetMeeting.addFullPacket(fp);

        return packetMeeting;
    }


    public static void extendPacketMeeting(@NonNull PacketMeeting pm, FullPacket fp){
        pm.setEndTime(fp.getPcapDataHeader().getTime());
        pm.setEndNumber(fp.getNumber());
        pm.setDuration(pm.getEndTime()-pm.getStartTime());

        IpV4Packet ipV4Packet = PacketUtil.getIpV4Packet(fp);
        Map<String, String> topProtocol = PacketUtil.getTopProtocolFromPacket(ipV4Packet);
        int layer = Integer.parseInt(topProtocol.get("layer"));
        String name = topProtocol.get("name");
        if(layer > 4){
            pm.setAppCount(pm.getAppCount()+1);
        }

        pm.addFullPacket(fp);
    }
}