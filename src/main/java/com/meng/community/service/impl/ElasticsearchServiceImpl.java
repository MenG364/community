package com.meng.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.meng.community.dao.elasticsearch.DiscussPostRepository;
import com.meng.community.entity.DiscussPost;
import com.meng.community.service.IElasticsearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @authoer:lrg
 * @createDate:2022/6/25
 * @description:
 */
@Service
public class ElasticsearchServiceImpl implements IElasticsearchService {
    @Autowired
    DiscussPostRepository discussPostRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void saveDiscussPost(DiscussPost discussPost){
        discussPostRepository.save(discussPost);
    }

    @Override
    public void deleteDiscussPost(int id){
        discussPostRepository.deleteById(id);
    }

    @Override
    public Map<String,Object> searchDiscussPost(String keyword, int offset, int limit) {
        Map<String, Object> map = new HashMap<>();
        SearchRequest searchRequest = new SearchRequest("discusspost");//discusspost是索引名，就是表名
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(offset)
                .size(limit)
                .highlighter(
                        new HighlightBuilder().field("title")
                                .field("content")
                                .requireFieldMatch(false)
                                .preTags("<em>")
                                .postTags("</em>")
                );
        searchRequest.source(searchSourceBuilder);
        List<DiscussPost> discussPosts;
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            discussPosts = new ArrayList<>();
            for (SearchHit hit : hits) {
                DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);
                HighlightField title = hit.getHighlightFields().get("title");
                if (title != null) {
                    discussPost.setTitle(title.getFragments()[0].toString());
                }
                HighlightField contentField = hit.getHighlightFields().get("content");
                if (contentField != null) {
                    discussPost.setContent(contentField.getFragments()[0].toString());
                }
                discussPosts.add(discussPost);
            }
            map.put("posts",discussPosts);
            map.put("rows",hits.getTotalHits().value);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return map;
    }




}
