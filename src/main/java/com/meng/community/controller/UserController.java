package com.meng.community.controller;

import com.meng.community.annotation.LoginRequired;
import com.meng.community.entity.User;
import com.meng.community.service.IUserService;
import com.meng.community.service.impl.UserServiceImpl;
import com.meng.community.util.CommunityUtil;
import com.meng.community.util.HostHolder;
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
import java.util.Map;

/**
 * Description: 与用户有关
 * Created by MenG on 2022/5/22 14:54
 */

@Slf4j(topic = "UserController.class")
@Controller
@RequestMapping("/user")
public class UserController {

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


    /**
     * 访问设置页面
     */
    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(){
        return "/site/setting";
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
    public String updatePassword(String oldPassword,String newPassword,String confirmPassword,Model model){
        // 验证空值
        if (StringUtils.isBlank(newPassword)){
            model.addAttribute("newPasswordMsg","新密码不能为空");
            return "/site/setting";
        }

        // 验证新密码和确认密码是否相等
        if (!newPassword.equals(confirmPassword)){
            model.addAttribute("confirmPassword","密码不一致，请重新输入");
            return "/site/setting";
        }

        //
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(user.getId(),oldPassword, newPassword);
        if (map!=null){
            model.addAttribute("oldPasswordMsg",map.get("oldPasswordMsg"));
            return "/site/setting";
        }
        return "redirect:/logout";
    }
}
