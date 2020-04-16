package com.ditecting.honeyeye.listener;

import com.ditecting.honeyeye.converter.FlowConverter;
import com.ditecting.honeyeye.converter.PacketConverter;
import com.ditecting.honeyeye.converter.RawPacketConverter;
import com.ditecting.honeyeye.converter.session.SessionConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@Component
public class ConvertingListener {
    /* the mode of parsing application layer protocols, "excludable=true" only just parse specified protocols,
     * otherwise parse other protocols as UnrelatedPacket.
     */
//    private static int excludingMode;

    /* converting grain, including packet(1), flow(2), and session(3)*/
    @Value("${listener.convertListener.convertGrain}")
    private int convertGrain;

    @Value("${listener.convertListener.timeout}")
    private double timeout;

    @Autowired
    RawPacketConverter rawPacketConverter;

    @Autowired
    PacketConverter packetConverter;

    @Autowired
    FlowConverter flowConverter;

    @Autowired
    SessionConverter sessionConverter;

    public void gotFullPacketPool () {
        switch (convertGrain){
            case 1:
               packetConverter.convert();
                break;
            case 2:
               flowConverter.convert();
                break;
            case 3:
               sessionConverter.convert(timeout);
                break;
            default:
                rawPacketConverter.convert();
        }
    }

}
