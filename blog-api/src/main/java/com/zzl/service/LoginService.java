package com.zzl.service;

import com.zzl.pojo.SysUser;
import com.zzl.vo.Result;
import com.zzl.vo.params.LoginParam;
import com.zzl.vo.params.PageParams;

public interface LoginService {

    /**
     * 登录功能
     * @param loginParam
     * @return
     */
    Result login(LoginParam loginParam);

    SysUser checkToken(String token);

    /**
     * 退出登录
     * @param token
     * @return
     */
    Result logout(String token);

    /**
     * 注册
     * @param loginParam
     * @return
     */
    Result register(LoginParam loginParam);
}
