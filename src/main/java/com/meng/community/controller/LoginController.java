package com.meng.community.controller;

import com.google.code.kaptcha.Producer;
import com.meng.community.entity.User;
import com.meng.community.service.IUserService;
import com.meng.community.util.CommunityUtil;
import com.meng.community.util.ICommunityConstant;
import com.meng.community.util.MailClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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


    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private MailClient mailClient;

    @GetMapping("/register")
    public String getRegisterPage(){
        return "/site/register";
    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "/site/login";
    }

    @GetMapping("/forget")
    public String getForgetPage(){
        return "/site/forget";
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

    /**
     * 如果参数是一个实体，那么springboot会将实体放入model中，但如果是普通参数，那么不会放在model中，怎么在前端访问到普通参数呢
     * 解决：1、人为放入model中
     *      2、传入的普通参数是放入request中的，返回前端后，本次请求还未结束，我么你可以在request取值${param.username}
     * @param username
     * @param password
     * @param code
     * @param rememberMe
     * @param model
     * @param session
     * @param response
     * @return
     */
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
            return "/site/login";
        }


    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";

    }

    @GetMapping("/forget/code")
    @ResponseBody
    public String getCode(String email,HttpSession session){
        if (StringUtils.isBlank(email)){
            return CommunityUtil.getJSONString(1, "邮箱不能为空！");
        }

        Context context=new Context();

        context.setVariable("email",email);
        String code = CommunityUtil.generateUUID().substring(0, 4);
        context.setVariable("code",code);
        String content=templateEngine.process("/mail/forget",context);
        mailClient.sendMail(email,"找回密码",content);

        session.setAttribute("code",code);

        return CommunityUtil.getJSONString(0);
    }


    @PutMapping("/forget/password")
    public String resetPassword(Model model,String email,String verifyCode,String password,HttpSession session){
        String code = (String) session.getAttribute("code");
        //空值判断
        if (StringUtils.isBlank(verifyCode)||StringUtils.isBlank(code)||!verifyCode.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码错误");
        }

        Map<String, Object> map = userService.resetPassword(email, password);
        if (map.containsKey("user")){
            return "redirect:/login";
        }

        model.addAttribute("emailMsg",map.get("emailMsg"));
        model.addAttribute("passwordMsg",map.get("passwordMsg"));

        return "/site/forget";

    }


}
