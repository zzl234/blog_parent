package com.zzl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author lum
 * @date 2021/9/7
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //密码配置 BCrypt密码加密策略
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    public static void main(String[] args) {
        //加密策略 MD5不安全 彩虹表 MD5加盐
        String lum = new BCryptPasswordEncoder().encode("lum");
        System.out.println(lum);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //开启登录验证
                .antMatchers("/user/findAll").hasRole("admin")
                //访问接口需要admin角色
                //对应放行
                .antMatchers("/css/**").permitAll()
                .antMatchers("/img/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/plugins/**").permitAll()

                .antMatchers("/admin/**").access("@authService.auth(request,authentication)")      //自定义Service来实现实时权限认证,要通过uthService认证  通过放行
                .antMatchers("/pages/**").authenticated()
                .and().formLogin()
                .loginPage("/login.html")             //自定义登录界面
                .loginProcessingUrl("/login")         //登录处理接口
                .usernameParameter("username")       //定义登录时的用户名的key 默认username
                .passwordParameter("password")      //定义登录时的密码的key 默认password
                .defaultSuccessUrl("/pages/main.html")
                .failureUrl("/login.html")
                .permitAll()                  //通过 不拦截没根据前面配的路径决定,这是指和登录表单相关的接口  都通过
                .and().logout()              //退出登录配置
                .logoutUrl("/logout")       //退出登录接口
                .logoutSuccessUrl("/login.html")
                .permitAll()                //退出登录的接口放行
                .and()
                .httpBasic()                //用http(postman)访问进行拦截
                .and()
                .csrf().disable()           //crf关闭 如果自定义登录 需要关闭
                .headers().frameOptions().sameOrigin();  //支持iframe页面嵌套
    }
}