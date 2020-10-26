package com.ditecting.honeyeye.pcap4j.extension.utils;

import com.ditecting.honeyeye.inputer.PacketCounter;
import com.ditecting.honeyeye.pcap4j.extension.packet.FullPacket;
import com.google.gson.JsonArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/6/3 11:17
 */
public class FullPacketUtil {

    /**
     * riffle incompleteFullPacketPool, parse incompleteFullPacket into FullPacket
     *
     * @param segmentMax segmentMax
     */
    public static void riffleFullPacketPool (List<FullPacket> incompleteFullPacketList, byte[] pfhArray, int segmentMax, PacketCounter packetCounter) {

        List<List<FullPacket>> fullPacketLists = null;
        if(incompleteFullPacketList.size() <= segmentMax){
            fullPacketLists = new ArrayList<>();
            fullPacketLists.add(incompleteFullPacketList);
        }else{
            fullPacketLists = segmentFullPacketList(incompleteFullPacketList, segmentMax);
        }

        fullPacketLists.forEach((value)->{
            JsonArray jsonPacketArray = generateRawJsonPacket(value, pfhArray);

            int index = 0;
            for(FullPacket fp : value){
                FullPacket.rebuildFullPacket(fp.getPacket(), jsonPacketArray.get(index++).toString());
                packetCounter.count(fp.getPacket());
            }
        });
    }

    /**
     * segment fullPacketList according to the segmentMax
     *
     * @param fullPacketList
     * @param segmentMax
     * @return
     */
    public static List<List<FullPacket>> segmentFullPacketList (List<FullPacket> fullPacketList, int segmentMax){
        List<List<FullPacket>> fullPacketLists = new ArrayList<>();
        Integer preIndex = 0;
        Integer lastIndex = 0;
        Integer totalNum = fullPacketList.size();
        Integer insertTimes = totalNum / segmentMax;
        List<FullPacket> subNewList;
        for (int i = 0; i <= insertTimes; i++) {
            preIndex = segmentMax * i;
            lastIndex = preIndex + segmentMax;
            if (i == insertTimes) {
                subNewList = fullPacketList.subList(preIndex, fullPacketList.size());
            } else {
                subNewList = fullPacketList.subList(preIndex, lastIndex);

            }
            if (subNewList.size() > 0) {
                fullPacketLists.add(subNewList);
            }
        }

        return fullPacketLists;
    }

    /**
     * call Tshark to parse fullPacketList, and return results in Json formatted by Tshark
     *
     * @param fullPacketList
     * @return
     */
    private static JsonArray generateRawJsonPacket (List<FullPacket> fullPacketList, byte[] pfhArray){
        File tempPcapFile = null;
        try {
            tempPcapFile = generatePcapFile(fullPacketList, pfhArray);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String rawJsonPacket = TsharkUtil.executeTshark(tempPcapFile);

        JsonArray jsonPacketArray = GsonUtil.getJsonPacketArray(rawJsonPacket);

        /*delete temp .pcap file*/
        deletePcapFile(tempPcapFile);

        return jsonPacketArray;
    }

    /**
     * create a .pcap file of fullPacketList
     *
     * @param fullPacketList
     * @return
     * @throws IOException
     */
    private static File generatePcapFile (List<FullPacket> fullPacketList, byte[] pfhArray) throws IOException {
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

    /**
     * delete temp file
     *
     * @param tempPcapFile
     */
    private static void deletePcapFile (File tempPcapFile) {
        tempPcapFile.delete();
    }
}