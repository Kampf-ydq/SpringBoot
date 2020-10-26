package com.ditecting.honeyeye.converter.utils;

import com.ditecting.honeyeye.converter.datatype.PacketFlow;
import com.ditecting.honeyeye.pcap4j.extension.packet.FullPacket;
import com.ditecting.honeyeye.pcap4j.extension.utils.PacketUtil;
import lombok.NonNull;
import org.pcap4j.packet.IpV4Packet;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/23 17:00
 */
public class FlowUtil {

    public static PacketFlow generatePacketFlow (FullPacket fp, double timeout) {
        IpV4Packet ipV4Packet = PacketUtil.getIpV4Packet(fp);

        String srcIp = ipV4Packet.getHeader().getSrcAddr().getHostAddress();
        String dstIp = ipV4Packet.getHeader().getDstAddr().getHostAddress();
        String signature = srcIp.compareTo(dstIp) <= 0 ? srcIp+ ":" +dstIp : dstIp+ ":" +srcIp;
        FullPacket.SubSignature subSignature = PacketUtil.getSubSignature(ipV4Packet);
        int packetNum = 1;
        int packetSize = ipV4Packet.getHeader().getTotalLengthAsInt();

        PacketFlow packetFlow = PacketFlow.builder()
                .signature(signature)
                .timeout(timeout)
                .srcIp(srcIp)
                .dstIp(dstIp)
                .prot(subSignature.getProt())
                .srcPort(subSignature.getSrcPort())
                .dstPort(subSignature.getDstPort())
                .packetNum(packetNum)
                .packetSize(packetSize)
                .startTime(fp.getPcapDataHeader().getTime())
                .endTime(fp.getPcapDataHeader().getTime())
                .duration(0).build();

        packetFlow.addSegment(PacketUtil.packetToSegment(fp));

        return packetFlow;
    }

    public static void extendPacketFlow(@NonNull PacketFlow pf, FullPacket fp){
        IpV4Packet ipV4Packet = PacketUtil.getIpV4Packet(fp);
        pf.setEndTime(fp.getPcapDataHeader().getTime());
        pf.setDuration(pf.getEndTime() - pf.getStartTime());
        pf.setPacketNum(pf.getPacketNum() + 1);
        pf.setPacketSize(pf.getPacketSize() + ipV4Packet.getHeader().getTotalLengthAsInt());
        pf.addSegment(PacketUtil.packetToSegment(fp));
    }
}