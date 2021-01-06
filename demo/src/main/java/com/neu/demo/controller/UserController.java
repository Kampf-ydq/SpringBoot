package com.neu.demo.controller;

import com.neu.demo.pojo.User;
import com.neu.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 *
 * @author ydq
 * @version 1.0
 * @date 2021/1/5 10:57
 */
@RestController
public class UserController {

    /*//获取.yml文件中的值
    @Value("${name}")
    private String name;

    @Value("${content}")
    private String content;

    @RequestMapping("/hello")
    public String hello(){
        return "Hello, " + name + " Welcome to " + content + "!";
    }*/

    @Autowired
    private User user;

    @Autowired
    private UserService userService;

    @RequestMapping("/star")
    public String getUser(){
        return userService.UserSay(user.getName(), user.getContent());
    }
}
