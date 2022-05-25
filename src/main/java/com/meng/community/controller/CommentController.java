package com.meng.community.controller;

import com.meng.community.entity.Comment;
import com.meng.community.service.ICommentService;
import com.meng.community.util.HostHolder;
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
public class CommentController {

    @Autowired
    private ICommentService commentService;

    @Autowired
    private HostHolder hostHolder;

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

        return "redirect:/discuss/detail/"+discussPostId;
    }
}
