package com.ditecting.honeyeye.converter.session;

import lombok.Builder;
import lombok.Data;

import java.net.Inet4Address;
import java.sql.Date;
import java.text.NumberFormat;
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
    private boolean complete;

    public String toJsonString(){
        StringBuilder sb = new StringBuilder();
        sb.append("\"signature\": \"" + signature + "\",");
        sb.append(protocolToJsonString () + ",");
        sb.append("\"timeout\": " + formatDouble(timeout) + ",");
        sb.append("\"srcIp\": \"" + srcIp + "\",");
        sb.append("\"dstIp\": \"" + dstIp + "\",");
        sb.append("\"startTime\": " + formatDouble(startTime) + ",");
        sb.append("\"endTime\": " + formatDouble(endTime) + ",");
        sb.append(segmentListToJsonString());
        return sb.toString();
    }

    public static String formatDouble(double d) {
        NumberFormat nf = NumberFormat.getInstance();
//        nf.setMaximumFractionDigits(20);
        nf.setGroupingUsed(false);
        return nf.format(d);
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

    private String segmentListToJsonString () {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(String str : segmentList){
            sb.append("{");
            sb.append(str);
            sb.append("},");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");

        return "\"segments\": " + sb.toString();
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