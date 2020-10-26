package com.ditecting.honeyeye.converter.datatype;

import com.ditecting.honeyeye.pcap4j.extension.utils.PacketUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/23 16:20
 */
@Setter
@Getter
@Builder
public class PacketFlow {
    private String signature;
    private double timeout;// threshold for timeout in s
    private String srcIp;
    private String dstIp;
    private int prot;//transport protocol, 1:tcp, 2:udp
    private int srcPort;
    private int dstPort;
    private int packetNum;
    private int packetSize;
    private double startTime;
    private double endTime;
    private double duration;
    private List<String> segmentList;

    public String toJsonString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"signature\": \"" + signature + "\",");
        sb.append("\"timeout\": " + PacketUtil.formatDouble(timeout) + ",");
        sb.append("\"srcIp\": \"" + srcIp + "\",");
        sb.append("\"dstIp\": \"" + dstIp + "\",");
        sb.append("\"prot\": " + prot + ",");
        sb.append("\"srcPort\": " + srcPort + ",");
        sb.append("\"dstPort\": " + dstPort + ",");
        sb.append("\"packetNum\": " + packetNum + ",");
        sb.append("\"packetSize\": " + packetSize + ",");
        sb.append("\"startTime\": " + PacketUtil.formatDouble(startTime) + ",");
        sb.append("\"endTime\": " + PacketUtil.formatDouble(endTime) + ",");
        sb.append("\"duration\": " + PacketUtil.formatDouble(duration) + ",");
        sb.append(PacketUtil.segmentListToJsonString(segmentList));
        sb.append("}");
        return sb.toString();
    }

    public void addSegment (String segment) {
        if(this.segmentList == null){
            segmentList  = new ArrayList<>();
        }
        segmentList.add(segment);
    }

}