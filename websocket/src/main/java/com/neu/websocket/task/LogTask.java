package com.neu.websocket.task;

import com.neu.websocket.entity.LoggerMessage;
import com.neu.websocket.service.WebSocketServer;
import com.neu.websocket.util.LoggerQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling  //开启定时任务
public class LogTask {

    @Autowired
    private WebSocketServer webSocketServer;

    /***
     * initialDelay是在程序启动过后1000ms触发这个任务，fixedDelay是每隔1000ms执行一次定时任务
     */
    @Scheduled(initialDelay = 1000, fixedDelay = 100)
    public void log2Client(){
        //System.out.println("logTask begin...");
        try {
            if (!WebSocketServer.sessionMap.isEmpty()) {
                LoggerMessage log = LoggerQueue.getInstance().poll();
                if (log != null) {
                    //调用格式化函数

                    webSocketServer.sendMessage(log.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //对日志进行格式化, PS：注意，这里要根据日志生成规则来操作
    private void logFormate(String line){

    }
}
