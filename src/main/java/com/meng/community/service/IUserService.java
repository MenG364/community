package com.meng.community.service;

import com.meng.community.entity.LoginTicket;
import com.meng.community.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

/**
 * Description: community
 * Created by MenG on 2022/5/20 17:54
 */


public interface IUserService {

    User findUserById(int id);

    User findUserByEmail(String email);

    User findUserByName(String username);

    Map<String,Object> register(User user);

    int activation(int userId, String code);

    Map<String,Object> login(String username, String password, int expiredSeconds);

    void logout(String ticket);

    LoginTicket findLoginTicket(String ticket);

    int updateHeader(int userid, String headerUrl);

    //重置密码
    Map<String,Object> resetPassword(String email, String newPassword);

    Map<String,Object> updatePassword(int userid, String oldPassword, String newPassword);

    Collection<? extends GrantedAuthority> getAuthorities(int userId);
}
