package com.zzl.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzl.mapper.AdminMapper;
import com.zzl.pojo.Admin;
import com.zzl.pojo.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    public Admin findAdminByUsername(String username){
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, username);
        queryWrapper.last("limit 1");

        Admin admin = adminMapper.selectOne(queryWrapper);
        return admin;
    }

    // //按照管理员id查找权限
    public List<Permission> findPermissionByAdminId(Long id) {

        return adminMapper.findPermissionByAdminId(id);

    }
}
