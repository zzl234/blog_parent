package com.zzl.config;

import com.zzl.handler.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        //addMapping就是所有的文件，allowedOrigins指的是可以那个地址可以访问
        registry.addMapping("/**").allowedOrigins("http://localhost:8080");
    }

    // 访问拦截
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/test")
        .addPathPatterns("/comments/create/change")
        .addPathPatterns("/articles/publish");
                /*.excludePathPatterns("/login")
                .excludePathPatterns("/register");*/
    }
}
