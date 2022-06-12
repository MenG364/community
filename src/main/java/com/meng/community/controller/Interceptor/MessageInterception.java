package com.meng.community.controller.Interceptor;

import com.meng.community.entity.User;
import com.meng.community.service.IMessageService;
import com.meng.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: community
 * Created by MenG on 2022/6/2 19:26
 */
@Component
public class MessageInterception implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private IMessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user!=null){
            int letterUnreadCount=messageService.findLetterUnreadCount(user.getId(),null);
            int noticeUnreadCount=messageService.findNoticeUnreadCount(user.getId(),null);
            modelAndView.addObject("allUnreadCount",letterUnreadCount+noticeUnreadCount);
        }

    }
}
