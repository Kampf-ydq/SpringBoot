package com.ditecting.honeyeye.pcap4j.extension.packet.pool;

import com.ditecting.honeyeye.load.LoadNote;
import com.ditecting.honeyeye.pcap4j.extension.core.TsharkMappings;
import com.ditecting.honeyeye.pcap4j.extension.packet.FullPacket;
import com.ditecting.honeyeye.pcap4j.extension.utils.GsonUtils;
import com.ditecting.honeyeye.pcap4j.extension.utils.TsharkUtils;
import com.google.gson.JsonArray;
import lombok.Setter;
import org.pcap4j.packet.Packet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * calling tshatk packet by packet will waste too much time.
 * when packets are too many, we do not need very real time,
 * and the order among them is not important, we can process them
 * in a pool. When the number of packets in the packet pool exceeds
 * a certain threshold  * or the packet pool has remained unchanged
 * for some time, we will call tshark to process the packet pool
 * instead of each packet.
 *
 * @author CSheng
 * @version 1.0
 * @date 2020/4/4 2:26
 */
@Setter
public class FullPacketPool {
    public static ThreadLocal<Map<FullPacket.Signature, List<FullPacket>>> fullPacketPoolMap = new ThreadLocal<Map<FullPacket.Signature, List<FullPacket>>>();
    public static ThreadLocal<List<FullPacket>> incompleteFullPacketPoolList = new ThreadLocal<List<FullPacket>>();
    public static ThreadLocal<TsharkMappings.PcapFileHeader> pcapFileHeader = new ThreadLocal<TsharkMappings.PcapFileHeader>();

    /**
     * generate FullPacket with packet and pcapDataHeader, and add FullPacket to the Pool
     * @param packet
     * @param pcapDataHeader
     */
    public static void addToFullPacketPool(Packet packet, TsharkMappings.PcapDataHeader pcapDataHeader) {

        if(fullPacketPoolMap.get() == null){
            fullPacketPoolMap.set(new HashMap<FullPacket.Signature, List<FullPacket>>());
        }

        FullPacket fullPacket = new FullPacket(packet, pcapDataHeader);
        /*add FullPacket to the fullPacketPoolMap*/
        if(!FullPacketPool.fullPacketPoolMap.get().containsKey(fullPacket.getSignature())){
            List<FullPacket> fullPacketList = new ArrayList<FullPacket>();
            fullPacket.setSequence(0);
            fullPacketList.add(fullPacket);
            FullPacketPool.fullPacketPoolMap.get().put(fullPacket.getSignature(), fullPacketList);
        }else{
            fullPacket.setSequence(FullPacketPool.fullPacketPoolMap.get().get(fullPacket.getSignature()).size());
            FullPacketPool.fullPacketPoolMap.get().get(fullPacket.getSignature()).add(fullPacket);
        }

        /*add incomplete FullPacket to the incompleteFullPacketPoolList*/
        if(!fullPacket.isComplete()){
            if(incompleteFullPacketPoolList.get() == null){
                incompleteFullPacketPoolList.set(new ArrayList<FullPacket>());
            }
            incompleteFullPacketPoolList.get().add(fullPacket);
        }else {
            LoadNote.count(packet);
        }
    }

    /**
     * riffle incompleteFullPacketPool and convert incompleteFullPacket into FullPacket
     */
    public static void riffleFullPacketPool () {

        int index = 0;
        JsonArray jsonPacketArray = generateRawJsonPacket(incompleteFullPacketPoolList.get());
        for(FullPacket fp : incompleteFullPacketPoolList.get()){
            FullPacket.rebuildFullPacket(fp.getPacket(), jsonPacketArray.get(index++).toString());
            LoadNote.count(fp.getPacket());
        }
    }

    private static JsonArray generateRawJsonPacket (List<FullPacket> fullPacketList){
        File tempPcapFile = null;
        try {
            tempPcapFile = generatePcapFile(fullPacketList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String rawJsonPacket = TsharkUtils.executeTshark(tempPcapFile);

        JsonArray jsonPacketArray = GsonUtils.getJsonPacketArray(rawJsonPacket);

        /*delete temp .pcap file*/
        deletePcapFile(tempPcapFile);

        return jsonPacketArray;
    }

    private static File generatePcapFile (List<FullPacket> fullPacketList) throws IOException {
        if (pcapFileHeader == null) {
            throw new NullPointerException("pcapFileHeader is null.");
        }
        byte[] pfhArray = pcapFileHeader.get().toByteArray();

        String tempName = (new Date()).getTime() + "";
        File tempPcapFile = null;
        FileOutputStream fos;
        try {
            tempPcapFile = File.createTempFile(tempName, ".pcap");
            fos = new FileOutputStream(tempPcapFile);
            fos.write(pfhArray);

            for (FullPacket fp : fullPacketList) {
                fos.write(fp.getPcapDataHeader().toByteArray());
                fos.write(fp.getPacket().getRawData());
            }

            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(tempPcapFile == null){
            throw new IOException(".pcap File failed to be created.");
        }
        return tempPcapFile;
    }

    private static void deletePcapFile (File tempPcapFile) {
        tempPcapFile.delete();
    }

}