package com.meng.community.service.impl;

import com.meng.community.dao.CommentMapper;
import com.meng.community.entity.Comment;
import com.meng.community.service.ICommentService;
import com.meng.community.service.IDiscussPostService;
import com.meng.community.util.ICommunityConstant;
import com.meng.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
* @author lrg
* @description 针对表【comment】的数据库操作Service实现
* @createDate 2022-05-24 14:36:57
*/
@Service
public class CommentServiceImpl implements ICommentService, ICommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private IDiscussPostService discussPostService;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit){
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    @Override
    public int findCommentCount(int entityType, int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    /**
     *  需要事务
     * @param comment
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    @Override
    public int addComment(Comment comment){
        if (comment==null){
            throw new IllegalArgumentException("参数不能为空");
        }

        //内容过滤
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        //添加评论
        int rows = commentMapper.insertComment(comment);

        //更新帖子评论的数量
        if (comment.getEntityType()==ENTITY_TYPE_POST){
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }

        return rows;

    }

}




