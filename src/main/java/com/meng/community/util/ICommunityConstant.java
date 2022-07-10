package com.meng.community.util;

/**
 * Description: community
 * Created by MenG on 2022/5/21 15:06
 */
public interface ICommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 重复失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认状态的登录凭证的超时时间
     */
    int DEFAULT_EXPIRED_SECOND = 3600 * 12;

    /**
     * 记住转态下的登录凭证超时时间
     */
    int REMEMBER_EXPIRED_SECOND = 3600 * 24 * 100;

    /**
     * 实体类型，帖子
     */

    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型。评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型。用户
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * 事件主题：评论
     */
    String TOPIC_COMMENT="comment";

    /**
     * 事件主题：点赞
     */
    String TOPIC_LIKE="like";

    /**
     * 事件主题：关注
     */
    String TOPIC_FOLLOW="FOLLOW";

    /**
     * 事件主题：发帖
     */
    String TOPIC_PUBLISH="publish";

    /**
     * 事件主题：删除
     */
    String TOPIC_DELETE="delete";


    /**
     * 系统用户ID
     */
    int SYSTEM_USER_ID=1;

    /**
     * 普通用户
     */
    String AUTHORITY_USER="user";

    /**
     * 管理员
     */
    String AUTHORITY_ADMIN="admin";

    /**
     * 版主
     */
    String AUTHORITY_MODERATOR="moderator";

}
