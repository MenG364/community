package com.meng.community.dao;

import com.meng.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description: community
 * Created by MenG on 2022/5/20 17:02
 */

@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id,int status);

    int updateHeader(int id,String headerUrl);

    int updatePassword(int id,String password);
}
