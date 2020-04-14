package com.ditecting.honeyeye.dao;

import com.ditecting.honeyeye.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@Mapper
public interface UserInfoMapper {

    List<UserInfo> list();
}
