package com.meng.community.controller;

import com.meng.community.entity.DiscussPost;
import com.meng.community.entity.User;
import com.meng.community.service.IDiscussPostService;
import com.meng.community.service.IUserService;
import com.meng.community.service.impl.DiscussPostServiceImpl;
import com.meng.community.util.CommunityUtil;
import com.meng.community.util.HostHolder;
import lombok.experimental.Delegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Description: 帖子相关，登陆之后
 * Created by MenG on 2022/5/23 20:45
 */

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private IDiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private IUserService userService;

    @PostMapping
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if (user==null) {
            return CommunityUtil.getJSONString(403,"你好没有登录");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        //报错的情况，将来同一处理
        return CommunityUtil.getJSONString(0,"发布成功");

    }

    @GetMapping("/detail/{id}")
    public String getDiscussPost(@PathVariable int id, Model model){
        DiscussPost discussPost = discussPostService.findDiscussPost(id);
        model.addAttribute("post",discussPost);
        //作者
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user",user);

        //查找帖子的回复

        return "/site/discuss-detail";

    }
}
