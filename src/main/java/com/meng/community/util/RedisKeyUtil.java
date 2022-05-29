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

}