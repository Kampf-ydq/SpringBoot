package com.ditecting.honeyeye.capture;

import com.ditecting.honeyeye.convert.FlowConverter;
import com.ditecting.honeyeye.convert.PacketConverter;
import com.ditecting.honeyeye.convert.RawPacketConverter;
import com.ditecting.honeyeye.convert.SessionConverter;
import lombok.SneakyThrows;
import org.pcap4j.core.PacketListener;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ConvertListener implements PacketListener{
    private static final Logger logger = LoggerFactory.getLogger(ConvertListener.class);

    @Value("${capture.ConvertListener.convertGrain}")
    private int convertGrain;

    @Autowired
    private RawPacketConverter rawPacketConverter;

    @Autowired
    private PacketConverter packetConverter;

    @Autowired
    private FlowConverter flowConverter;

    @Autowired
    private SessionConverter sessionConverter;

    @Override
    public void gotPacket(Packet packet) {//observer mode, call back to gotPacket() to process the packet content after capturing it
        System.out.println(packet);

        switch (convertGrain){
            case 1:
                packetConverter.executeAsync();
                break;
            case 2:
                flowConverter.executeAsync();
                break;
            case 3:
                sessionConverter.executeAsync();
                break;
            default:
                rawPacketConverter.executeAsync();
        }
    }
}
