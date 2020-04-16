package com.ditecting.honeyeye.converter.session;

import com.ditecting.honeyeye.pcap4j.extension.packet.FullPacket;
import com.ditecting.honeyeye.pcap4j.extension.packet.pool.FullPacketPool;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@Component
public class SessionConverter {
    private static final Logger logger = LoggerFactory.getLogger(SessionConverter.class);

    private List<PacketSession> completeSessionPool = new ArrayList<PacketSession>();

    @Autowired
    SessionConverterAsyn sessionConverterAsyn;

    public void convert(double timeout){
        logger.info("start SessionConverter");
        if(FullPacketPool.fullPacketPoolMap.get() == null){
            throw new NullPointerException("FullPacketPool.fullPacketPoolMap is null.");
        }

        Map<FullPacket.Signature, List<FullPacket>> fullPacketPoolMap = FullPacketPool.fullPacketPoolMap.get();

        fullPacketPoolMap.forEach((key, value) -> {
//            sessionConverterAsyn.convert(value, timeout, completeSessionPool);
            convert(value, timeout);
        });

        PacketSessionPool.addTocompleteSessionPool(completeSessionPool);

        logger.info("completeSessionPool size :" + completeSessionPool.size());
        logger.info("finish SessionConverter");
    }

    /*synchronously converting*/
    protected void convert (@NonNull List<FullPacket> fullPacketList, double timeout) {
//        logger.info(Thread.currentThread().getName() + " start.");

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
