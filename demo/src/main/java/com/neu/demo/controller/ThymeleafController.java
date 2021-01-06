package com.neu.demo.controller;

import com.neu.demo.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Thymeleaf模板渲染测试控制器
 *    此处切记不能使用@RestController，因为这个注解使用流的形式进行返回数据。
 * @author ydq
 * @version 1.0
 * @date 2021/1/6 10:14
 */
@Controller
public class ThymeleafController {

    @Autowired
    private User user;

    @RequestMapping(value = "show", method = RequestMethod.GET)
    public String starShow(Model model){
        model.addAttribute("theme", "体育运动员");
        model.addAttribute("star",user);
        return "show";
    }
}
