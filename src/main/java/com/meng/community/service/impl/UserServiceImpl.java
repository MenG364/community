package com.meng.community.service.impl;

import com.meng.community.dao.UserMapper;
import com.meng.community.entity.User;
import com.meng.community.service.IUserService;
import com.meng.community.util.CommunityUtil;
import com.meng.community.util.ICommunityConstant;
import com.meng.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Description: community
 * Created by MenG on 2022/5/20 17:54
 */

@Service
public class UserServiceImpl implements IUserService, ICommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    @Override
    public Map<String,Object> register(User user){
        HashMap<String, Object> map = new HashMap<>();

        //空值处理
        if (user==null){
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMessage","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMessage","密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMessage","邮箱不能为空");
            return map;
        }

        //验证账号
        User userFromDB = userMapper.selectByName(user.getUsername());
        if (userFromDB!=null){
            map.put("usernameMessage","用户名已存在");
            return map;
        }

        //验证账号
        User emailFromDB = userMapper.selectByName(user.getEmail());
        if (emailFromDB!=null){
            map.put("emailMessage","邮箱已被注册");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活邮件
        Context context=new Context();
        context.setVariable("email",user.getEmail());
        //http://localhost:8080/community/activation/{101}/{code}
        String url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content=templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账户",content);

        return map;
    }

    /**
     * 激活账号
     * @return 传回激活转态
     */
    @Override
    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if (user.getStatus()==1){
            return ACTIVATION_FAILURE;
        }else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }
}
