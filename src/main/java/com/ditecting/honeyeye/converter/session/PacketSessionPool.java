package com.ditecting.honeyeye.converter.session;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/4 2:18
 */
public class PacketSessionPool {
    private static ThreadLocal<List<PacketSession>> initialSessionPool = new ThreadLocal<List<PacketSession>>();
    private static ThreadLocal<List<PacketSession>> waitingSessionPool = new ThreadLocal<List<PacketSession>>();
    private static ThreadLocal<List<PacketSession>> completeSessionPool = new ThreadLocal<List<PacketSession>>();

    public static List<PacketSession> getCompleteSessionPool(){
        return completeSessionPool.get();
    }

    public static synchronized void addTocompleteSessionPool(List<PacketSession> completeSessionList){
        if(completeSessionPool.get() == null){
            completeSessionPool.set(new ArrayList<PacketSession>());
        }

        completeSessionPool.get().addAll(completeSessionList);
    }


}