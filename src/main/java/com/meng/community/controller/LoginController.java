package com.meng.community.controller;

import com.meng.community.entity.User;
import com.meng.community.service.IUserService;
import com.meng.community.util.ICommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * Description: community
 * Created by MenG on 2022/5/21 11:53
 */

@Controller
public class LoginController implements ICommunityConstant {

    @Autowired
    private IUserService userService;

    @GetMapping("/register")
    public String getRegisterPage(){
        return "/site/register";
    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "/site/login";
    }

    @PostMapping("/register")
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        if (map==null||map.isEmpty()){
            model.addAttribute("msg","注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }
        model.addAttribute("usernameMessage",map.get("usernameMessage"));
        model.addAttribute("passwordMessage",map.get("passwordMessage"));
        model.addAttribute("emailNameMessage",map.get("emailNameMessage"));
        return "/site/register";
    }

    /**
     *  激活账号业务
     * @param model
     * @param userId 用户名
     * @param code 激活码
     */
    //http://localhost:8080/community/activation/{101}/{code}
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable int userId, @PathVariable String code){
        int activation = userService.activation(userId, code);
        if (activation==ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，您的账号已经可以正常使用了");
            model.addAttribute("target","/login");
        }else if (activation==ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作，该账户已经激活过了");
            model.addAttribute("target","/index");
        }else{
            model.addAttribute("msg","激活失败，您提供的激活码不正确");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }
}
