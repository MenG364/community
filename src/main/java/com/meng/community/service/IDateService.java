package com.meng.community.service;

import java.util.Date;

/**
 * @authoer: MenG364
 * @createDate:2022/7/5
 * @description:
 */
public interface IDateService {
    //将置顶的IP计入UV
    void recordUV(String ip);

    //统计指定日期范围内的UV
    Long calculateUV(Date start, Date end);

    //将指定用户计入DAU
    void recordDAU(int userId);

    //统计指定日期范围内的DAU
    Long calculateDAU(Date start, Date end);
}
