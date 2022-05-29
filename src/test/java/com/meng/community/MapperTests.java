package com.meng.community;

import com.meng.community.dao.DiscussPostMapper;
import com.meng.community.dao.LoginTicketMapper;
import com.meng.community.dao.MessageMapper;
import com.meng.community.dao.UserMapper;
import com.meng.community.entity.DiscussPost;
import com.meng.community.entity.LoginTicket;
import com.meng.community.entity.Message;
import com.meng.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;

/**
 * Description: community
 * Created by MenG on 2022/5/20 17:18
 */


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void TestSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);
    }

    @Test
    public void TestSelectPosts(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for(DiscussPost d:discussPosts){
            System.out.println(d);
        }


        int i = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(i);

    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc",1);
        System.out.println(loginTicketMapper.selectByTicket("abc"));
    }

    @Test
    public void testSelectLetters(){
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for(Message message:messages){
            System.out.println(message);
        }
        System.out.println(messageMapper.selectConversationCount(111));

        List<Message> messages1 = messageMapper.selectLetters("111_112", 0, 10);
        for(Message message:messages1){
            System.out.println(message);
        }

        System.out.println(messageMapper.selectLetterCount("111_112"));
        System.out.println(messageMapper.selectLetterUnreadCount(131, "111_131"));
    }

}
