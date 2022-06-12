package com.meng.community.controller;

import com.meng.community.entity.Comment;
import com.meng.community.entity.DiscussPost;
import com.meng.community.entity.Event;
import com.meng.community.event.EventConsumer;
import com.meng.community.event.EventProducer;
import com.meng.community.service.ICommentService;
import com.meng.community.service.IDiscussPostService;
import com.meng.community.util.HostHolder;
import com.meng.community.util.ICommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * Description: community
 * Created by MenG on 2022/5/25 14:53
 */

@Controller
@RequestMapping("/comment")
public class CommentController implements ICommunityConstant {

    @Autowired
    private ICommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private IDiscussPostService discussPostService;


    /**
     *
     * @param discussPostId 重定向的帖子id
     * @param comment 评论
     * @return
     */
    @PostMapping("/{discussPostId}")
    public String addComment(@PathVariable int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        if (comment.getTargetId()==null){
            comment.setTargetId(0);
        }
        commentService.addComment(comment);

        // 触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",discussPostId);
        if (comment.getEntityType()==ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if (comment.getEntityType()==ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        return "redirect:/discuss/detail/"+discussPostId;
    }
}
