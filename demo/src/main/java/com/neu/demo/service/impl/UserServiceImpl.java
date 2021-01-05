package com.neu.demo.service.impl;

import com.neu.demo.service.UserService;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author ydq
 * @version 1.0
 * @date 2021/1/5 19:35
 */
@Service
public class UserServiceImpl implements UserService {
    @Override
    public String UserSay(String name, String content) {
        return "The Star Desc: " + name + "# "+ content;
    }
}
