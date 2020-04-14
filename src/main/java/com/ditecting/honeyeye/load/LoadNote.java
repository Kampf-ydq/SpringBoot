package com.ditecting.honeyeye.load;

import com.ditecting.honeyeye.pcap4j.extension.packet.FullPacket;
import com.ditecting.honeyeye.pcap4j.extension.packet.JsonPacket;
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
public class LoadNote {
    private static ThreadLocal<Map<String, Long>> counter = new ThreadLocal<Map<String, Long>>();

    public static Map<String, Long> getCounter () {
        return counter.get();
    }

    public static void count(FullPacket fullPacket) {
        count(fullPacket.getPacket());
    }

    public static void count(Packet packet) {
        Packet endPacket = null;
        for(Packet p : packet){
            endPacket = p;
        }
        String name;
        if(endPacket instanceof JsonPacket){
            name = endPacket.getClass().getName().replace(endPacket.getClass().getPackage().getName() + ".", "");
            name = name.replace("JsonPacket", "");
        }else {
            name = endPacket.getClass().getName().replace(endPacket.getClass().getPackage().getName() + ".", "");
            name = name.replace("Packet", "");
        }

        if(counter.get() == null){
            counter.set(new HashMap<>());
        }

        if(counter.get().containsKey(name)){
            counter.get().put(name, counter.get().get(name).longValue() + 1);
        }else {
            counter.get().put(name, 1L);
        }
    }

    public static String printCounter () {
        String ls = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, Long>> counterSet = counter.get().entrySet();
        for (Map.Entry<String, Long> entry : counterSet){
            sb.append(entry.getKey() + ": " + entry.getValue() + ls);
        }
        return sb.toString();
    }
}