package com.zzl.handler;

import com.alibaba.fastjson.JSON;
import com.zzl.pojo.SysUser;
import com.zzl.service.LoginService;
import com.zzl.utils.UserThreadLocal;
import com.zzl.vo.ErrorCode;
import com.zzl.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 在执行controller方法之前执行
        /**
         * 1.需要判断请求的接口路径是否为 HandlerMethod (controller方法)
         * 2.判断token是否为空，如果为空未登录
         * 3.如果token不为空，登录验证loginService checkToken
         * 4.如果认证成功放行即可
         */
        if (!(handler instanceof HandlerMethod)){
            // handler可能是RequestResourceHandler springboot程序一访问静态资源―默认去classpath下i的static目录去查询
            return true;
        }

        String token = request.getHeader("Authorization");
        log.info("==========================request start========================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}",requestURI);
        log.info("request method:{}", request.getMethod());
        log.info("token:{}", token);
        log.info("==========================request end========================");
        if (StringUtils.isBlank(token)){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), ErrorCode.NO_LOGIN.getMsg());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }

        SysUser sysUser = loginService.checkToken(token);
        if (sysUser == null){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), ErrorCode.NO_LOGIN.getMsg());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        //登陆验证成功，放行
        // 在controller中直接获取用户信息
        UserThreadLocal.put(sysUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 如果不删除 ThreadLocal中用完的信息会有内存泄露的风险
        UserThreadLocal.remove();
    }
}
