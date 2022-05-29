package com.meng.community.service;

/**
 * Description: community
 * Created by MenG on 2022/5/27 16:41
 */
public interface ILikeService {
    //点赞
    void like(int userId, int entityType, int entityId,int entityUserId);

    //查询某实体点赞的数量
    long findEntityLikeCount(int entityType, int entityId);

    //查询某人对某实体的点赞转态
    int findEntityLikeStatus(int userId, int entityType, int entityId);

    //查询某个用户获得的赞的数量
    int findUserLikeCount(int userId);
}
