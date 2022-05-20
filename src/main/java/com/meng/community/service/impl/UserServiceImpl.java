package com.meng.community.service.impl;

import com.meng.community.dao.UserMapper;
import com.meng.community.entity.User;
import com.meng.community.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: community
 * Created by MenG on 2022/5/20 17:54
 */

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }
}
