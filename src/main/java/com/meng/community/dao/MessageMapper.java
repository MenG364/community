package com.meng.community.dao;

import com.meng.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author lrg
* @description 针对表【message】的数据库操作Mapper
* @createDate 2022-05-25 19:58:41
* @Entity com.meng.community.entity.Message
*/

@Mapper
public interface MessageMapper {

    /**
     * 查询当前用户的会话列表，针对每个会话只返回最后一条私信
     * @param userId 当前的登录用户Id
     * @param offset 分页，偏移值
     * @param limit 分页，一页值
     * @return 最后一条私信
     */
    List<Message> selectConversations(int userId,int offset,int limit);


    /**
     * 查询当前用户的会话数量
     * @param userId
     * @return
     */
    int selectConversationCount(int userId);

    /**
     * 查询某个会话所包含的私信列表
     * @param conversationId 会话id
     * @param offset 分页，偏移值
     * @param limit 分页，一页值
     * @return
     */
    List<Message> selectLetters(String conversationId,int offset,int limit);

    //查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    /**
     * 查询未读私信的数量，若conversationId为null，则查询当前用户所有未读信息量，否则，查询该会话id的未读量
     * @param userId 前的登录用户Id
     * @param conversationId 会话id
     * @return
     */
    int selectLetterUnreadCount(int userId,String conversationId);

    /**
     * 新增一个消息
     * @param message 消息
     * @return
     */
    int insertMessage(Message message);

    /**
     * 修改消息的状态
     * @param ids
     * @param status
     * @return
     */
    int updateStatus(List<Integer> ids,int status);

    /**
     * 根据id查询消息
     * @param id
     * @return
     */
    Message selectLetterById(int id);
}




