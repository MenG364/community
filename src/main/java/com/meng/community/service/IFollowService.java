package com.meng.community.service;

import java.util.List;
import java.util.Map;

/**
 * Description: community
 * Created by MenG on 2022/5/28 15:45
 */
public interface IFollowService {
    // 关注
    void follow(int userId, int entityType, int entityId);

    // 取消关注
    void unfollow(int userId, int entityType, int entityId);

    //查询某个用户关注的实体数量
    long findFolloweeCount(int userId, int entityType);

    //查询实体的粉丝数量
    long findFollowerCount(int entityType, int entityId);

    //查询当前用户是否已关注该实体
    boolean hasFollowed(int userId, int entityType, int entityId);

    //查询某个用户关注的人，只查人
    List<Map<String,Object>> findFollowees(int userId, int offset, int limit);

    //查询某个用户的粉丝
    List<Map<String,Object>> findFollowers(int userId, int offset, int limit);
}
