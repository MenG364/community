package com.meng.community.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;

/**
 * Description: community
 * Created by MenG on 2022/5/23 10:32
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean registrationHiddenHttpMethodFilter(){
        FilterRegistrationBean<HiddenHttpMethodFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new HiddenHttpMethodFilter());
        bean.addUrlPatterns("/*");
        return bean;
    }
}
