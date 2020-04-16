package com.ditecting.honeyeye.converter.session;

import com.ditecting.honeyeye.pcap4j.extension.packet.FullPacket;
import com.ditecting.honeyeye.pcap4j.extension.utils.PacketUtil;
import lombok.NonNull;
import org.pcap4j.packet.IpV4Packet;

import java.util.Map;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/15 21:03
 */
public class SessionUtil {

    public static PacketSession generatePacketSession (FullPacket fp, double timeout) {
        IpV4Packet ipV4Packet = PacketUtil.getIpV4Packet(fp);

        String srcIp = ipV4Packet.getHeader().getSrcAddr().getHostAddress();
        String dstIp = ipV4Packet.getHeader().getDstAddr().getHostAddress();
        String signature = srcIp.compareTo(dstIp) <= 0 ? srcIp+ ":" +dstIp : dstIp+ ":" +srcIp;
        PacketSession packetSession = PacketSession.builder().signature(signature)
                .timeout(timeout)
                .startTime(fp.getPcapDataHeader().getTime())
                .srcIp(srcIp)
                .dstIp(dstIp)
                .complete(false).build();

        Map<String, String> topProtocol = PacketUtil.getTopProtocolFromPacket(ipV4Packet);
        int layer = Integer.parseInt(topProtocol.get("layer"));
        String name = topProtocol.get("name");
        packetSession.addProtocol(layer, name);

        packetSession.addSegment(PacketUtil.packetToSegment(ipV4Packet));


        return packetSession;
    }

    public static void extendPacketSession(@NonNull PacketSession ps, FullPacket fp){
        IpV4Packet ipV4Packet = PacketUtil.getIpV4Packet(fp);

        Map<String, String> topProtocol = PacketUtil.getTopProtocolFromPacket(ipV4Packet);
        int layer = Integer.parseInt(topProtocol.get("layer"));
        String name = topProtocol.get("name");
        ps.addProtocol(layer, name);

        ps.addSegment(PacketUtil.packetToSegment(ipV4Packet));
    }

    public static void endPacketSession(@NonNull PacketSession ps, double endTime){
        ps.setEndTime(endTime);
        ps.setComplete(true);
    }
}