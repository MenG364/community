package com.meng.community.service;

import com.meng.community.entity.DiscussPost;

import java.util.List;

/**
 * Description: community
 * Created by MenG on 2022/5/20 17:50
 */
public interface IDiscussPostService {

    List<DiscussPost> findDiscussPosts(int userId,int offset,int limit,int orderMode);

    int findDiscussPostRows(int userId);

    int addDiscussPost(DiscussPost discussPost);

    DiscussPost findDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);

    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int updateScore(int id, double score);
}
