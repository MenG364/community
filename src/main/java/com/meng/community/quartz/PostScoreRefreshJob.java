package com.meng.community.quartz;

import com.meng.community.entity.DiscussPost;
import com.meng.community.service.IDiscussPostService;
import com.meng.community.service.IElasticsearchService;
import com.meng.community.service.ILikeService;
import com.meng.community.util.ICommunityConstant;
import com.meng.community.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @authoer: MenG364
 * @createDate:2022/7/6
 * @description:
 */

@Slf4j(topic = "PostScoreRefreshJob.class")
public class PostScoreRefreshJob implements Job, ICommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IDiscussPostService discussPostService;

    @Autowired
    private ILikeService likeService;

    @Autowired
    private IElasticsearchService elasticsearchService;

    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化牛客纪元失败:" + e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);
        if (operations.size() == 0) {
            log.info("【任务取消】没有需要刷新的帖子");
            return;
        }

        log.info("【任务开始】正在刷新帖子分数: " + operations.size());
        while (operations.size() > 0) {
            this.refresh((Integer) operations.pop());
        }

        log.info("【任务结束】帖子刷新完毕！" + operations.size());

    }

    private void refresh(int postId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if (post==null){
            log.error("该贴子不存在: id = "+postId);
            return;
        }
        //是否加精
        boolean wonderful = post.getStatus() == 1;
        //评论数量
        int commentCount = post.getCommentCount();
        //点赞数量
        long likeCount = likeService.findEntityLikeCount(post.getType(), postId);

        //计算权重
        double w=(wonderful?75:0)+commentCount*10+likeCount*2;
        //分数
        double score=Math.log10(Math.max(w,1))+(post.getCreateTime().getTime()-epoch.getTime())/(1000*3600*24);
        //更新帖子分数
        discussPostService.updateScore(postId,score);
        //同步搜索数据
        post.setScore(score);
        elasticsearchService.saveDiscussPost(post);

    }
}
