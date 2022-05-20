package com.meng.community;

import com.meng.community.dao.DiscussPostMapper;
import com.meng.community.dao.UserMapper;
import com.meng.community.entity.DiscussPost;
import com.meng.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * Description: community
 * Created by MenG on 2022/5/20 17:18
 */


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

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
}
