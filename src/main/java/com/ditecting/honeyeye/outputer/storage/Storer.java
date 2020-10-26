package com.ditecting.honeyeye.outputer.storage;

import com.ditecting.honeyeye.cachepool.OutputCachePool;
import com.ditecting.honeyeye.pcap4j.extension.core.TsharkMappings;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CountDownLatch;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/6/5 11:58
 */
@Slf4j
public class Storer implements Runnable{
    private OutputCachePool outputCachePool;
    private String filePath;
    private String fileName;
    private TsharkMappings.PcapFileHeader pcapFileHeader;
    private int outputtingGrain;
    private boolean start = false;
    private CountDownLatch countDownLatch;


    public Storer(String filePath, String fileName, TsharkMappings.PcapFileHeader pcapFileHeader, int outputtingGrain, OutputCachePool outputCachePool, CountDownLatch countDownLatch) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.pcapFileHeader = pcapFileHeader;
        this.outputtingGrain = outputtingGrain;
        this.outputCachePool = outputCachePool;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        log.info("Storer starts working.");

        try {
            while (true) {
                String string;
                synchronized (outputCachePool) {
                    if (outputCachePool.isEmpty()) {
                        if(!outputCachePool.getStatus()){
                            outputCachePool.wait();
                        }else {
                            log.info("Storer stops working.");
                            break;
                        }
                    }
                    string = outputCachePool.remove();
                }

                /* output strings*/
                if (string != null) {
                    if(outputtingGrain == 0) {// output pcap file
                        if(!start){
                            if(pcapFileHeader == null){
                                throw new NullPointerException("pcapFileHeader is null.");
                            }
                            byte[] pfhArray = pcapFileHeader.toByteArray();
                            byte[] dataArray = string.getBytes("ISO-8859-1");
                            byte[] rawData = new byte[pfhArray.length+dataArray.length];
                            int countLength = 0;
                            System.arraycopy(pfhArray, 0, rawData, countLength, pfhArray.length);
                            countLength += pfhArray.length;
                            System.arraycopy(dataArray, 0, rawData, countLength, dataArray.length);
                            CreateFileUtil.createPcapFile(rawData, filePath, fileName);
                            start = true;
                        }else {
                            byte[] dataArray = string.getBytes("ISO-8859-1");
                            CreateFileUtil.writePcapFile(dataArray, filePath, fileName);
                        }
                    }else {// output json file
                        if(!start){
                            StringBuilder sb = new StringBuilder();
                            sb.append("[");
                            sb.append(string);
                            sb.append("]");
                            CreateFileUtil.createJsonFile(sb.toString(), filePath, fileName);
                            start = true;
                        }else {
                            CreateFileUtil.writeJsonFile(string, filePath, fileName);
                        }
                    }
//                    log.info(string);
                }
            }
            if(countDownLatch != null){
                countDownLatch.countDown();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}