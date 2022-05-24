package com.meng.community;

import com.meng.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Description: community
 * Created by MenG on 2022/5/23 19:58
 */

@SpringBootTest
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitive(){
        System.out.println(sensitiveFilter.filter("吸毒，嫖娼的撒发发"));
    }
}
