package com.ditecting.honeyeye.converter.utils;

import com.ditecting.honeyeye.converter.datatype.PacketFlow;
import com.ditecting.honeyeye.converter.datatype.PacketSession;
import com.ditecting.honeyeye.pcap4j.extension.packet.FullPacket;
import com.ditecting.honeyeye.pcap4j.extension.utils.PacketUtil;
import lombok.NonNull;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/6/4 9:59
 */
public class ConverterUtil {

    /**
     * convert fullPacketList with the same srcIP and dstIP into Flow
     * @param fullPacketList
     * @param dataTimeout
     */
    public static List<PacketFlow> convertFlow (@NonNull List<FullPacket> fullPacketList, double dataTimeout) {

        List<PacketFlow> completeFlowList = new ArrayList<PacketFlow>();

        Map<FullPacket.SubSignature, PacketFlow> currentPacketFlows = new HashMap<FullPacket.SubSignature, PacketFlow>();

        for(FullPacket fp : fullPacketList){
            double myTime = fp.getPcapDataHeader().getTime();
            FullPacket.SubSignature mySubSignature = PacketUtil.getSubSignature(PacketUtil.getIpV4Packet(fp));

            if(currentPacketFlows.isEmpty()){
                PacketFlow packetFlow = FlowUtil.generatePacketFlow(fp, dataTimeout);
                currentPacketFlows.put(mySubSignature, packetFlow);
                continue;
            }

            if(currentPacketFlows.containsKey(mySubSignature)) {
                if (myTime - currentPacketFlows.get(mySubSignature).getEndTime() < dataTimeout) {
                    FlowUtil.extendPacketFlow(currentPacketFlows.get(mySubSignature), fp);
                    continue;
                } else {
                    completeFlowList.add(currentPacketFlows.get(mySubSignature));
                }
            }
            PacketFlow packetFlow = FlowUtil.generatePacketFlow(fp, dataTimeout);
            currentPacketFlows.put(mySubSignature, packetFlow);
        }

        currentPacketFlows.forEach((key, value) -> {
            completeFlowList.add(value);
        });

        return completeFlowList;
    }

    /**
     * convert fullPacketList with the same srcIP and dstIP into Session
     * @param fullPacketList
     * @param dataTimeout
     * @return
     */
    public static List<PacketSession> convertSession (@NonNull List<FullPacket> fullPacketList, double dataTimeout) {
        List<PacketSession> completeSessionList = new ArrayList<PacketSession>();

        PacketSession packetSession = null;

        for(FullPacket fp : fullPacketList){
            double myTime = fp.getPcapDataHeader().getTime();
            if(packetSession == null){
                packetSession = SessionUtil.generatePacketSession(fp, dataTimeout);
                continue;
            }

            if((myTime-packetSession.getEndTime()) < dataTimeout){//continue current session
                SessionUtil.extendPacketSession(packetSession, fp);
            }else{//break current session
                completeSessionList.add(packetSession);
                packetSession = SessionUtil.generatePacketSession(fp, dataTimeout);
            }
        }

        if(packetSession != null){
            completeSessionList.add(packetSession);
        }

        return completeSessionList;
    }

    /**
     * convert PacketMeeting into rawPacket list in String
     * @param fullPacketList
     * @return
     * @throws UnsupportedEncodingException
     */
    public static List<String> adaptRawPacket (List<FullPacket> fullPacketList) throws UnsupportedEncodingException {

        List<String> stringList = new ArrayList<>();
        for(int a=0; a<fullPacketList.size(); a++){
            byte[] bytes = PacketUtil.fullPacketToRawPacket(fullPacketList.get(a));
            String  str = new String(bytes, "ISO-8859-1");
            stringList.add(str);
        }

        return stringList;
    }

    /**
     * convert PacketMeeting into Packet list in String
     * @param fullPacketList
     * @return
     */
    public static List<String> adaptPacket (List<FullPacket> fullPacketList) {
        List<String> stringList = new ArrayList<>();
        for(int a=0; a<fullPacketList.size(); a++){
            String str = PacketUtil.fullPacketToJson(fullPacketList.get(a));
            stringList.add(str);
        }

        return stringList;
    }

    /**
     * convert PacketMeeting into Flow list in String in ascending startTime order
     * @param fullPacketPoolMap
     * @param dataTimeout
     * @return
     */
    public static List<String> adaptFlow (Map<FullPacket.Signature, List<FullPacket>> fullPacketPoolMap, double dataTimeout) {

        List<PacketFlow> completeFlowPool = new ArrayList<>();
        fullPacketPoolMap.forEach((key, value)->{
            if(key != null){
                completeFlowPool.addAll(ConverterUtil.convertFlow(value, dataTimeout));
            }
        });

        //order flows by startTime
        Collections.sort(completeFlowPool, (o1, o2) -> {
            double diff = o1.getStartTime() - o2.getStartTime();
            if(diff > 0){
                return 1;
            }else if (diff < 0){
                return -1;
            }
            return 0;
        });

        List<String> stringList = new ArrayList<>();
        for(int a=0; a<completeFlowPool.size(); a++){
            stringList.add(completeFlowPool.get(a).toJsonString());
        }

        return stringList;
    }

    /**
     * convert PacketMeeting into Session list in String in ascending startTime order
     * @param fullPacketPoolMap
     * @param dataTimeout
     * @return
     */
    public static List<String> adaptSession (Map<FullPacket.Signature, List<FullPacket>> fullPacketPoolMap, double dataTimeout) {
        List<PacketSession> completeSessionPool = new ArrayList<>();
        fullPacketPoolMap.forEach((key, value)->{
            if(key != null){
                completeSessionPool.addAll(ConverterUtil.convertSession(value, dataTimeout));
            }
        });

        //order sessions by startTime
        Collections.sort(completeSessionPool, (o1, o2) -> {
            double diff = o1.getStartTime() - o2.getStartTime();
            if(diff > 0){
                return 1;
            }else if (diff < 0){
                return -1;
            }
            return 0;
        });

        List<String> stringList = new ArrayList<>();
        for(int a=0; a<completeSessionPool.size(); a++){
            stringList.add(completeSessionPool.get(a).toJsonString());
        }

        return stringList;
    }

    public static Map<FullPacket.Signature, List<FullPacket>> fullPacketListToMap (List<FullPacket> fullPacketList){
        Map<FullPacket.Signature, List<FullPacket>> fullPacketPoolMap = new HashMap<FullPacket.Signature, List<FullPacket>>();

        fullPacketList.forEach((value)->{
            if(!fullPacketPoolMap.containsKey(value.getSignature())){
                List<FullPacket> fpl = new ArrayList<FullPacket>();
                fpl.add(value);
                fullPacketPoolMap.put(value.getSignature(), fpl);
            }else{
                fullPacketPoolMap.get(value.getSignature()).add(value);
            }
        });

        return fullPacketPoolMap;
    }
}