package com.meng.community.config;


import com.meng.community.entity.LoginTicket;
import com.meng.community.entity.User;
import com.meng.community.service.IUserService;
import com.meng.community.util.CommunityUtil;
import com.meng.community.util.CookieUtil;
import com.meng.community.util.ICommunityConstant;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @authoer: MenG364
 * @createDate:2022/7/2
 * @description:
 */

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements ICommunityConstant {

    @Autowired
    private IUserService userService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权
        http.authorizeHttpRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/discuss",
                        "/comment/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                )
                .hasAnyAuthority(AUTHORITY_USER, AUTHORITY_ADMIN, AUTHORITY_MODERATOR)
                .antMatchers("/discuss/top","/discuss/wonderful")
                .hasAnyAuthority(AUTHORITY_MODERATOR)
                .antMatchers("/discuss/delete","/data/**","/actuator/")
                .hasAnyAuthority(AUTHORITY_ADMIN)
                .anyRequest().permitAll()
                .and().csrf().disable();
        //权限不够时的处理
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() { //没有登录时的处理
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equalsIgnoreCase(xRequestedWith)) {
                            response.setContentType("application/plain;charset=utf-8");
                            response.getWriter().write(CommunityUtil.getJSONString(HttpStatus.SC_FORBIDDEN, "你还没有登录！"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() { //权限不足时的处理
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equalsIgnoreCase(xRequestedWith)) {
                            response.setContentType("application/plain;charset=utf-8");
                            response.getWriter().write(CommunityUtil.getJSONString(HttpStatus.SC_FORBIDDEN, "你没有权限！"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });
        //security底层会默认拦截/logout，进行退出处理
        //覆盖它的默认逻辑，执行自己的退出逻辑
        http.logout().logoutUrl("/securitylogout");

        http.addFilterBefore(new Filter() {
                                 @Override
                                 public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                                     HttpServletRequest request = (HttpServletRequest) servletRequest;
                                     String ticket= CookieUtil.getValue(request,"ticket");
                                     if (ticket!=null){
                                         //如果登录凭证存在，查询LoginTicket
                                         LoginTicket loginTicket = userService.findLoginTicket(ticket);
                                         //检查凭证是否有效
                                         if (loginTicket!=null&&loginTicket.getStatus()==0&& loginTicket.getExpired().after(new Date())){
                                             //根据凭证查询用户
                                             User user = userService.findUserById(loginTicket.getUserId());
                                             //在本次请求持有User，采用ThreadLocal
                                             //构建用户认证结果，并存入SecurityContext，以便于Security授权
                                             UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                                     user,
                                                     user.getPassword(),
                                                     userService.getAuthorities(user.getId())
                                             );
                                             SecurityContextHolder.setContext(new SecurityContextImpl(authenticationToken));

                                         }
                                     }
                                     filterChain.doFilter(servletRequest,servletResponse);
                                 }
                             }
                , UsernamePasswordAuthenticationFilter.class);
    }
}
