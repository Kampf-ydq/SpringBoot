package com.detecting.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * WebSocket服务
 */
@Slf4j
@ServerEndpoint(value = "/websocket")
@Component
public class WebSocketServer {

    public static Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        // Add current session to map which name is logSession
        sessionMap.put("logSession", session);
        try {
            sendMessage("连接成功");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @OnClose
    public void onClose() {
        // Remove the session which name is logSession
        sessionMap.remove("logSession");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // TODO You can add handle in it.
        System.out.println(message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) throws IOException {
        sessionMap.get("logSession").getBasicRemote().sendText(message);
    }

}
