package com.neu.websocket.controller;

import com.neu.websocket.service.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;

/*
 * WebSocket 控制类
 *
 */

@Slf4j
@Controller
@RequestMapping("/webSocketCtrl")
public class WebSocketController {
    /*@ResponseBody
    @RequestMapping(value = "/pushMessageToWeb", method = RequestMethod.POST, consumes = "application/json")
    public String pushMessageToWeb(@RequestBody String message) {
        try {
            WebSocketServer.sendInfo("有新内容：" + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }*/

    @RequestMapping("/hello")
    public String helloHtml(HashMap<String, Object> map) {
        map.put("hello", "这是一个thymeleaf页面");
        return "MyHtml";
    }

}
