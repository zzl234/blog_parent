package com.zzl.controller;

import com.zzl.pojo.SysUser;
import com.zzl.utils.UserThreadLocal;
import com.zzl.vo.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @RequestMapping
    public Result test(){
        SysUser sysUser = UserThreadLocal.get();
        System.out.println(sysUser);

        return Result.success(null);

    }

}
