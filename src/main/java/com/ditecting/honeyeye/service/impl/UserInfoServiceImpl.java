package com.ditecting.honeyeye.service.impl;

import com.ditecting.honeyeye.dao.UserInfoMapper;
import com.ditecting.honeyeye.domain.UserInfo;
import com.ditecting.honeyeye.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService{
    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public List<UserInfo> list() {
        return userInfoMapper.list();
    }
}
