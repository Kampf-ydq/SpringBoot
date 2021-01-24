package com.ditecting.controller;

import com.ditecting.entity.*;
import com.ditecting.honeyeye.inputer.capturer.CaptureHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 在线解析控制器
 *
 * @author ydq
 * @version 1.0
 * @date 2021/1/11 11:16
 */
@Controller
public class OnlineAnalysis {

    //注入CaptureHolder
    @Autowired
    CaptureHolder captureHolder;

    @RequestMapping(value = "/online", method = RequestMethod.POST)
    public HoneyResult onlineAnalysis(@RequestBody Honeyeye honeyeye){
        //参数配置
        SystemMode sys = honeyeye.getSystem();
        Capturer capturer = honeyeye.getInputer().getCapturer();
        Listener listener = honeyeye.getListener();

        captureHolder.setOutputingMode(sys.getOutputingMode());
        captureHolder.setCount(capturer.getCount());
        captureHolder.setReadTimeout(capturer.getReadTimeout());
        captureHolder.setSnaplen(capturer.getSnaplen());
        captureHolder.setFilter(capturer.getFilter());
        captureHolder.setInterval(capturer.getInterval());
        captureHolder.setMeetingTimeout(listener.getMeetingTimeout());

        System.out.println(captureHolder);

        //调用在线分析业务层
        try {
            captureHolder.capture();
        }catch (Exception e){
            e.printStackTrace();
        }
        return HoneyResult.ok();
    }

}
