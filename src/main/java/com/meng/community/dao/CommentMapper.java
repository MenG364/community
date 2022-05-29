package com.meng.community.dao;

import com.meng.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Description: community
 * Created by MenG on 2022/5/24 14:39
 */

@Mapper
public interface CommentMapper {

    /**
     * 根据实体类型和实体ID查询评论
     * @param entityType 实体类型
     * @param entityId 实体id
     * @param offset 偏移量
     * @param limit 每一页显示的数量
     * @return 评论
     */
    List<Comment> selectCommentsByEntity(int entityType,int entityId,int offset,int limit);


    /**
     * 根据实体类型和实体ID查询评论数量
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return 数量
     */
    int selectCountByEntity(int entityType,int entityId);


    /**
     * 新增评论
     * @param comment
     * @return
     */
    int insertComment(Comment comment);

    /**
     * 根据用户id查询评论
     */
    List<Comment> selectCommentByUser(int userId,int offset,int limit);

    /**
     * 根据 userId查询评论数量
     * @param userId
     * @return
     */
    int selectCountByUser(int userId);
}
