package com.meng.community.service.impl;

import com.meng.community.dao.LoginTicketMapper;
import com.meng.community.service.LoginTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
* @author lrg
* @description 针对表【login_ticket】的数据库操作Service实现
* @createDate 2022-05-21 20:26:32
*/
@Service
public class LoginTicketServiceImpl implements LoginTicketService {

    @Autowired
    private LoginTicketMapper loginTicketMapper;


}




