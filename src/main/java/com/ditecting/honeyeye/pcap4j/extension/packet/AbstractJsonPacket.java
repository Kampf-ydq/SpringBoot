package com.ditecting.honeyeye.pcap4j.extension.packet;

import org.pcap4j.packet.AbstractPacket;
import org.pcap4j.util.ByteArrays;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A Packet built by TShark have to extends this abstract class to distinguish itself from "previous" packets.
 *
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 19:44
 */
public abstract class AbstractJsonPacket extends AbstractPacket implements JsonPacket {

    private static final long serialVersionUID = -6719588265295624033L;

}