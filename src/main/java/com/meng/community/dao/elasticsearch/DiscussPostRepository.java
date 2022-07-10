package com.meng.community.dao.elasticsearch;

import com.meng.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @authoer:lrg
 * @createDate:2022/6/25
 * @description:
 */

@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {

}
