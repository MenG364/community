package com.meng.community.controller;

import com.meng.community.annotation.LoginRequired;
import com.meng.community.entity.Event;
import com.meng.community.entity.User;
import com.meng.community.event.EventProducer;
import com.meng.community.service.ILikeService;
import com.meng.community.util.CommunityUtil;
import com.meng.community.util.HostHolder;
import com.meng.community.util.ICommunityConstant;
import com.meng.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: community
 * Created by MenG on 2022/5/27 16:40
 */

@Controller
public class LikeController implements ICommunityConstant {

    @Autowired
    private ILikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @LoginRequired
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId,int postId){
        User user = hostHolder.getUser();
        //点赞
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        //数量
        long likeCount=likeService.findEntityLikeCount(entityType,entityId);
        //转态
        int likeStatus=likeService.findEntityLikeStatus(user.getId(),entityType,entityId);

        Map<String,Object> map=new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        // 触发点赞事件
        if (likeStatus==1){
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);
            eventProducer.fireEvent(event);
        }

        if (entityType==ENTITY_TYPE_POST){
            //计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey,postId);
        }

        return CommunityUtil.getJSONString(0,null,map);

    }
}
