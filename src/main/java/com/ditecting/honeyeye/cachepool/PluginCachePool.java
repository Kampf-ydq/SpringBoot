package com.ditecting.honeyeye.cachepool;

import com.ditecting.honeyeye.pcap4j.extension.packet.FullPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/6/4 10:28
 */

@Component
@Slf4j
public class PluginCachePool {
    private Queue<String> stringCachePool = new LinkedList<>();
    private Queue<FullPacket> fullPacketCachePool = new LinkedList<>();
    private boolean status = false; //false:continue, true:end

    public boolean getStatus () {
        return status;
    }

    public void setStatus (boolean status) {
        this.status = status;
    }

    public void addAll (List<String> strings, List<FullPacket> fullPackets) {
        stringCachePool.addAll(strings);
        fullPacketCachePool.addAll(fullPackets);
//        log.info(strings.size() + " items are added to the PluginCachePool.");
    }

    public void add (String string, FullPacket fullPacket) {
        stringCachePool.offer(string);
        fullPacketCachePool.offer(fullPacket);
//        log.info("An item is added to the PluginCachePool.");
    }

    public boolean isStringEmpty () {
        return stringCachePool.isEmpty();
    }

    public boolean isFullPacketEmpty () {
        return fullPacketCachePool.isEmpty();
    }

    public String removeString () {
        String string = stringCachePool.poll();
//        log.info("An item is removed from the PluginCachePool.");
        return string;
    }

    public FullPacket removeFullPacket () {
        FullPacket fullPacket = fullPacketCachePool.poll();
//        log.info("An item is removed from the PluginCachePool.");
        return fullPacket;
    }

    public List<String> removeAllString(){
        List<String> result = new ArrayList<>(stringCachePool);
        stringCachePool.clear();
        return result;
    }

    public List<FullPacket> removeAllFullPacket(){
        List<FullPacket> result = new ArrayList<>(fullPacketCachePool);
        fullPacketCachePool.clear();
        return result;
    }


    public List<String> getAllString(){
        List<String> result = new ArrayList<>(stringCachePool);
        return result;
    }

    public List<FullPacket> getAllFullPacket(){
        List<FullPacket> result = new ArrayList<>(fullPacketCachePool);
        return result;
    }

    public void clean () {
        status = false;
        stringCachePool.clear();
        fullPacketCachePool.clear();
    }
}