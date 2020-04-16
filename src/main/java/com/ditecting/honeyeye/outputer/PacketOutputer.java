package com.ditecting.honeyeye.outputer;

import com.ditecting.honeyeye.pcap4j.extension.packet.FullPacket;
import com.ditecting.honeyeye.pcap4j.extension.packet.pool.FullPacketPool;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/15 21:21
 */
@Component
public class PacketOutputer {

    public void output(){
        FullPacket fullPacket = FullPacketPool.currentFullPacket.get();

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
        try {
            FileOutputStream fos = new FileOutputStream(FullPacketPool.pcapFile.get(), true);
            fos.write(rawData);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}