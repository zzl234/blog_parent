package com.zzl.common.cache;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {

    //缓存过期时间
    long expire() default 1*60*1000;
    //名称
    String name() default "";

}
