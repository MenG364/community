package com.meng.community.service;

import com.meng.community.entity.Comment;

import java.util.List;

/**
 * Description: community
 * Created by MenG on 2022/5/24 14:49
 */
public interface ICommentService {
    List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit);

    List<Comment> findUserComment(int userId, int offset, int limit);

    int findCommentCount(int entityType, int entityId);

    int findUserCount(int userId);

    int addComment(Comment comment);

    Comment findCommentById(int id);
}
