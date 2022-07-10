package com.meng.community;

import com.alibaba.fastjson.JSONObject;
import com.meng.community.dao.DiscussPostMapper;
import com.meng.community.dao.elasticsearch.DiscussPostRepository;
import com.meng.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

/**
 * @authoer:lrg
 * @createDate:2022/6/25
 * @description:
 */

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTests {

    @Autowired
    private DiscussPostMapper discussMapper;

    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void testInsert(){
        discussRepository.save(discussMapper.selectDiscussPostById(241));
        discussRepository.save(discussMapper.selectDiscussPostById(242));
        discussRepository.save(discussMapper.selectDiscussPostById(243));
    }

    @Test
    public void testInsertList(){
        discussRepository.saveAll(discussMapper.selectDiscussPosts(101,0,100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(102,0,100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(103,0,100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(111,0,100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(112,0,100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(131,0,100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(132,0,100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(133,0,100,0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(134,0,100,0));
    }

    @Test
    public void testUpdate(){
        DiscussPost post = discussMapper.selectDiscussPostById(231);
        post.setContent("我是新人，使劲灌水");
        discussRepository.save(post);
    }

    @Test
    public void testSearchByRepository(){
        SearchRequest searchRequest = new SearchRequest("discusspost");//discusspost是索引名，就是表名
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(1)
                .size(10)
                .highlighter(
                        new HighlightBuilder().field("title")
                                .field("content")
                                .requireFieldMatch(false)
                                .preTags("<span style='color: red'>")
                                .postTags("</span>")
                );
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            System.out.println(hits.getTotalHits());
            System.out.println(response.getTook());
            for(SearchHit hit:hits){
                DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);
                HighlightField title = hit.getHighlightFields().get("title");
                if (title != null) {
                    discussPost.setTitle(title.getFragments()[0].toString());
                }
                HighlightField contentField = hit.getHighlightFields().get("content");
                if (contentField != null) {
                    discussPost.setContent(contentField.getFragments()[0].toString());
                }
                System.out.println(discussPost);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
