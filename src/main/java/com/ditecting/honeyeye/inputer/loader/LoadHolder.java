package com.ditecting.honeyeye.inputer.loader;

import com.ditecting.honeyeye.cachepool.InputCachePool;
import com.ditecting.honeyeye.inputer.PacketCounter;
import com.ditecting.honeyeye.listener.ConvertingListener;
import com.ditecting.honeyeye.listener.LoadingListener;
import com.ditecting.honeyeye.listener.OutputtingListener;
import com.ditecting.honeyeye.listener.TransmittingListener;
import com.ditecting.honeyeye.pcap4j.extension.core.FullPcapHandle;
import com.ditecting.honeyeye.pcap4j.extension.core.TsharkMappings;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.*;
import org.pcap4j.util.ByteArrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.ByteOrder;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import static java.lang.System.currentTimeMillis;

/**
 * LoadHolder ByteOrder is consistent with System ByteOrder.
 *
 * @author CSheng
 * @version 1.0
 * @date 2020/3/31 22:01
 */
@Slf4j
@Component
public class LoadHolder {
    private FullPcapHandle handle = null;
    private CountDownLatch countDownLatch;

    @Value("${honeyeye.inputer.loader.filePath}")
    private String filePath;

    @Value("${honeyeye.listener.segmentMax}")
    private int segmentMax;

    @Value("${honeyeye.system.outputingMode}")
    private int outputingMode;// 1:transmission, 2:storage, 3: double

    @Autowired
    private LoadingListener loadingListener;

    @Autowired
    private ConvertingListener convertingListener;

    @Autowired
    private TransmittingListener transmittingListener;

    @Autowired
    private OutputtingListener outputtingListener;

    @Autowired
    private InputCachePool inputCachePool;

    @Autowired
    private PacketCounter packetCounter;

    @PostConstruct
    private void init() {
        int threadCount = 1;
        if(outputingMode==1 || outputingMode==2){
            threadCount++;
        }else if(outputingMode==3){
            threadCount += 2;
        }
        countDownLatch = new CountDownLatch(threadCount);
    }

    /**
     * as an interface used to be called by other projects
     *
     * @param filePath
     */
    public void load(String filePath) throws InterruptedException {
        this.filePath = filePath;
        load();
    }

    public void load() throws InterruptedException {
        log.info("Start loading ["+ currentTimeMillis() +"].");

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

        /* Start loading and parsing .pcap file*/
        try {
            filePath = generateHandle(filePath);
            if(handle == null){
                throw new NullPointerException("FullPcapHandle is null in LoadHolder.");
            }
            log.info("Start looping ["+ currentTimeMillis() +"].");
            handle.loop(-1,  loadingListener);
        } catch (PcapNativeException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NotOpenException e) {
            e.printStackTrace();
        }
        handle.close();

        /* end the last waitingMeeting*/
        inputCachePool.endLastWaitingMeeting();
        countDownLatch.await();

        log.info("The file [" + filePath + "] has been parsed:" + System.lineSeparator() + packetCounter.printCounter());
        log.info("Finish loading ["+ currentTimeMillis() +"].");

        /* reset the packet globalNumber to 1*/
        inputCachePool.resetGlobalNumber();
        /* reset packetCounter*/
        packetCounter.cleanCounter();
    }

    private String generateHandle(String filePath){
        try {
            if (!checkPcapFile(filePath)){
                filePath = inputPcapFile();
            }

            PcapHandle ph = Pcaps.openOffline(filePath);

            inputCachePool.setPcapFileHeader(generatePcapFileHeader(filePath));
            ;
            ByteOrder byteOrder = null;
            if(inputCachePool.getPcapFileHeader() == null){
                throw new NullPointerException("PcapFileHeader is null");
            }else{
                if(inputCachePool.getPcapFileHeader().getMagic() == 0xa1b2c3d4){
                    byteOrder = ByteOrder.BIG_ENDIAN;
                }
                if(inputCachePool.getPcapFileHeader().getMagic() == 0xd4c3b2a1){
                    byteOrder = ByteOrder.LITTLE_ENDIAN;
                }
            }
            if(byteOrder == null){
                throw new IOException("The file [" + filePath + "] is not a valid .pcap file.");
            }

            handle = new FullPcapHandle(ph.getHandle(), ph.getTimestampPrecision(), byteOrder);
        } catch (PcapNativeException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        return filePath;
    }

    private boolean checkPcapFile(String filePath) {
        if (filePath == null){
            log.warn("The filePath is null.");
            return false;
        }

        if (filePath.equals("")){
            log.warn("The filePath is \"\".");
            return false;
        }

        File file = new File(filePath);
        if (! file.exists()){
            log.warn("The file [" + filePath + "] does not exist.");
            return false;
        }
        int index = filePath.lastIndexOf(".");
        if (index == -1){
            log.warn("The file [" + filePath + "] is not a .pcap file.");
            return false;
        }
        String fileType = filePath.substring(index + 1);
        if (!fileType.equals("pcap")){
            log.warn("The file [" + filePath + "] is not a .pcap file.");
            return false;
        }

        return true;
    }

    private String inputPcapFile() throws IOException {
        Scanner scan = new Scanner(System.in);

        boolean validPath = false;
        int maxLoops = 3;

        /*read the file path in a loop*/
        String  filePath = null;
        while (! validPath && maxLoops > 0){
            System.out.println("please enter the file path of a .pcap file：");
            filePath = scan.nextLine();

            if(checkPcapFile(filePath)){
                validPath = true;
                break;
            }
            maxLoops--;
        }

        if(! validPath){
            StringBuilder sb = new StringBuilder();
            sb.append("filePath: [").append(filePath).append("] is invalid!");
            throw new IOException(sb.toString());
        }

        return filePath;
    }

    private TsharkMappings.PcapFileHeader generatePcapFileHeader (String filePath) {
        FileInputStream fis = null;
        byte[] file_header = new byte[24];
        int headerLength = 24;
        try {
            fis = new FileInputStream(filePath);
            int m = fis.read(file_header, 0, headerLength-1);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        log.info("file_header: " + ByteArrays.toHexString(file_header, ""));

        int offset = 0;
        int magic = ByteArrays.getInt(file_header, offset);
        offset += 4;

        short magorVersion = ByteArrays.getShort(file_header, offset);
        offset += 2;

        short minorVersion = ByteArrays.getShort(file_header, offset);
        offset += 2;

        int timezone = ByteArrays.getInt(file_header, offset);
        offset += 4;

        int sigflags = ByteArrays.getInt(file_header, offset);
        offset += 4;

        int snaplen = ByteArrays.getInt(file_header, offset);
        offset += 4;

        int linktype = ByteArrays.getInt(file_header, offset);

        TsharkMappings.PcapFileHeader pcapFileHeader = TsharkMappings.PcapFileHeader.builder()
                .magic(magic)
                .magorVersion(magorVersion)
                .minorVersion(minorVersion)
                .timezone(timezone)
                .sigflags(sigflags)
                .snaplen(snaplen)
                .linktype(linktype)
                .build();
//        log.info("PcapFileHeader: " + ByteArrays.toHexString(pcapFileHeader.toByteArray(), ""));

        return pcapFileHeader;
    }
}