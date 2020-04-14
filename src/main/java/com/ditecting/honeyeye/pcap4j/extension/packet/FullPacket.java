package com.ditecting.honeyeye.pcap4j.extension.packet;

import com.ditecting.honeyeye.pcap4j.extension.core.TsharkMappings;
import com.ditecting.honeyeye.pcap4j.extension.packet.factory.JsonPacketFactories;
import com.ditecting.honeyeye.pcap4j.extension.packet.factory.JsonPacketFactory;
import com.ditecting.honeyeye.pcap4j.extension.utils.TsharkUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.TcpPort;
import org.pcap4j.packet.namednumber.UdpPort;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * a FullPacket consists of a packet and a pcapDataHeader
 *
 * @author CSheng
 * @version 1.0
 * @date 2020/4/3 18:55
 */
@Slf4j
@Getter
public class FullPacket implements Serializable{
    private static final long serialVersionUID = -8256169247076888862L;

    private final Packet packet;
    private final TsharkMappings.PcapDataHeader pcapDataHeader;
    private final Signature signature;
    private long sequence = -1;// -1 means unassigned value
    private boolean complete;

//    public static FullPacket newFullPacket(FullPacket fullPacket, TsharkMappings.PcapFileHeader pcapFileHeader){
//        if(!fullPacket.isComplete()){
//            rebuildFullPacket(fullPacket.getPacket(), fullPacket.getPcapDataHeader(), pcapFileHeader);
//        }
//        return fullPacket;
//    }

    public FullPacket(Packet packet, TsharkMappings.PcapDataHeader pcapDataHeader){
        if(!isDissectedAbsolutely(packet)){
            complete = false;
        }else {
            complete = true;
        }

        this.packet = packet;
        this.pcapDataHeader = pcapDataHeader;

        IpV4Packet ipV4Packet = null;
        for(Packet p : packet){
            if(p instanceof IpV4Packet){
                ipV4Packet = (IpV4Packet)p;
                break;
            }
        }

        if(ipV4Packet != null){
            signature = new Signature(ipV4Packet.getHeader().getSrcAddr(), ipV4Packet.getHeader().getDstAddr());
        }else {
            signature = null;
        }
    }

    public void setSequence (long seq){
        this.sequence = seq;
    }

    /**
     * Check whether raw packet has been dissected absolutely
     * or not with an unknownPacket that cannot be dissected.
     *
     * @param packet
     *
     * @return
     */
    private static boolean isDissectedAbsolutely(Packet packet){
        for(Packet p : packet){
            if(p instanceof UnknownPacket){
                return false;
            }
        }
        return true;
    }

    /**
     * deal with UnknownPacket by tshark and rebuild original packet with JsonPacket
     *
     * @param packet packet
     * @param pcapDataHeader pcapDataHeader
     * @param pcapFileHeader pcapFileHeader
     */
    private static void rebuildFullPacket (Packet packet, TsharkMappings.PcapDataHeader pcapDataHeader, TsharkMappings.PcapFileHeader pcapFileHeader) {
        String rawJsonPacket = generateRawJsonPacket(packet, pcapDataHeader, pcapFileHeader);

        rebuildFullPacket(packet, rawJsonPacket);
    }

    /**
     * rebuild original packet with JsonPacket
     *
     * @param packet packet
     * @param rawJsonPacket rawJsonPacket
     */
    public static void rebuildFullPacket (Packet packet,String rawJsonPacket) {
        Packet newPacket = null;
        IpV4Packet ipV4Packet = null;
        for(Packet p : packet){
            if(p instanceof IpV4Packet){
                ipV4Packet = (IpV4Packet)p;
                break;
            }
        }

        if(ipV4Packet != null){
            if(ipV4Packet.getPayload() instanceof TcpPacket){
                TcpPacket tcpPacket = (TcpPacket) ipV4Packet.getPayload();

                JsonPacketFactory<JsonPacket, TcpPort> factory = JsonPacketFactories.getJsonFactory(JsonPacket.class, TcpPort.class);
                Class<? extends JsonPacket> class4UnknownPort = factory.getTargetClass();
                Class<? extends JsonPacket> class4DstPort = factory.getTargetClass(tcpPacket.getHeader().getDstPort());
                TcpPort serverPort;
                if (class4DstPort.equals(class4UnknownPort)) {
                    serverPort = tcpPacket.getHeader().getSrcPort();
                } else {
                    serverPort = tcpPacket.getHeader().getDstPort();
                }

                newPacket = factory.newInstance(rawJsonPacket, findUnknownPacket(tcpPacket).getRawData(), serverPort);
                if(newPacket.getHeader() == null){//Header == null means unmatched
                    newPacket = UnknownJsonPacket.newPacket(rawJsonPacket, findUnknownPacket(tcpPacket).getRawData());
                }
            }

            if(ipV4Packet.getPayload() instanceof UdpPacket){
                UdpPacket udpPacket = (UdpPacket) ipV4Packet.getPayload();

                JsonPacketFactory<JsonPacket, UdpPort>  factory = JsonPacketFactories.getJsonFactory(JsonPacket.class, UdpPort.class);
                Class<? extends JsonPacket> class4UnknownPort = factory.getTargetClass();
                Class<? extends JsonPacket> class4DstPort = factory.getTargetClass(udpPacket.getHeader().getDstPort());
                UdpPort serverPort;
                if (class4DstPort.equals(class4UnknownPort)) {
                    serverPort = udpPacket.getHeader().getSrcPort();
                } else {
                    serverPort = udpPacket.getHeader().getDstPort();
                }

                newPacket = factory.newInstance(rawJsonPacket, findUnknownPacket(udpPacket).getRawData(), serverPort);
                if(newPacket.getHeader() == null){//Header == null means unmatched
                    newPacket = UnknownJsonPacket.newPacket(rawJsonPacket, findUnknownPacket(udpPacket).getRawData());
                }
            }

            /*use the newPacket to alter the unknownPacket */
            if(newPacket == null){
                log.warn("the packet contains non-tcp and non-udp transport layer data which cannot be dissected [" + packet.toString() + "]");
            }else{
                updateUnknownPacket(packet, newPacket);
            }
        }
    }

