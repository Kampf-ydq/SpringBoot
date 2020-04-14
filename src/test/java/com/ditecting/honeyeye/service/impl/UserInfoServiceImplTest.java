package com.ditecting.honeyeye.service.impl;

import com.ditecting.honeyeye.domain.UserInfo;
import com.ditecting.honeyeye.service.UserInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@RunWith(SpringRunner.class)
@AutoConfigureMybatis
@SpringBootTest
class UserInfoServiceImplTest {

    @Autowired
    UserInfoService userInfoService;

    @Test
    void list() {
        System.out.println("UserInfoMapperTest");
        List<UserInfo> result = userInfoService.list();
        assertNotNull(result);
    }
}