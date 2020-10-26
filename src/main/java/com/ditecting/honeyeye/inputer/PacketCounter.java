package com.ditecting.honeyeye.inputer;

import com.ditecting.honeyeye.pcap4j.extension.utils.PacketUtil;
import org.pcap4j.packet.Packet;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/3 19:54
 */
@Component
public class PacketCounter {
    private Map<String, Long> counter = new HashMap<>();

    public void cleanCounter () {
        counter.clear();
    }

    public void count(Packet packet) {
        String name = PacketUtil.getTopProtocolFromPacket(packet).get("name");

        if(counter.containsKey(name)){
            counter.put(name, counter.get(name).longValue() + 1);
        }else {
            counter.put(name, 1L);
        }
    }

    public String printCounter () {
        String ls = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, Long>> counterSet = counter.entrySet();
        for (Map.Entry<String, Long> entry : counterSet){
            sb.append(entry.getKey() + ": " + entry.getValue() + ls);
        }
        return sb.toString();
    }
}