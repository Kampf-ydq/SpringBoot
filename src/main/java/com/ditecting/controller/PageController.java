package com.ditecting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/***
 * 页面跳转控制器
 */
@Controller
public class PageController {
    @RequestMapping("/")
    public String page(){
        return "main";
    }

    /*@RequestMapping("/main")
    public String getMainPage(){
        return "s";
    }*/

    @RequestMapping("/monitorPage")
    public String getMonitor(){
        return "monitor";
    }

    @RequestMapping("/onlinePage")
    public String test(){
        return "online";
    }


    @RequestMapping("/offlinePage")
    public String getOffline(){
        return "offline";
    }
}
