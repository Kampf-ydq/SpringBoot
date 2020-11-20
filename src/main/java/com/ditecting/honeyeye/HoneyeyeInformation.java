package com.ditecting.honeyeye;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/11/6 15:28
 */
@Component
@Slf4j
public class HoneyeyeInformation {
    @Value("${pcap4j.extension.util.tsharkPath}")
    private String tsharkPath;
    @Value("${honeyeye.system.inputingMode}")
    private int inputingMode;// 1:capture, 2:load
    @Value("${honeyeye.system.outputingMode}")
    private int outputingMode;// 1:transmission, 2:storage, 3: combination
    @Value("${honeyeye.inputer.capturer.count}")
    private int capturerCount;
    @Value("${honeyeye.inputer.capturer.readTimeout}")
    private int capturerReadTimeout;
    @Value("${honeyeye.inputer.capturer.snaplen}")
    private int capturerSnaplen;
    @Value("${honeyeye.inputer.capturer.filter}")
    private String capturerFilter;
    @Value("${honeyeye.inputer.capturer.interval}")
    private int capturerInterval;
    @Value("${honeyeye.inputer.capturer.enableAutoFind}")
    private boolean capturerEnableAutoFind;
    @Value("${honeyeye.inputer.loader.filePath}")
    private String loaderFilePath;
    @Value("${honeyeye.listener.transmittingGrain}")
    private int transmittingGrain;
    @Value("${honeyeye.listener.transmittingTimeout}")
    private double transmittingTimeout;
    @Value("${honeyeye.listener.outputtingGrain}")
    private int outputtingGrain;
    @Value("${honeyeye.listener.outputtingTimeout}")
    private double outputtingTimeout;
    @Value("${honeyeye.listener.pluginGrain}")
    private int pluginGrain;
    @Value("${honeyeye.listener.pluginTimeout}")
    private double pluginTimeout;
    @Value("${honeyeye.listener.meetingTimeout}")
    private double meetingTimeout;
    @Value("${honeyeye.listener.segmentMax}")
    private int segmentMax;
    @Value("${honeyeye.listener.transmittingListener.port}")
    private String transmittingPort;
    @Value("${honeyeye.listener.transmittingListener.netAddress}")
    private String transmittingNetAddress;
    @Value("${honeyeye.outputer.filePath}")
    private String outputtingFilePath;
    @Value("${honeyeye.outputer.fileName}")
    private String outputtingFileName;

    public void getHoneyeyeInformation(){
        log.info("******************************************* Honeyeye *******************************************");
        log.info("Tshark path: " + tsharkPath);
        log.info("Packet meeting timeout threshold: " + meetingTimeout + " [s]");
        log.info("Max segment size: " + segmentMax);
        if(inputingMode == 1){
            log.info("Input mode: " + "Capture");
            log.info("PacketCount: " + capturerCount);
            log.info("TimeoutMillis: " + capturerReadTimeout + " [ms]");
            log.info("Snaplen: " + capturerSnaplen);
            log.info("Filter: " + capturerFilter);
            log.info("EnableAutoFind: " + capturerEnableAutoFind);
        }else {
            log.info("Input mode: " + "Load");
            log.info("FilePath: " + loaderFilePath);
        }

        switch (outputingMode){
            case 1:
                log.info("Output mode: " + "Transmission");
                log.info("Transmission grain: " + convertGrain(transmittingGrain));
                log.info("Transmission timeout threshold: " + transmittingTimeout + " [s]");
                log.info("Transmission port: " + transmittingPort);
                log.info("Transmission netAddress: " + transmittingNetAddress);
                break;
            case 2:
                log.info("Output mode: " + "Storage");
                log.info("Storage grain: " + convertGrain(outputtingGrain));
                log.info("Storage timeout threshold: " + outputtingTimeout + " [s]");
                log.info("Storage filePath: " + outputtingFilePath);
                log.info("Storage fileName: " + outputtingFileName);
                break;
            case 3:
                log.info("Output mode: " + "Transmission & Storage");
                log.info("Transmission grain: " + convertGrain(transmittingGrain));
                log.info("Transmission timeout threshold: " + transmittingTimeout + " [s]");
                log.info("Transmission port: " + transmittingPort);
                log.info("Transmission netAddress: " + transmittingNetAddress);
                log.info("Storage grain: " + convertGrain(outputtingGrain));
                log.info("Storage timeout threshold: " + outputtingTimeout + " [s]");
                log.info("Storage filePath: " + outputtingFilePath);
                log.info("Storage fileName: " + outputtingFileName);
                break;
            default:
                log.info("Output mode: " + "Plugin");
                log.info("Plugin grain: " + convertGrain(pluginGrain));
                log.info("Plugin timeout threshold: " + pluginTimeout + " [s]");
        }
        log.info("*************************************************************************************************");
    }

     private String convertGrain (int grain) {
         switch (grain){
             case 1:
                 return "Packet";
             case 2:
                 return "Flow";
             case 3:
                 return "Session";
             default:
                 return "RawPacket";
         }
     }

}