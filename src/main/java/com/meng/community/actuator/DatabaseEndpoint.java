package com.meng.community.actuator;

import com.meng.community.util.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @authoer: MenG364
 * @createDate:2022/7/9
 * @description:
 */

@Slf4j(topic = "DatabaseEndpoint.class")
@Component
@Endpoint(id = "database")
public class DatabaseEndpoint {

    @Autowired
    private DataSource dataSource;

    @ReadOperation
    public String checkConnection(){
        try {
            dataSource.getConnection();
            return CommunityUtil.getJSONString(0,"获取链接成功");
        } catch (SQLException e) {
            log.error("获取链接失败");
            return CommunityUtil.getJSONString(1,"获取链接失败");
        }
    }
}
