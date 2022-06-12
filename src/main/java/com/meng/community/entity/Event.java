package com.meng.community.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: community
 * Created by MenG on 2022/6/2 14:39
 */
@Data
public class Event {

    private String topic; //事件的主题
    private int userId; //触发事件的用户
    private int entityType; //实体类型
    private int entityId; //指向的目标，具体是哪个帖子或哪个评论
    private int entityUserId; //帖子作者

    private Map<String,Object> data=new HashMap<>(); //其他字段

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Event setData(String key,Object value) {
        data.put(key,value);
        return this;
    }
}
