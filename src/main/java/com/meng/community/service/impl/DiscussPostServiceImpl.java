package com.meng.community.service.impl;

import com.meng.community.dao.DiscussPostMapper;
import com.meng.community.entity.DiscussPost;
import com.meng.community.service.IDiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: community
 * Created by MenG on 2022/5/20 17:51
 */

@Service
public class DiscussPostServiceImpl implements IDiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;


    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    @Override
    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }
}
