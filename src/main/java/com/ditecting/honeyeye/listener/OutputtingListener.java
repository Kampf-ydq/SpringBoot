package com.ditecting.honeyeye.listener;

import com.ditecting.honeyeye.cachepool.OutputCachePool;
import com.ditecting.honeyeye.outputer.storage.*;
import com.ditecting.honeyeye.pcap4j.extension.core.TsharkMappings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@Component
@Slf4j
public class OutputtingListener {

    /* outputing grain, including: -1:do not output, 0:rawPacket, 1:packet, 2:flow, 3:session */
    @Value("${honeyeye.listener.outputtingGrain}")
    private int outputtingGrain;

    @Value("${honeyeye.system.inputingMode}")
    private int inputingMode;// 1:capture, 2:load

    @Value("${honeyeye.outputer.filePath}")
    private String filePath;

    @Value("${honeyeye.outputer.fileName}")
    private String fileName;

    @Autowired
    private OutputCachePool outputCachePool;

    public void gotOutputCachePool(TsharkMappings.PcapFileHeader pcapFileHeader, CountDownLatch countDownLatch){
        Storer storer = new Storer(filePath, fileName, pcapFileHeader, outputtingGrain, outputCachePool, countDownLatch);
        Thread thread = new Thread(storer);
        thread.start();
    }

    //添加getter、setter
    public int getOutputtingGrain() {
        return outputtingGrain;
    }

    public void setOutputtingGrain(int outputtingGrain) {
        this.outputtingGrain = outputtingGrain;
    }

    public int getInputingMode() {
        return inputingMode;
    }

    public void setInputingMode(int inputingMode) {
        this.inputingMode = inputingMode;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "OutputtingListener{" +
                "outputtingGrain=" + outputtingGrain +
                ", inputingMode=" + inputingMode +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
