package com.ditecting.honeyeye.pcap4j.extension.utils;

import com.ditecting.honeyeye.pcap4j.extension.packet.FullPacket;
import com.ditecting.honeyeye.pcap4j.extension.packet.JsonPacket;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/15 20:50
 */
@Slf4j
public class PacketUtil {

    public static FullPacket.SubSignature getSubSignature(IpV4Packet ipV4Packet){
        int prot = 0;
        int srcPort = 0;
        int dstPort = 0;
        if(ipV4Packet.getPayload() instanceof TcpPacket){
            TcpPacket tp = (TcpPacket) ipV4Packet.getPayload();
            prot = 1;
            srcPort = Short.toUnsignedInt(tp.getHeader().getSrcPort().value());
            dstPort = Short.toUnsignedInt(tp.getHeader().getDstPort().value());
        }
        if(ipV4Packet.getPayload() instanceof UdpPacket){
            UdpPacket up = (UdpPacket) ipV4Packet.getPayload();
            prot = 2;
            srcPort = Short.toUnsignedInt(up.getHeader().getSrcPort().value());
            dstPort = Short.toUnsignedInt(up.getHeader().getDstPort().value());
        }

        return new FullPacket.SubSignature(prot, srcPort, dstPort);
    }

    public static IpV4Packet getIpV4Packet (FullPacket fp) {
        IpV4Packet ipV4Packet = null;
        for(Packet p : fp.getPacket()){
            if(p instanceof IpV4Packet){
                ipV4Packet = (IpV4Packet)p;
                break;
            }
        }

        if(ipV4Packet == null){
            log.warn("ipV4Packet is null in FullPacket");
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
        sb.append("\"dstPort\": ").append(Short.toUnsignedInt(udpHeader.getDstPort().value())).append(",");
        sb.append("\"Length\": ").append(udpHeader.getLengthAsInt());
        sb.append("}");
        return sb.toString();
    }

    public static String packetToSegment(FullPacket fp) {
        double myTime = fp.getPcapDataHeader().getTime();

        IpV4Packet ipV4Packet =  getIpV4Packet(fp);
        int mySize = ipV4Packet.getHeader().getTotalLengthAsInt();

        StringBuilder sb = new StringBuilder();
        /* Network layer */
        sb.append("{");
        sb.append("\"number\": " + fp.getNumber() + ", ");
        sb.append("\"time\": " + formatDouble(myTime) + ", "); //TODO test
        sb.append("\"size\": " + mySize + ", "); //TODO test
        sb.append("\"srcIp\": \"" + ipV4Packet.getHeader().getSrcAddr().getHostAddress() + "\", ");
        sb.append("\"dstIp\": \"" + ipV4Packet.getHeader().getDstAddr().getHostAddress() + "\"");

        /* Transport layer*/
        if(ipV4Packet.getPayload() == null){
            sb.append("}");
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
            sb.append("}");
            return sb.toString();
        }
        /* Application layer JsonPacket*/
        if(! (ipV4Packet.getPayload().getPayload() instanceof JsonPacket)){
            sb.append("}");
            return sb.toString();
        }
        sb.append(" ,");
        sb.append(ipV4Packet.getPayload().getPayload().toString());
        sb.append("}");
//        logger.info(sb.toString());
        return sb.toString();
    }

    public static String segmentListToJsonString (List<String> segmentList) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(String str : segmentList){
            sb.append(str);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");

        return "\"segments\": " + sb.toString();
    }

    public static Map<String, String> getTopProtocolFromPacket(Packet packet){
        int layer = 2;
        Map<String, String> result = new HashMap<String, String>();
        if(packet == null){
            result.put("layer", layer+"");
            result.put("name", "Ethernet");
            return result;
        }

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

        result.put("layer", layer+"");
        result.put("name", name);
        return result;
    }

    /**
     * format fullPacket into Json type
     *
     * @param fullPacket
     * @return
     */
    public static String fullPacketToJson(FullPacket fullPacket){
        double myTime = fullPacket.getPcapDataHeader().getTime();
        String packetJson = packetToSegment(fullPacket);
        StringBuilder sbPacket = new StringBuilder(packetJson);
        sbPacket.deleteCharAt(sbPacket.indexOf("{"));
        sbPacket.deleteCharAt(sbPacket.lastIndexOf("}"));

        StringBuilder sb  = new StringBuilder();
        sb.append("{");
        sb.append("\"time\": " + formatDouble(myTime) + ", ");
        sb.append(sbPacket.toString());
        sb.append("}");
        return sb.toString();
    }

    /**
     * extract raw packet data from fullPacket
     *
     * @param fullPacket
     * @return
     */
    public static byte[] fullPacketToRawPacket(FullPacket fullPacket){
        int pcapLength = 0;
        byte[] pdhArray = fullPacket.getPcapDataHeader().toByteArray();
        pcapLength += pdhArray.length;
        byte[] rawPacketData = fullPacket.getPacket().getRawData();
        pcapLength += rawPacketData.length;
        byte[] rawData = new byte[pcapLength];

        int countLength = 0;
        System.arraycopy(pdhArray, 0, rawData, countLength, pdhArray.length);
        countLength += pdhArray.length;
        System.arraycopy(rawPacketData, 0, rawData, countLength, rawPacketData.length);

        return rawData;
    }

    /**
     * format double data
     *
     * @param d
     * @return
     */
    public static String formatDouble(double d) {
        NumberFormat nf = NumberFormat.getInstance();
//        nf.setMaximumFractionDigits(20);
        nf.setGroupingUsed(false);
        return nf.format(d);
    }
}

