package com.neu.demo.test;

import com.neu.demo.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 尝试Junit测试开发
 *
 * @author ydq
 * @version 1.0
 * @date 2021/1/5 19:45
 */
@SpringBootTest
public class UserTest {
    @Autowired
    private User user;
    @Test
    public void testObtainUser(){
        System.out.println(user);
    }
}
