package com.ditecting.honeyeye.pcap4j.extension.utils;

import com.ditecting.honeyeye.converter.session.PacketSession;
import com.ditecting.honeyeye.pcap4j.extension.packet.FullPacket;
import com.ditecting.honeyeye.pcap4j.extension.packet.JsonPacket;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/15 20:50
 */
@Slf4j
public class PacketUtil {

    public static IpV4Packet getIpV4Packet (FullPacket fp) {
        IpV4Packet ipV4Packet = null;
        for(Packet p : fp.getPacket()){
            if(p instanceof IpV4Packet){
                ipV4Packet = (IpV4Packet)p;
                break;
            }
        }

        if(ipV4Packet == null){
            throw new NullPointerException("ipV4Packet is null in FullPacket");
        }
        return ipV4Packet;
    }

    public static String tcpToSegement (TcpPacket tcpPacket){
        TcpPacket.TcpHeader tcpHeader = tcpPacket.getHeader();
        StringBuilder sb = new StringBuilder();
        sb.append("\"tcpHeader\": {");
        sb.append("\"srcPort\": ").append(Short.toUnsignedInt(tcpHeader.getSrcPort().value())).append(",");
        sb.append("\"dstPort\": ").append(Short.toUnsignedInt(tcpHeader.getDstPort().value())).append(",");
        sb.append("\"seqNumber\": ").append(tcpHeader.getSequenceNumberAsLong()).append(",");
        sb.append("\"ackNumber\": ").append(tcpHeader.getAcknowledgmentNumberAsLong()).append(",");
        sb.append("\"dataOffset\": ").append(tcpHeader.getDataOffsetAsInt()).append(",");
        sb.append("\"URG\": ").append(tcpHeader.getUrg()).append(",");
        sb.append("\"ACK\": ").append(tcpHeader.getAck()).append(",");
        sb.append("\"PSH\": ").append(tcpHeader.getPsh()).append(",");
        sb.append("\"RST\": ").append(tcpHeader.getRst()).append(",");
        sb.append("\"SYN\": ").append(tcpHeader.getSyn()).append(",");
        sb.append("\"FIN\": ").append(tcpHeader.getFin()).append(",");
        sb.append("\"Window\": ").append(tcpHeader.getWindowAsInt());
        sb.append("}");
        return sb.toString();
    }

    public static String udpToSegement (UdpPacket udpPacket){
        UdpPacket.UdpHeader udpHeader = udpPacket.getHeader();
        StringBuilder sb = new StringBuilder();
        sb.append("\"udpHeader\": {");
        sb.append("\"srcPort\": ").append(Short.toUnsignedInt(udpHeader.getSrcPort().value())).append(",");
        sb.append("\"detPort\": ").append(Short.toUnsignedInt(udpHeader.getDstPort().value())).append(",");
        sb.append("\"Length\": ").append(udpHeader.getLengthAsInt());
        sb.append("}");
        return sb.toString();
    }

    public static String packetToSegment( IpV4Packet ipV4Packet ) {
        StringBuilder sb = new StringBuilder();

        /* Network layer */
        sb.append("\"srcIp\": \"" + ipV4Packet.getHeader().getSrcAddr().getHostAddress() + "\", ");
        sb.append("\"dstIp\": \"" + ipV4Packet.getHeader().getDstAddr().getHostAddress() + "\"");

        /* Transport layer*/
        if(ipV4Packet.getPayload() == null){
            return sb.toString();
        }
        if(ipV4Packet.getPayload() instanceof TcpPacket){
            sb.append(" ,");
            sb.append(tcpToSegement((TcpPacket) ipV4Packet.getPayload()));
        }
        if(ipV4Packet.getPayload() instanceof UdpPacket){
            sb.append(" ,");
            sb.append(udpToSegement((UdpPacket) ipV4Packet.getPayload()));
        }

        /* Application layer*/
        if(ipV4Packet.getPayload().getPayload() == null){
            return sb.toString();
        }
        sb.append(" ,");
        sb.append(ipV4Packet.getPayload().getPayload().toString());

//        logger.info(sb.toString());
        return sb.toString();
    }

    public static Map<String, String> getTopProtocolFromPacket(Packet packet){
        int layer = 2;
        Packet endPacket = null;
        for(Packet p : packet){
            layer ++;
            endPacket = p;
        }
        String name;
        if(endPacket instanceof JsonPacket){
            name = endPacket.getClass().getName().replace(endPacket.getClass().getPackage().getName() + ".", "");
            name = name.replace("JsonPacket", "");
        }else {
            name = endPacket.getClass().getName().replace(endPacket.getClass().getPackage().getName() + ".", "");
            name = name.replace("Packet", "");
        }

        Map<String, String> result = new HashMap<String, String>();
        result.put("layer", layer+"");
        result.put("name", name);
        return result;
    }

    public static String fullPacketToJson(FullPacket fullPacket){//TODO test
        double myTime = fullPacket.getPcapDataHeader().getTime();
        String packetJson = packetToSegment(getIpV4Packet(fullPacket));
        StringBuilder sb  = new StringBuilder();
        sb.append("\"time\": " + PacketSession.formatDouble(myTime) + ", ");
        sb.append(packetJson);
        return sb.toString();
    }
}

