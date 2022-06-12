package com.meng.community.util;

/**
 * Description: community
 * Created by MenG on 2022/5/27 16:29
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";

    private static final String PREFIX_ENTITY_LIKE = "like:entity"; //帖子和评论统称实体
    private static final String PREFIX_USER_LIKE = "like:user"; //某个user的赞
    private static final String PREFIX_FOLLOWEE="followee";  //关注的目标
    private static final String PREFIX_FOLLOWER="follower";  //粉丝
    private static final String PREFIX_KAPTCHA="kaptcha"; //验证码
    private static final String PREFIX_TICKET="ticket"; // 登录凭证
    private static final String PREFIX_USER="user"; // 登录凭证

    //某个实体的赞
    //like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    //某个user的赞
    //like:user:userId->int
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注的实体
    // followee:userId:entityType->zset(entityId,now);
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }

    //某个实体的粉丝
    //follower:entityType:entityId->zset(userId,now)
    public static String getFollowerKey(int entityType,int entityId) {
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }

    //登录验证码
    //onwer 登录时的凭证
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA+SPLIT+owner;
    }

    //返回登录凭证
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET+SPLIT+ticket;
    }

    // 返回用户key,用于缓存用户信息
    public static String getUserKey(int userId){
        return PREFIX_USER+SPLIT+userId;
    }

}
