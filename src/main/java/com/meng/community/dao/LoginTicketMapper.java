package com.meng.community.dao;


import com.meng.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
* @author lrg
* @description 针对表【login_ticket】的数据库操作Mapper
* @createDate 2022-05-21 20:26:32
* @Entity generator.entity.LoginTicket
*/

@Mapper
@Deprecated
public interface LoginTicketMapper{

    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values (#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id, user_id, ticket, status, expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\"> ",
                "and 1=1" ,
            "</if> ",
            "</script>"
    })
    int updateStatus(String ticket, int status);
}




