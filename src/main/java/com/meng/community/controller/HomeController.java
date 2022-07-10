package com.meng.community.controller;

import com.meng.community.entity.DiscussPost;
import com.meng.community.entity.Page;
import com.meng.community.service.IDiscussPostService;
import com.meng.community.service.ILikeService;
import com.meng.community.service.IUserService;
import com.meng.community.util.ICommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: community
 * Created by MenG on 2022/5/20 17:57
 */

@Controller
@RequestMapping
public class HomeController implements ICommunityConstant {
    @Autowired
    private IDiscussPostService discussPostService;
    @Autowired
    private IUserService userService;

    @Autowired
    private ILikeService likeService;


    /**
     * 返回首页，支持分页
     * @param model
     * @return
     */
    @GetMapping(value = {"/index/{orderMode}","/index"})
    public String getIndexPage(Model model, Page page,@PathVariable(value = "orderMode",required = false) Integer orderMode){
        //方法调用之前，SpringMVC会自动实例化 Model 和 page，并将Page注入Model
        //所以，在Thymeleaf中可以直接访问page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index/"+orderMode);
        if (orderMode==null) orderMode=0;
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit(),orderMode);
        List<Map<String,Object>> discussPosts=new ArrayList<>();
        if (list!=null){
            for(DiscussPost post:list){
                HashMap<String, Object> map = new HashMap<>();
                map.put("post",post);
                map.put("user",userService.findUserById(post.getUserId()));

                //查询帖子的点赞数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount",likeCount);

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode",orderMode);
        return "/index";
    }

    @GetMapping("/error")
    public String getErrorPage(){
        return "/error/500";
    }

    @GetMapping("/denied")
    public String getDeniedPage(){
        return "/error/404";
    }


}
