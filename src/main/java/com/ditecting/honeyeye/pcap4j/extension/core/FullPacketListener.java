package com.ditecting.honeyeye.pcap4j.extension.core;

import org.pcap4j.core.PacketListener;
import org.pcap4j.packet.Packet;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/2 10:25
 */
public interface FullPacketListener extends PacketListener{
    /**
     *continue dissecting packet with tshark which needs PcapFileHeader and PcapDataHeader
     *
     * @param packet
     * @param pcapDataHeader
     */
    public void gotFullPacket(Packet packet, TsharkMappings.PcapDataHeader pcapDataHeader);

}
