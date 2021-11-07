package com.zzl.common.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zzl.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;

@Aspect
@Component
@Slf4j
public class CacheAspect {

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    //切入点为注解Cache
    @Pointcut("@annotation(com.zzl.common.cache.Cache)")
    public void pt(){}

    // 环绕通知
    @Around("pt()")
    public Object around(ProceedingJoinPoint joinPoint){
        try{
            Signature signature = joinPoint.getSignature();
            //获得类名
            String className = joinPoint.getTarget().getClass().getSimpleName();
            //获得方法名
            String methodName = signature.getName();
            //存取方法参数类型
            Class[] parameterTypes = new Class[joinPoint.getArgs().length];
            //拿到参数
            Object[] args = joinPoint.getArgs();
            //将所有参数拼接成字符串
            String params = "";
            for(int i=0; i<args.length; i++){
                if(args[i] != null){
                    params += JSON.toJSONString(args[i]);
                    parameterTypes[i] = args[i].getClass();
                }else{
                    parameterTypes[i] = null;
                }
            }
            if(StringUtils.isNotEmpty(params)){
                //md5参数加密,用于设置redis key
                params = DigestUtils.md5Hex(params);
            }
            //通过parameterTypes拿到对应的方法
            Method method = joinPoint.getSignature().getDeclaringType().getMethod(methodName, parameterTypes);
            //获取cache注解
            Cache annotation = method.getAnnotation(Cache.class);
            //获取过期时间
            long expire = annotation.expire();
            //缓存名称
            String name = annotation.name();

            //创建redis Key，保证key的唯一性
            String redisKey = name+"::"+className+"::"+methodName+"::"+params;
            //1、先从redis中获取要查询的信息
            String redisValue = redisTemplate.opsForValue().get(redisKey);
            //如果redis中有
            if(StringUtils.isNotEmpty(redisValue)){
                log.info("走了缓存---,{},{}",className,methodName);
                return JSON.parseObject(redisValue,Result.class);
            }
            //2、redis中没有，访问查询方法，然后将结果存入redis
            //proceed()即代表执行了Controller中的方法，
            // 如果有返回值就返回，如果没有就不用返回，在这里有返回值，为文章信息
            Object proceed = joinPoint.proceed();
            //JSON.toJSONString将对象转为json字符串
            //JSON.parseObject将json字符串转为对象
            redisTemplate.opsForValue().set(redisKey,JSON.toJSONString(proceed), Duration.ofMillis(expire));
            log.info("存入缓存---{},{}",className,methodName);
            return proceed;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return Result.fail(-999,"系统错误");
    }
}