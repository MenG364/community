package com.meng.community.service;

import com.meng.community.entity.DiscussPost;

import java.util.List;
import java.util.Map;

/**
 * @authoer:lrg
 * @createDate:2022/6/25
 * @description:
 */
public interface IElasticsearchService {
    void saveDiscussPost(DiscussPost discussPost);

    void deleteDiscussPost(int id);

    Map<String,Object> searchDiscussPost(String keyword, int offset, int limit);

}
