package com.meng.community.controller;

import com.meng.community.annotation.LoginRequired;
import com.meng.community.entity.Comment;
import com.meng.community.entity.DiscussPost;
import com.meng.community.entity.Page;
import com.meng.community.entity.User;
import com.meng.community.service.*;
import com.meng.community.service.impl.UserServiceImpl;
import com.meng.community.util.CommunityUtil;
import com.meng.community.util.HostHolder;
import com.meng.community.util.ICommunityConstant;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 与用户有关
 * Created by MenG on 2022/5/22 14:54
 */

@Slf4j(topic = "UserController.class")
@Controller
@RequestMapping("/user")
public class UserController implements ICommunityConstant {

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domainPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private IUserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private ILikeService likeService;

    @Autowired
    private IFollowService followService;

    @Autowired
    private IDiscussPostService discussPostService;

    @Autowired
    private ICommentService commentService;


    /**
     * 访问设置页面
     */
    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(){
        return "/site/setting";
    }

    /**
     * 访问某个userId的主页
     * @param userId
     * @param model
     * @return
     */
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable int userId,Model model){
        User user = userService.findUserById(userId);
        if (user==null){
            throw new RuntimeException("该用户不存在");
        }

        model.addAttribute("user",user);

        //点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);

        boolean hasFollowed=false;
        // 是否已关注
        if (hostHolder.getUser()!=null){
            hasFollowed= followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);

        return "/site/profile";
    }

    @GetMapping("/profile/discuss/{userId}")
    public String getMyDiscuss(@PathVariable int userId, Model model, Page page){
        User user = userService.findUserById(userId);
        if (user==null){
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setPath("/user/profile/discuss/"+userId);
        page.setRows(discussPostService.findDiscussPostRows(user.getId()));

        List<DiscussPost> discussList = discussPostService.findDiscussPosts(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussVoList=new ArrayList<>();
        if (discussList!=null){
            for(DiscussPost discussPost:discussList){
                HashMap<String, Object> map = new HashMap<>();
                map.put("discussPost",discussPost);
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPost.getId()));
                discussVoList.add(map);
            }
        }
        model.addAttribute("discussPosts",discussVoList);

        return "/site/my-post";
    }

    @GetMapping("/profile/reply/{userId}")
    public String getMyReply(@PathVariable int userId, Model model, Page page){
        User user = userService.findUserById(userId);
        if (user==null){
            throw new RuntimeException("用户不存在");
        }

        model.addAttribute("user",user);
        page.setLimit(5);
        page.setPath("/user/profile/reply/"+userId);
        page.setRows(commentService.findUserCount(userId));

        List<Comment> commentList = commentService.findUserComment(userId,page.getOffset(),page.getLimit());
        List<Map<String,Object>> commentVoList=new ArrayList<>();
        if (commentList!=null){
            for(Comment comment:commentList){
                HashMap<String, Object> map = new HashMap<>();
                map.put("comment",comment);
                map.put("discussPost",discussPostService.findDiscussPostById(comment.getEntityId()));
                commentVoList.add(map);
            }
        }
        model.addAttribute("comments",commentVoList);

        return "/site/my-reply";
    }

    /**
     *
     */
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(@RequestBody MultipartFile headerImage, Model model){
        if (headerImage==null){
            model.addAttribute("error","您还没有选择图片");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件的格式不正确");
            return "/site/setting";
        }

        //生成随机的文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        //确定文件存放的路径
        File dest=new File(uploadPath+"/"+fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            log.error("上传图像失败"+e.getMessage());
            throw new RuntimeException("上传图像失败，服务器发生异常",e);
        }

        //更新当前用户的头像路径(web访问路径)
        //http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl=domainPath+contextPath+"/user/header/"+fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";

    }


    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放的路径
        fileName = uploadPath + "/" + fileName;
        //向浏览器输入图片
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
        //相应图片
        response.setContentType("image/"+suffix);
        try ( FileInputStream fis = new FileInputStream(fileName);
              ServletOutputStream os = response.getOutputStream()){
            byte[] buffer=new byte[1024];
            int b=0;
            while((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            log.error("读取头像失败"+e.getMessage());
        }

    }

    @LoginRequired
    @PutMapping("/password")
    public String updatePassword(String oldPassword,String newPassword,Model model){

        User user = hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(user.getId(),oldPassword, newPassword);
        if (map==null||map.isEmpty()){
            return "redirect:/logout";

        }

        model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
        model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
        return "/site/setting";

    }


}
