package com.meng.community.controller.Interceptor;

import com.meng.community.entity.LoginTicket;
import com.meng.community.entity.User;
import com.meng.community.service.IUserService;
import com.meng.community.util.CookieUtil;
import com.meng.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Description: 登录凭证拦截器
 * Created by MenG on 2022/5/22 14:19
 */

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private IUserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //从cookie获取登录凭证
        String ticket= CookieUtil.getValue(request,"ticket");
        if (ticket!=null){
            //如果登录凭证存在，查询LoginTicket
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //检查凭证是否有效
            if (loginTicket!=null&&loginTicket.getStatus()==0&& loginTicket.getExpired().after(new Date())){
                //根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                //在本次请求持有User，采用ThreadLocal
                hostHolder.setUser(user);

            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user!=null&& modelAndView!=null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
