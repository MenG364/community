package com.meng.community.controller;

import com.google.code.kaptcha.Producer;
import com.meng.community.entity.User;
import com.meng.community.service.IUserService;
import com.meng.community.util.ICommunityConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

/**
 * Description: community
 * Created by MenG on 2022/5/21 11:53
 */

@Slf4j(topic = "LoginController.class")
@Controller
public class LoginController implements ICommunityConstant {

    @Autowired
    private IUserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

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

    /**
     *
     */
    @GetMapping("/kaptcha")
    public void getkaptcha(HttpServletResponse response, HttpSession session){
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码存入session
        session.setAttribute("kaptcha",text);

        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("响应验证码失败"+e.getMessage());
        }

    }

    @PostMapping("/login")
    public String login(String username,String password,String code,boolean rememberMe,Model model,HttpSession session
                        ,HttpServletResponse response){
        //验证验证码
        String kaptcha= (String) session.getAttribute("kaptcha");
        if (StringUtils.isBlank(kaptcha)||StringUtils.isBlank(code)||!kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";
        }
        //检查账号，密码
        int expiredSecond=rememberMe?REMEMBER_EXPIRED_SECOND:DEFAULT_EXPIRED_SECOND;
        Map<String, Object> map = userService.login(username, password, expiredSecond);

        if (map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSecond);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "site/login";
        }


    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";

    }

}
