package com.meng.community.service.impl;

import com.meng.community.service.IDateService;
import com.meng.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @authoer: MenG364
 * @createDate:2022/7/5
 * @description:
 */

@Service
public class DateService implements IDateService {
    @Autowired
    private RedisTemplate redisTemplate;

    private final SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");

    //将置顶的IP计入UV
    @Override
    public void recordUV(String ip){
        String redisKey = RedisKeyUtil.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey,ip);
    }

    //统计指定日期范围内的UV
    @Override
    public Long calculateUV(Date start, Date end){
        if (start==null||end==null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        //整理该日期范围的key
        List<String> keyList=new ArrayList<>();
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getUVKey(df.format(calendar.getTime()));
            keyList.add(key);
            calendar.add(Calendar.DATE,1);
        }

        //合并数据
        String redisKey = RedisKeyUtil.getUVKey(df.format(start), df.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray());

        //返回结果
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    //将指定用户计入DAU
    @Override
    public void recordDAU(int userId){
        String redisKey = RedisKeyUtil.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey,userId,true);
    }

    //统计指定日期范围内的DAU
    @Override
    public Long calculateDAU(Date start,Date end){
        if (start==null||end==null){
            throw new IllegalArgumentException("参数不能为空！");
        }

        //整理该日期范围的key
        List<byte[]> keyList=new ArrayList<>();
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getDAUKey(df.format(calendar.getTime()));
            keyList.add(key.getBytes());
            calendar.add(Calendar.DATE,1);
        }

        // 进行or运算
        return (Long) redisTemplate.execute(new RedisCallback() {

            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getUVKey(df.format(start), df.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,redisKey.getBytes(),keyList.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
            }
        });


    }
}