    /**
     * regenerate raw data of the packet and call tshark to dissect it to get raw jsonPacket.
     *
     * @param packet
     * @param pcapDataHeader
     * @return
     */
    private static String generateRawJsonPacket (Packet packet, TsharkMappings.PcapDataHeader pcapDataHeader, TsharkMappings.PcapFileHeader pcapFileHeader){
        int pcapLength = 0;

        byte[] pfhArray = pcapFileHeader.toByteArray();
        pcapLength += pfhArray.length;
//        log.info("pcapFileHeader: " + ByteArrays.toHexString(pfhArray, " "));

        byte[] pdhArray = pcapDataHeader.toByteArray();
        pcapLength += pdhArray.length;
//        log.info("pcapDataHeader: " + ByteArrays.toHexString(pdhArray, " "));

        byte[] rawPacketData = packet.getRawData();
        pcapLength += rawPacketData.length;
//        log.info("rawPacketData: " + ByteArrays.toHexString(rawPacketData, " "));

        byte[] rawData = new byte[pcapLength];
        int countLength = 0;
        System.arraycopy(pfhArray, 0, rawData, countLength, pfhArray.length);
        countLength += pfhArray.length;
        System.arraycopy(pdhArray, 0, rawData, countLength, pdhArray.length);
        countLength += pdhArray.length;
        System.arraycopy(rawPacketData, 0, rawData, countLength, rawPacketData.length);

        return TsharkUtils.executeTshark(rawData);
    }

    private static Packet findUnknownPacket (Packet packet){
        for(Packet p : packet) {
            if (p instanceof UnknownPacket) {
                return p;
            }
        }
        return null;
    }

    /**
     * replace the nknownPacket in the packet with the newPacket by reflection
     *
     * @param packet packet
     * @param newPacket newPacket
     */
    private static void updateUnknownPacket (Packet packet, Packet newPacket){
        Packet parent = packet;

        if(packet instanceof UnknownPacket){
            return;
        }

        for(Packet p : packet){
            if(p instanceof UnknownPacket){
                try {
                    Field payloadField  =  parent.getClass().getDeclaredField("payload");
                    payloadField.setAccessible(true);
                    payloadField.set(parent, newPacket);
                    return;
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return ;
            }
            parent = p;
        }
    }

    @Getter
    public static final class Signature{
        private final Inet4Address srcV4Addr;
        private final Inet4Address dstV4Addr;

        public Signature(InetAddress srcAddr, InetAddress dstAddr){
                srcV4Addr = (Inet4Address) srcAddr;
                dstV4Addr = (Inet4Address) dstAddr;
        }

        /**
         * ignore address order
         * @param obj
         * @return
         */
        @Override
        public boolean equals(Object obj){
            if(this == obj){
                return true;
            }

            if(obj == null){
                return false;
            }

            if(obj instanceof Signature){
                Signature other = (Signature) obj;
                    if(srcV4Addr.equals(other.srcV4Addr) && dstV4Addr.equals(other.dstV4Addr)){
                        return true;
                    }
                    if(srcV4Addr.equals(other.dstV4Addr) && dstV4Addr.equals(other.srcV4Addr)){
                        return true;
                    }
            }

            return false;
        }

        @Override
        public int hashCode() {
            int hashcode = 1;
                if(srcV4Addr.hashCode() < dstV4Addr.hashCode()){
                    hashcode = 31*hashcode + srcV4Addr.hashCode();
                    hashcode = 31*hashcode + dstV4Addr.hashCode();
                }else {
                    hashcode = 31*hashcode + dstV4Addr.hashCode();
                    hashcode = 31*hashcode + srcV4Addr.hashCode();
                }
            return hashcode;
        }

    }
}