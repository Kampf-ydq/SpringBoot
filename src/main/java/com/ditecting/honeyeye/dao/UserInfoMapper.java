package com.ditecting.honeyeye.dao;

import com.ditecting.honeyeye.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserInfoMapper {

    List<UserInfo> list();
}
