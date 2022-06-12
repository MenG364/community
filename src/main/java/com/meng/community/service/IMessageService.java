package com.meng.community.service;

import com.meng.community.entity.Message;

import java.util.List;

/**
* @author lrg
* @description 针对表【message】的数据库操作Service
* @createDate 2022-05-25 19:58:41
*/
public interface IMessageService {

    Message findLetterById(int id);

    List<Message> findConversations(int userId, int offset, int limit);

    int findConversationCount(int userId);

    List<Message> findLetters(String conversationId, int offset, int limit);

    int findLetterCount(String conversationId);

    int findLetterUnreadCount(int userId, String conversationId);

    int addMessage(Message message);

    int readMessage(List<Integer> ids);

    int deleteMessage(int id);

    Message findLatestNotice(int userid, String topic);

    int findNoticeCount(int userid, String topic);

    int findNoticeUnreadCount(int userid, String topic);

    List<Message> findNotices(int userId, String topic, int offset, int limit);
}
