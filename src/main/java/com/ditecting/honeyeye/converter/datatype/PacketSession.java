package com.ditecting.honeyeye.converter.datatype;

import com.ditecting.honeyeye.pcap4j.extension.utils.PacketUtil;
import lombok.Builder;
import lombok.Data;

import java.util.*;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/4 2:47
 */
@Data
@Builder
public class PacketSession {
    private String signature;
    private Map<Integer, Set<String>> protocols;// top-level protocol, multi protocols should be seperated by ";".
    private double timeout;// threshold for timeout in s
    private String srcIp;
    private String dstIp;
    private double startTime;
    private double endTime;
    private List<String> segmentList;

    public String toJsonString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"signature\": \"" + signature + "\",");
        sb.append(protocolToJsonString () + ",");
        sb.append("\"timeout\": " + PacketUtil.formatDouble(timeout) + ",");
        sb.append("\"srcIp\": \"" + srcIp + "\",");
        sb.append("\"dstIp\": \"" + dstIp + "\",");
        sb.append("\"startTime\": " + PacketUtil.formatDouble(startTime) + ",");
        sb.append("\"endTime\": " + PacketUtil.formatDouble(endTime) + ",");
        sb.append(PacketUtil.segmentListToJsonString(segmentList));
        sb.append("}");
        return sb.toString();
    }

    private String protocolToJsonString () {
        StringBuilder sbProtocol = new StringBuilder();
        protocols.forEach((key, value) -> {
            sbProtocol.append("\""+ key +"\": [");
            for(String str : value){
                sbProtocol.append("\""+ str +"\",");
            }
            sbProtocol.deleteCharAt(sbProtocol.length() - 1);
            sbProtocol.append("],");
        });
        sbProtocol.deleteCharAt(sbProtocol.length() - 1);

       return "\"protocols\": {" + sbProtocol.toString() + "}";
    }

    public void addProtocol (Integer layer, String name){
        if(protocols == null){
            protocols = new HashMap<Integer, Set<String>>();
        }

        if( protocols.containsKey(layer)){
            protocols.get(layer).add(name);
        }else {
            Set<String> set = new TreeSet<>();
            set.add(name);
            protocols.put(layer, set);
        }
    }

    public void addSegment (String segment) {
        if(this.segmentList == null){
            segmentList  = new ArrayList<>();
        }
        segmentList.add(segment);
    }


}