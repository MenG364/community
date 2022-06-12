package com.meng.community.service.impl;

import com.meng.community.dao.LoginTicketMapper;
import com.meng.community.dao.UserMapper;
import com.meng.community.entity.LoginTicket;
import com.meng.community.entity.User;
import com.meng.community.service.IUserService;
import com.meng.community.util.CommunityUtil;
import com.meng.community.util.ICommunityConstant;
import com.meng.community.util.MailClient;
import com.meng.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Description: community
 * Created by MenG on 2022/5/20 17:54
 */

@Service
public class UserServiceImpl implements IUserService, ICommunityConstant {

    @Autowired
    private UserMapper userMapper;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private RedisTemplate redisTemplate;

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
//        return userMapper.selectById(id);
        User user = getCache(id);
        if (user==null){
            user = initCache(id);
        }
        return user;
    }

    @Override
    public User findUserByEmail(String email){
        return userMapper.selectByEmail(email);
    }

    @Override
    public User findUserByName(String username){
        return userMapper.selectByName(username);
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
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if (user.getStatus()==1){
            return ACTIVATION_FAILURE;
        }else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    @Override
    public Map<String,Object> login(String username, String password, int expiredSeconds){
        Map<String, Object> map = new HashMap<>();
        //空值处理
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        //验证账号
        User user = userMapper.selectByName(username);
        if (user==null){
            map.put("usernameMsg","该账户不存在");
            return map;
        }
        //验证状态
        if (user.getStatus()==0){
            map.put("usernameMsg","该账户未激活");
            return map;
        }
        //验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds* 1000L));
//        loginTicketMapper.insertLoginTicket(loginTicket);

        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey,loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    @Override
    public void logout(String ticket){
//        loginTicketMapper.updateStatus(ticket,1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicker = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicker.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicker);
    }

    @Override
    public LoginTicket findLoginTicket(String ticket){
//        return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    @Override
    public int updateHeader(int userid, String headerUrl){
//        return userMapper.updateHeader(userid, headerUrl);
        int rows = userMapper.updateHeader(userid, headerUrl);
        clearCache(userid);
        return rows;
    }

    //重置密码
    @Override
    public Map<String,Object> resetPassword(String email, String password){
        Map<String,Object> map=new HashMap<>();
        if (StringUtils.isBlank(email)) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("PasswordMsg", "新密码不能为空!");
            return map;
        }
        User user = userMapper.selectByEmail(email);
        if (user==null){
            map.put("emailMsg", "该邮箱尚未注册!");
            return map;
        }


        password=CommunityUtil.md5(password + user.getSalt());
        userMapper.updatePassword(user.getId(),password);
        clearCache(user.getId());
        map.put("user", user);
        return map;
    }
    //

    @Override
    public Map<String,Object> updatePassword(int userid,String oldPassword, String newPassword){
        User user = userMapper.selectById(userid);
        HashMap<String, Object> map = new HashMap<>();
        // 空值处理
        if (StringUtils.isBlank(oldPassword)) {
            map.put("oldPasswordMsg", "原密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(newPassword)) {
            map.put("newPasswordMsg", "新密码不能为空!");
            return map;
        }

        oldPassword=CommunityUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPassword)){
            map.put("oldPasswordMsg","原密码输入有误");
            return map;
        }
        newPassword=CommunityUtil.md5(newPassword + user.getSalt());
        userMapper.updatePassword(userid,newPassword);
        clearCache(userid);
        return map;
    }

    // 1.优先从缓存中取值
    private User getCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    // 2.取不到时初始化缓存数据
    private User initCache(int userId){
        User user = userMapper.selectById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey,user,3600, TimeUnit.SECONDS);
        return user;
    }

    // 3.数据变更时清楚缓存数据
    private void clearCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }




}
