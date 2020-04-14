package com.ditecting.honeyeye.session;

import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/4/4 2:18
 */
//TODO incomplete
public class SessionPool {
    //    public static List<FullPacket> packetPool = new ArrayList<>();
//    public static long corePoolSize;
//    public static long maxPoolSize;
//    public static long queueCapacity;
//    public static long maxWaitTime;
//    public static long dynamicWaitTime;
    public static List<PacketSession> initialSessions = new ArrayList<>();
    public static List<PacketSession> waitingSessions = new ArrayList<>();
    public static List<PacketSession> completeSessions = new ArrayList<>();

    /* the mode of parsing application layer protocols, "excludable=true" only just parse specified protocols,
     * otherwise parse other protocols as UnrelatedPacket.
     */
//    private static int excludingMode;

    /* converting grain, including packet(1), flow(2), and session(3)*/
    @Value("${capture.convertListener.convertGrain}")
    private int convertGrain;

    //    @Autowired
//    private RawPacketConverter rawPacketConverter;
//
//    @Autowired
//    private PacketConverter packetConverter;
//
//    @Autowired
//    private FlowConverter flowConverter;
//
//    @Autowired
//    private SessionConverter sessionConverter;

    //TODO incomplete
//        switch (convertGrain){
//            case 1:
//                packetConverter.executeAsync(packet);
//                break;
//            case 2:
//                flowConverter.executeAsync(packet);
//                break;
//            case 3:
//                sessionConverter.executeAsync(packet);
//                break;
//            default:
//                rawPacketConverter.executeAsync(packet);
//        }

}