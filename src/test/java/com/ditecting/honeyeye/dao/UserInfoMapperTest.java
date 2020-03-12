package com.ditecting.honeyeye.dao;

import com.ditecting.honeyeye.domain.UserInfo;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@AutoConfigureMybatis
@SpringBootTest
class UserInfoMapperTest {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Test
    void findByUserInfoId() {
        System.out.println("UserInfoMapperTest");
        List<UserInfo> result = userInfoMapper.list();
        assertNotNull(result);
    }
}