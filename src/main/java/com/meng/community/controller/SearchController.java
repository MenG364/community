package com.meng.community.controller;

import com.meng.community.entity.DiscussPost;
import com.meng.community.entity.Page;
import com.meng.community.service.IElasticsearchService;
import com.meng.community.service.ILikeService;
import com.meng.community.service.IUserService;
import com.meng.community.util.ICommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @authoer:lrg
 * @createDate:2022/6/25
 * @description:
 */

@Controller
public class SearchController implements ICommunityConstant {
    @Autowired
    private IElasticsearchService elasticsearchService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ILikeService likeService;

    @GetMapping("/search")
    public String search(String keyword, Page page, Model model){
        //搜索帖子
        Map<String, Object> resultMap = elasticsearchService.searchDiscussPost(keyword, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts= new ArrayList<>();
        List<DiscussPost> result = (List<DiscussPost>) resultMap.get("posts");
        if (result!=null){
            for (DiscussPost discussPost:result){
                Map<String, Object> map = new HashMap<>();
                map.put("post",discussPost);
                map.put("user",userService.findUserById(discussPost.getUserId()));
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPost.getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("keyword",keyword);
        page.setPath("/search?keyword="+keyword);
        long rows = (long) resultMap.get("rows");
        page.setRows(result==null?0: (int) rows);
        return "site/search";
    }
}
