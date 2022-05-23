package com.meng.community.util;

/**
 * Description: community
 * Created by MenG on 2022/5/21 15:06
 */
public interface ICommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS=0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT=1;

    /**
     * 重复失败
     */
    int ACTIVATION_FAILURE=2;

    /**
     * 默认状态的登录凭证的超时时间
     */
    int DEFAULT_EXPIRED_SECOND=3600*12;

    /**
     * 记住转态下的登录凭证超时时间
     */
    int REMEMBER_EXPIRED_SECOND=3600*24*100;
}
