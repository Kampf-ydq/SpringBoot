package com.ditecting.honeyeye.pcap4j.extension.core;

import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.factory.PacketFactories;
import org.pcap4j.packet.namednumber.DataLinkType;
import org.pcap4j.util.ByteArrays;

import java.nio.ByteOrder;
import java.sql.Timestamp;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/2 15:33
 */
@Slf4j
public class FullPcapHandle extends PcapHandle {
    private final ThreadLocal<Timestamp> timestamps = new ThreadLocal<Timestamp>();
    private final ThreadLocal<Integer> originalLengths = new ThreadLocal<Integer>();
    private final ByteOrder byteOrder;

    public FullPcapHandle(Pointer handle, TimestampPrecision timestampPrecision, ByteOrder byteOrder) {
        super(handle, timestampPrecision);
        this.byteOrder = byteOrder;
    }

    public void loop(int packetCount, FullPacketListener listener) throws PcapNativeException, InterruptedException, NotOpenException {
        this.loop(packetCount, listener, FullPcapHandle.SimpleExecutor.INSTANCE);
    }

    public void loop(int packetCount, FullPacketListener listener, Executor executor) throws PcapNativeException, InterruptedException, NotOpenException {
        if (listener != null && executor != null) {
            this.doLoop(packetCount, new FullPcapHandle.GotFullPacketFuncExecutor(listener, this.getDlt(), executor));
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("listener: ").append(listener).append(" executor: ").append(executor);
            throw new NullPointerException(sb.toString());
        }
    }

    private final class GotFullPacketFuncExecutor implements NativeMappings.pcap_handler {
        private final DataLinkType dlt;
        private final FullPacketListener listener;
        private final Executor executor;

        public GotFullPacketFuncExecutor(FullPacketListener listener, DataLinkType dlt, Executor executor) {
            this.dlt = dlt;
            this.listener = listener;
            this.executor = executor;
        }

        public void got_packet(Pointer args, Pointer header, Pointer packet) {
            final Timestamp ts = FullPcapHandle.this.buildTimestamp(header);
            final int len = NativeMappings.pcap_pkthdr.getLen(header);
            final byte[] hd = header.getByteArray(0L, 16);
            final byte[] ba = packet.getByteArray(0L, NativeMappings.pcap_pkthdr.getCaplen(header));

            try {
                this.executor.execute(new Runnable() {
                    public void run() {
                        FullPcapHandle.this.timestamps.set(ts);
                        FullPcapHandle.this.originalLengths.set(len);
                        Packet p = (Packet) PacketFactories.getFactory(Packet.class, DataLinkType.class).newInstance(ba, 0, ba.length, GotFullPacketFuncExecutor.this.dlt);
                        TsharkMappings.PcapDataHeader pdh = new TsharkMappings.PcapDataHeader(hd);
//                        log.info("PcapDataHeader: " + ByteArrays.toHexString(pdh.toByteArray(), ""));
                        GotFullPacketFuncExecutor.this.listener.gotFullPacket(p, pdh);
                    }
                });
            } catch (Throwable var8) {
                log.error("The executor has thrown an exception.", var8);
            }

        }
    }

    private static final class SimpleExecutor implements Executor {
        private static final FullPcapHandle.SimpleExecutor INSTANCE = new FullPcapHandle.SimpleExecutor();

        private SimpleExecutor() {
        }

        public static FullPcapHandle.SimpleExecutor getInstance() {
            return INSTANCE;
        }

        public void execute(Runnable command) {
            command.run();
        }
    }

}