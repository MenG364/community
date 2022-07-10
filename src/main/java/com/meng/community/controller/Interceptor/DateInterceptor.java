package com.meng.community.controller.Interceptor;

import com.meng.community.entity.User;
import com.meng.community.service.IDateService;
import com.meng.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @authoer: MenG364
 * @createDate:2022/7/5
 * @description:
 */

@Component
public class DateInterceptor implements HandlerInterceptor {

    @Autowired
    private IDateService dateService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计UV
        String ip = request.getRemoteHost();
        dateService.recordUV(ip);

        //统计DAU
        User user = hostHolder.getUser();
        if (user!=null){
            dateService.recordDAU(user.getId());
        }
        return true;
    }
}
