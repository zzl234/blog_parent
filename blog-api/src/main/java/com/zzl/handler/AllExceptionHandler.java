package com.zzl.handler;

import com.zzl.vo.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice //对加了@Controller注解的方法进行拦截 AOP的实现
public class AllExceptionHandler {

    @ExceptionHandler(Exception.class) //进行异常处理，处理Exception.class的异常
    public Result doException(Exception ex){
        ex.printStackTrace();
        return Result.fail(-999, "系统异常");

    }

}
