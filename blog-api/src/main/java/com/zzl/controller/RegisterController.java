package com.zzl.controller;

import com.zzl.service.LoginService;
import com.zzl.vo.Result;
import com.zzl.vo.params.LoginParam;
import com.zzl.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("register")
public class RegisterController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public Result register(@RequestBody LoginParam loginParam){

        return loginService.register(loginParam);

    }

}
