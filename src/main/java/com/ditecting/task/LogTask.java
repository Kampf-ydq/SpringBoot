package com.ditecting.task;


import com.ditecting.entity.LoggerMessage;
import com.ditecting.util.LoggerQueue;
import com.detecting.service.WebSocketServer;
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
                    webSocketServer.sendMessage(log.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
