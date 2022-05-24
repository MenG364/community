package com.meng.community.dao;

import com.meng.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description: 帖子
 * Created by MenG on 2022/5/20 16:38
 */
@Mapper
public interface DiscussPostMapper {

    /**
     * 根据userid查询帖子
     * @param userId 用户id
     * @return 帖子数组
     */
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

    /**
     *  根据userid查询帖子数量
     * @param userId
     * @return
     */
    //@Param注解用于给参数取别名
    //如果只有一个参数，并且在<if>里使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 增加帖子
     * @param discussPost
     * @return
     */
    int insertDiscussPost(DiscussPost discussPost);

    /**
     * 查询帖子详情
     * @param id
     * @return
     */
    DiscussPost selectDiscussPostById(int id);
}
