package com.meng.community.event;

import com.alibaba.fastjson.JSONObject;
import com.meng.community.dao.elasticsearch.DiscussPostRepository;
import com.meng.community.entity.DiscussPost;
import com.meng.community.entity.Event;
import com.meng.community.entity.Message;
import com.meng.community.service.IDiscussPostService;
import com.meng.community.service.IElasticsearchService;
import com.meng.community.service.IMessageService;
import com.meng.community.util.ICommunityConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: community
 * Created by MenG on 2022/6/2 14:50
 */

@Slf4j(topic = "EventConsumer.class")
@Component
public class EventConsumer implements ICommunityConstant {

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IDiscussPostService discussPostService;

    @Autowired
    private IElasticsearchService elasticsearchService;

    /**
     * 消费评论事件
     * @param record
     */
    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record){
        if (record==null||record.value()==null){
            log.error("消息的内容为空");
            return;
        }

        Event event= JSONObject.parseObject(record.value().toString(),Event.class);
        if (event==null){
            log.error("消息格式错误");
            return;
        }

        //发送站内通知
        Message message=new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setStatus(0);
        message.setCreateTime(new Date());

        Map<String,Object> content=new HashMap<>(event.getData());
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());

        message.setContent(JSONObject.toJSONString(content));

        messageService.addMessage(message);
    }

    /**
     * 消费发帖事件
     */
    @KafkaListener(topics = TOPIC_PUBLISH)
    public void handlePublishMessage(ConsumerRecord record){
        if (record==null||record.value()==null){
            log.error("消息的内容为空");
            return;
        }

        Event event= JSONObject.parseObject(record.value().toString(),Event.class);
        if (event==null){
            log.error("消息格式错误");
            return;
        }
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);
    }

    /**
     * 消费删帖事件
     */
    @KafkaListener(topics = TOPIC_DELETE)
    public void handleDeleteMessage(ConsumerRecord record){
        if (record==null||record.value()==null){
            log.error("消息的内容为空");
            return;
        }

        Event event= JSONObject.parseObject(record.value().toString(),Event.class);
        if (event==null){
            log.error("消息格式错误");
            return;
        }
        elasticsearchService.deleteDiscussPost(event.getEntityId());
    }

}
