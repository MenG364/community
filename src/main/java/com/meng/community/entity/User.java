package com.meng.community.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: community
 * Created by MenG on 2022/5/20 17:00
 */
@NoArgsConstructor
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private String password;
    private String salt;
    private String email;
    /**
     * '0-普通用户; 1-超级管理员; 2-版主;',
     */
    private int type;
    /**
     * '0-未激活; 1-已激活;',
     */
    private int status;
    private String activationCode;
    private String headerUrl;
    private Date createTime;
}
