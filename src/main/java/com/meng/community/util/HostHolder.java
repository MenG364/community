package com.meng.community.util;

import com.meng.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * Description: 持有用户信息，用于代替session对象
 * Created by MenG on 2022/5/22 14:33
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users=new ThreadLocal<User>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }

}
