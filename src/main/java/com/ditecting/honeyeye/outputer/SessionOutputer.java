package com.ditecting.honeyeye.outputer;

import com.ditecting.honeyeye.converter.session.PacketSession;
import com.ditecting.honeyeye.converter.session.PacketSessionPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/15 21:22
 */
@Slf4j
@Component
public class SessionOutputer {

    public void output(){
        List<PacketSession> completeSessionPool = PacketSessionPool.getCompleteSessionPool();
        if(completeSessionPool == null){
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(PacketSession packetSession : completeSessionPool){
            sb.append("{");
            sb.append(packetSession.toJsonString());
            sb.append("},");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");

        CreateFileUtil.createJsonFile(sb.toString(), "C:\\Users\\18809\\Desktop", "test2");
    }

}