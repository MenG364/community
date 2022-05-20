package com.meng.community.service;

import com.meng.community.entity.User;

/**
 * Description: community
 * Created by MenG on 2022/5/20 17:54
 */


public interface IUserService {

    User findUserById(int id);
}
