package com.neu.websocket.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.HashMap;

/*
 * WebSocket 控制类
 *
 */

@Slf4j
@Controller
@RequestMapping("/webSocketCtrl")
public class WebSocketController {

    private static Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @RequestMapping("/hello")
    public String helloHtml(HashMap<String, Object> map) {
        map.put("hello", "这是一个thymeleaf页面");
        for (int i = 0; i < 100; i++) {
            logger.info("Generate info log " + i);
        }
        return "MyHtml";
    }

}
