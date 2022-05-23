package com.meng.community.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: community
 * Created by MenG on 2022/5/20 16:35
 */

@NoArgsConstructor
@Data
public class DiscussPost implements Serializable {

    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;
    private static final long serialVersionUID = 1L;
}
