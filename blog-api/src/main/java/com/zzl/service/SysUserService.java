package com.zzl.service;

import com.zzl.pojo.SysUser;
import com.zzl.vo.Result;
import com.zzl.vo.UserVo;

public interface SysUserService {

    UserVo findUserVoById(Long id);

    SysUser findUserById(Long id);

    SysUser findUser(String account, String password);


    /**
     * 根据token查询用户信息
     * @param token
     * @return
     */
    Result findUserByToken(String token);


    /**
     * 根据用户查找账户
     * @param account
     * @return
     */
    SysUser findUserByAccount(String account);

    /**
     * 保存用户
     * @param sysUser
     */
    void save(SysUser sysUser);
}
