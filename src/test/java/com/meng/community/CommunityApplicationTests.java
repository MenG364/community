package com.meng.community;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MapperScan("com.meng.community")
class CommunityApplicationTests {

    @Test
    void contextLoads() {
    }

}
