package com.meng.community.service.impl;

import com.meng.community.dao.MessageMapper;
import com.meng.community.entity.Message;
import com.meng.community.service.IMessageService;
import com.meng.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

/**
* @author lrg
* @description 针对表【message】的数据库操作Service实现
* @createDate 2022-05-25 19:58:41
*/
@Service
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public Message findLetterById(int id){
        return messageMapper.selectLetterById(id);
    }

    @Override
    public List<Message> findConversations(int userId, int offset, int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }

    @Override
    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    @Override
    public List<Message> findLetters(String conversationId, int offset, int limit){
        return messageMapper.selectLetters(conversationId,offset,limit);
    }

    @Override
    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    @Override
    public int findLetterUnreadCount(int userId, String conversationId){
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }

    @Override
    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));

        return messageMapper.insertMessage(message);
    }

    @Override
    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids,1);
    }

    @Override
    public int deleteMessage(int id){
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(id);
        return messageMapper.updateStatus(ids,2);
    }

    @Override
    public Message findLatestNotice(int userid, String topic){
        return messageMapper.selectLatestNotice(userid, topic);
    }

    @Override
    public int findNoticeCount(int userid, String topic){
        return messageMapper.selectNoticeCount(userid,topic);
    }

    @Override
    public int findNoticeUnreadCount(int userid, String topic){
        return messageMapper.selectNoticeUnreadCount(userid,topic);
    }

    @Override
    public List<Message> findNotices(int userId,String topic,int offset,int limit){
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }
}




