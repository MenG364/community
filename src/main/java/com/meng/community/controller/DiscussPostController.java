package com.meng.community.controller;

import com.meng.community.entity.*;
import com.meng.community.event.EventProducer;
import com.meng.community.service.ICommentService;
import com.meng.community.service.IDiscussPostService;
import com.meng.community.service.ILikeService;
import com.meng.community.service.IUserService;
import com.meng.community.util.CommunityUtil;
import com.meng.community.util.HostHolder;
import com.meng.community.util.ICommunityConstant;
import com.meng.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Description: 帖子相关
 * Created by MenG on 2022/5/23 20:45
 */

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements ICommunityConstant {

    @Autowired
    private IDiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private IUserService userService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private ILikeService likeService;

    @Autowired
    private EventProducer eventProducer;


    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if (user==null) {
            return CommunityUtil.getJSONString(403,"你还没有登录");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        //触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);
        //计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey,post.getId());

        //报错的情况，将来同一处理
        return CommunityUtil.getJSONString(0,"发布成功");

    }

    /**
     *
     * 查找帖子的回复
     * 评论的分页信息
     * @param model
     * @param id 帖子id
     * @param page 分页
     * @return
     */
    @GetMapping("/detail/{id}")
    public String getDiscussPost(Model model,@PathVariable int id, Page page){
        DiscussPost discussPost = discussPostService.findDiscussPostById(id);
        model.addAttribute("post",discussPost);
        //作者
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user",user);

        //点赞
        long likeCount=likeService.findEntityLikeCount(ENTITY_TYPE_POST,id);
        model.addAttribute("likeCount",likeCount);
        //点赞状态
        int likeStatus= hostHolder.getUser()==null?0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,id);
        model.addAttribute("likeStatus",likeStatus);


        page.setLimit(5);
        page.setPath("/discuss/detail/" + id);
        page.setRows(discussPost.getCommentCount());


        //评论列表
        List<Comment> commentList = commentService.findCommentByEntity(
                ENTITY_TYPE_POST, discussPost.getId(), page.getOffset(), page.getLimit());
        //view object 显示的对象
        List<Map<String,Object>> commentVoList=new ArrayList<>();
        //添加数据
        if (commentVoList!=null){
            for(Comment comment:commentList){
                //评论VO
                HashMap<String, Object> commentVo = new HashMap<>();
                //评论
                commentVo.put("comment",comment);
                //评论的作者
                commentVo.put("user",userService.findUserById(comment.getUserId()));
                //点赞
                likeCount=likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeCount",likeCount);
                //点赞状态
                likeStatus= hostHolder.getUser()==null?0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeStatus",likeStatus);

                //回复
                List<Comment> replayList = commentService.findCommentByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);

                //回复的VO列表
                List<Map<String ,Object>> replayVoList=new ArrayList<>();
                if (replayList!=null){
                    for(Comment replay:replayList){
                        Map<String ,Object> replayVo=new HashMap<>();
                        //回复
                        replayVo.put("replay",replay);
                        //作者
                        replayVo.put("user",userService.findUserById(replay.getUserId()));
                        //回复的目标
                        User target=replay.getTargetId()==0?null:userService.findUserById(replay.getTargetId());
                        replayVo.put("target",target);
                        //点赞
                        likeCount=likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,replay.getId());
                        replayVo.put("likeCount",likeCount);
                        //点赞状态
                        likeStatus= hostHolder.getUser()==null?0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,replay.getId());
                        replayVo.put("likeStatus",likeStatus);

                        replayVoList.add(replayVo);
                    }
                }
                commentVo.put("replays",replayVoList);

                //回复的数量
                int replayCount=commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("replayCount",replayCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments",commentVoList);

        return "/site/discuss-detail";

    }


    @PutMapping("/top")
    @ResponseBody
    public String setTop(int id){
        discussPostService.updateType(id,1);

        //触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }


    //加精
    @PutMapping("/wonderful")
    @ResponseBody
    public String setWonderful(int id){
        discussPostService.updateStatus(id,1);

        //触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        //计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey,id);

        return CommunityUtil.getJSONString(0);
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public String setDelete(int id){
        discussPostService.updateStatus(id,2);

        //删帖事件
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }

}
