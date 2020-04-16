package com.ditecting.honeyeye.picker.loader;

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
public class LoadNote {
    private static ThreadLocal<Map<String, Long>> counter = new ThreadLocal<Map<String, Long>>();

    public static Map<String, Long> getCounter () {
        return counter.get();
    }

    public static void count(Packet packet) {
        String name = PacketUtil.getTopProtocolFromPacket(packet).get("name");

        if(counter.get() == null){
            counter.set(new HashMap<>());
        }

        if(counter.get().containsKey(name)){
            counter.get().put(name, counter.get().get(name).longValue() + 1);
        }else {
            counter.get().put(name, 1L);
        }
    }

//    public static void count(PacketSession packetSession) {
//        if(counter.get() == null){
//            counter.set(new HashMap<>());
//        }
//        String name = packetSession.getSignature();
//        if(counter.get().containsKey(name)){
//            counter.get().put(name, counter.get().get(name).longValue() + 1);
//        }else {
//            counter.get().put(name, 1L);
//        }
//
//    }

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