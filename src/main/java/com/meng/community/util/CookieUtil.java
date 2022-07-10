package com.meng.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Description: community
 * Created by MenG on 2022/5/22 14:22
 */
public class CookieUtil {

    public static String getValue(HttpServletRequest request, String name){
        if (request==null||name==null) throw new IllegalArgumentException("参数为空！");
        Cookie[] cookies = request.getCookies();
        if (cookies!=null){
            for(Cookie cookie:cookies){
                if (cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
