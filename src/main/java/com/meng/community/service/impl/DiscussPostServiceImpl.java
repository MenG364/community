package com.meng.community.service.impl;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.meng.community.dao.DiscussPostMapper;
import com.meng.community.entity.DiscussPost;
import com.meng.community.service.IDiscussPostService;
import com.meng.community.util.SensitiveFilter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Description: community
 * Created by MenG on 2022/5/20 17:51
 */

@Slf4j(topic = "DiscussPostServiceImpl.class")
@Service
public class DiscussPostServiceImpl implements IDiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

//    Caffeine核心接口：Cache，LoadingCache，AsyncLoadingCache

    private LoadingCache<String,List<DiscussPost>> postListCache;

    private LoadingCache<Integer,Integer> postRowsCache;

    //初始化缓存
    @PostConstruct
    public void init(){
        postListCache= Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(@NonNull String key) throws Exception {
                        //查数据库
                        if (key==null||key.length()==0){
                            throw new IllegalArgumentException("参数为空");
                        }
                        String[] params = key.split(":");
                        if (params==null||params.length!=2){
                            throw new IllegalArgumentException("参数错误");
                        }
                        int offset= Integer.parseInt(params[0]);
                        int limit= Integer.parseInt(params[1]);

                        // 二级缓存 -->redis -->DB

                        log.debug("load post list from DB");
                        return discussPostMapper.selectDiscussPosts(0,offset,limit,1);
                    }
                });

        postRowsCache=Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds,TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull Integer key) throws Exception {
                        log.debug("load post rows from DB");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit,int orderMode) {
        if (userId==0&&orderMode==1){
            //启用缓存
            return postListCache.get(offset+":"+limit);
        }
        log.debug("load post list from DB");
        return discussPostMapper.selectDiscussPosts(userId,offset,limit,orderMode);
    }

    @Override
    public int findDiscussPostRows(int userId) {
        if (userId==0){
            return postRowsCache.get(userId);
        }
        log.debug("load post rows from DB");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public int addDiscussPost(DiscussPost post){
        if (post==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //转移HTML标签
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    @Override
    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    @Override
    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updateCommentCount(id,commentCount);
    }

    @Override
    public int updateType(int id, int type){
        return discussPostMapper.updateType(id,type);
    }

    @Override
    public int updateStatus(int id,int status){
        return discussPostMapper.updateStatus(id,status);
    }

    @Override
    public int updateScore(int id,double score){
        return discussPostMapper.updateScore(id,score);
    }
}
