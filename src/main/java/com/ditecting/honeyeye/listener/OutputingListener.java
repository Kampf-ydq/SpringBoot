package com.ditecting.honeyeye.listener;

import com.ditecting.honeyeye.outputer.FlowOutputer;
import com.ditecting.honeyeye.outputer.PacketOutputer;
import com.ditecting.honeyeye.outputer.RawPacketOutputer;
import com.ditecting.honeyeye.outputer.SessionOutputer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@Component
public class OutputingListener{

    /* converting grain, including packet(1), flow(2), and session(3)*/
    @Value("${listener.convertListener.convertGrain}")
    private int convertGrain;

    @Autowired
    RawPacketOutputer rawPacketOutputer;

    @Autowired
    PacketOutputer packetOutputer;

    @Autowired
    FlowOutputer flowOutputer;

    @Autowired
    SessionOutputer sessionOutputer;

    public void gotFullPacketPool(boolean mode, int myGrain){
        int outputMode  = -1;
        if(mode){
            outputMode = myGrain;
        }else {
            outputMode = convertGrain;
        }

        switch (outputMode){
            case 1:
                packetOutputer.output();
                break;
            case 2:
                flowOutputer.output();
                break;
            case 3:
                sessionOutputer.output();
                break;
            default:
                rawPacketOutputer.output();
        }
    }
}
