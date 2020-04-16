package com.ditecting.honeyeye.converter.session;

import com.ditecting.honeyeye.pcap4j.extension.packet.FullPacket;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * //TODO untested
 *
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@Slf4j
@Component
public class SessionConverterAsyn {

    /*asynchronously converting*/
    @Async("asyncConvertExecutor")
    protected void convert (@NonNull List<FullPacket> fullPacketList, double timeout, List<PacketSession> completeSessionPool) {
//        log.info(Thread.currentThread().getName() + " start.");

        List<PacketSession> completeSessionList = new ArrayList<PacketSession>();

        double preTime = 0;
        PacketSession packetSession = null;

        for(FullPacket fp : fullPacketList){
            double myTime = fp.getPcapDataHeader().getTime();
            if(preTime == 0){
                preTime = myTime;
                packetSession = SessionUtil.generatePacketSession(fp, timeout);
                continue;
            }

            if((myTime-preTime) < timeout){//continue current session
                SessionUtil.extendPacketSession(packetSession, fp);
                preTime = myTime;
            }else{//break current session
                SessionUtil.endPacketSession(packetSession, preTime);
                completeSessionList.add(packetSession);

                preTime = myTime;
                packetSession = SessionUtil.generatePacketSession(fp, timeout);
            }
        }

        if(packetSession != null && !packetSession.isComplete()){
            SessionUtil.endPacketSession(packetSession, preTime);
            completeSessionList.add(packetSession);
        }

        synchronized (completeSessionPool) {
            completeSessionPool.addAll(completeSessionList);
        }
    }

}
