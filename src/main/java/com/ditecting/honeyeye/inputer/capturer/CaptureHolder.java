package com.ditecting.honeyeye.inputer.capturer;

import com.ditecting.honeyeye.HoneyeyeInformation;
import com.ditecting.honeyeye.cachepool.InputCachePool;
import com.ditecting.honeyeye.inputer.PacketCounter;
import com.ditecting.honeyeye.inputer.capturer.scheduler.DefaultSchedulingConfigurer;
import com.ditecting.honeyeye.listener.CapturingListener;
import com.ditecting.honeyeye.listener.ConvertingListener;
import com.ditecting.honeyeye.listener.OutputtingListener;
import com.ditecting.honeyeye.listener.TransmittingListener;
import com.ditecting.honeyeye.pcap4j.extension.core.FullPcapHandle;
import com.ditecting.honeyeye.pcap4j.extension.core.TsharkMappings;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static java.lang.System.currentTimeMillis;

/**
 * CaptureHolder ByteOrder is consistent with Network ByteOrder.
 *
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@Slf4j
@Component
public class CaptureHolder {
    private PcapNetworkInterface nif;
    private FullPcapHandle handle = null;
    private CountDownLatch countDownLatch;

    @Value("${honeyeye.inputer.capturer.count}")
    private int count; //maximum number of captured packet, -1 means infinite

    @Value("${honeyeye.inputer.capturer.readTimeout}")
    private int readTimeout; //read timeout [ms]

    @Value("${honeyeye.inputer.capturer.snaplen}")
    private int snaplen; //number of bytes captured for each packet [bytes]

    @Value("${honeyeye.inputer.capturer.filter}")
    String filter; //filter condition for capture, which is consistent with filter rules in wireshark

    @Value("${honeyeye.listener.meetingTimeout}")
    private double meetingTimeout; //[s]

    @Value("${honeyeye.inputer.capturer.interval}")
    private int interval;

    @Value("${honeyeye.system.outputingMode}")
    private int outputingMode;// 1:transmission, 2:storage, 3: double

    @Autowired
    private MyPcapNetworkInterface myPcapNetworkInterface;

    @Autowired
    private CapturingListener capturingListener;

    @Autowired
    private ConvertingListener convertingListener;

    @Autowired
    private TransmittingListener transmittingListener;

    @Autowired
    private OutputtingListener outputtingListener;

    @Autowired
    private DefaultSchedulingConfigurer defaultSchedulingConfigurer;

    @Autowired
    private InputCachePool inputCachePool;

    @Autowired
    private PacketCounter packetCounter;

    @Autowired
    private HoneyeyeInformation honeyeyeInformation;

    @PostConstruct
    private void init() {
        nif = Objects.requireNonNull(myPcapNetworkInterface.getMyPcapNetworkInterface(), "NIF cannot be NULL");

        int threadCount = 1;
        if(outputingMode==1 || outputingMode==2){
            threadCount++;
        }else if(outputingMode==3){
            threadCount += 2;
        }
        countDownLatch = new CountDownLatch(threadCount);
    }

    public void capture() throws PcapNativeException, NotOpenException, InterruptedException {
        honeyeyeInformation.getHoneyeyeInformation();
        log.info("Start capturing ["+ currentTimeMillis() +"].");

        /* Start MonitorTask*/
        generateMonitorTask();
        /* Start OnlineConverter*/
        convertingListener.gotPacketMeeting(countDownLatch);
        /* Start Outputer*/
        switch (outputingMode){
            case 1:
                transmittingListener.gotTransmitterCachePool(TsharkMappings.generateDefaultPcapFileHeader(), countDownLatch);
                break;
            case 2:
                outputtingListener.gotOutputCachePool(TsharkMappings.generateDefaultPcapFileHeader(), countDownLatch);
                break;
            case 3:
                transmittingListener.gotTransmitterCachePool(TsharkMappings.generateDefaultPcapFileHeader(), countDownLatch);
                outputtingListener.gotOutputCachePool(TsharkMappings.generateDefaultPcapFileHeader(), countDownLatch);
                break;
            default:
                log.warn("No Transmitter or Outputer is started.");
        }

        try {
            generateHandle();
            if (filter.length() != 0) {
                handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);
            }
            log.info("Start capturing NIF [" + nif.getName() + "].");
            handle.loop(count, capturingListener);
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
        handle.close();

        /* stop MonitorTask*/
        defaultSchedulingConfigurer.cancelTriggerTask("Meeting_Monitor");
        defaultSchedulingConfigurer.destroy();
        log.info("MonitorTask stops working.");

        /* end the last waitingMeeting*/
        inputCachePool.endLastWaitingMeeting();
        countDownLatch.await();

        log.info("Stop capturing NIF [" + nif.getName() + "]: " + System.lineSeparator() + packetCounter.printCounter());
        log.info("Finish capturing ["+ currentTimeMillis() +"].");

        /* reset packetCounter*/
        packetCounter.cleanCounter();
    }

    private void generateHandle(){
        try {
            PcapHandle ph = nif.openLive(snaplen, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout);
            inputCachePool.setPcapFileHeader(TsharkMappings.generateDefaultPcapFileHeader());
            handle = new FullPcapHandle(ph.getHandle(), ph.getTimestampPrecision(), ByteOrder.LITTLE_ENDIAN);
        } catch (PcapNativeException e) {
            e.printStackTrace();
        }
    }

    private void generateMonitorTask () throws InterruptedException {
        if(!defaultSchedulingConfigurer.inited()){
            throw new NullPointerException("DefaultSchedulingConfigurer is null in CaptureHolder.");
        }

        Runnable task = () -> {
            int status = inputCachePool.monitorWaitingMeeting();
            switch (status){
                case 0:
                    log.info("There is no WaitingMeeting."); break;
                case 1:
                    log.info("Close a WaitingMeeting."); break;
                default:
                    log.info("There is a WaitingMeeting.");
            }
        };
        String cron = "0/"+ (int)meetingTimeout/interval +" * * * * ? ";
        CronTrigger cronTrigger = new CronTrigger(cron);
        TriggerTask triggerTask = new TriggerTask(task, cronTrigger);
        defaultSchedulingConfigurer.addTriggerTask("Meeting_Monitor", triggerTask);
        log.info("MonitorTask starts working.");
    }

    /**
     * break pcap_loop()
     */
    public void breakLoop () {
        if(handle != null){
            try {
                handle.breakLoop();
            } catch (NotOpenException e) {
                e.printStackTrace();
            }
        }
    }

    //添加构造方法
    public CaptureHolder(){}

    public CaptureHolder(int outputingMode, int count, int readTimeout, int snaplen, String filter, int interval, double meetingTimeout) {
        this.outputingMode = outputingMode;
        this.count = count;
        this.readTimeout = readTimeout;
        this.snaplen = snaplen;
        this.filter = filter;
        this.meetingTimeout = meetingTimeout;
        this.interval = interval;
    }

    //添加getter、setter
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getSnaplen() {
        return snaplen;
    }

    public void setSnaplen(int snaplen) {
        this.snaplen = snaplen;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public double getMeetingTimeout() {
        return meetingTimeout;
    }

    public void setMeetingTimeout(double meetingTimeout) {
        this.meetingTimeout = meetingTimeout;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getOutputingMode() {
        return outputingMode;
    }

    public void setOutputingMode(int outputingMode) {
        this.outputingMode = outputingMode;
    }

    @Override
    public String toString() {
        return "CaptureHolder{" +
                "outputingMode=" + outputingMode +
                ", count=" + count +
                ", readTimeout=" + readTimeout +
                ", snaplen=" + snaplen +
                ", filter='" + filter + '\'' +
                ", interval=" + interval +
                ", meetingTimeout=" + meetingTimeout +
                '}';
    }
}
