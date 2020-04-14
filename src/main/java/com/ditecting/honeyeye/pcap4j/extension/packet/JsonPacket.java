package com.ditecting.honeyeye.pcap4j.extension.packet;

import org.pcap4j.packet.Packet;

/**
 *  * When pcap4j cannot dissect following payloads, honeyeye will call TShark to proceed. TShark will return results in json.
 * So the following sub-packets have to store information in json. This interface will be used to identify the type of a packet
 * (packet ot jsonpacket), which will be processed by different ways.
 *
 *
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 19:16
 */

public interface JsonPacket extends Packet {


}
