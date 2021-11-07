package com.zzl.service;

import com.zzl.pojo.Admin;
import com.zzl.pojo.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private AdminService adminService;

    public boolean auth(HttpServletRequest request, Authentication authentication){
        //权限认证
        //请求路径
        String requestURI = request.getRequestURI();
        Object principal = authentication.getPrincipal();
        if (principal == null || "anonymousUser".equals(principal)) {
            //未登录
            return false;
        }
        UserDetails userDetails = (UserDetails) principal;
        String username = userDetails.getUsername();
        Admin admin = adminService.findAdminByUsername(username);
        if (admin == null) {
            return false;
        }
        if (1 == admin.getId()){
            //超级管理员
            return true;
        }
        Long id = admin.getId();
        List<Permission> permissionsList = this.adminService.findPermissionByAdminId(id);
        //有可能有?传参取0位
        requestURI = StringUtils.split(requestURI, "1234567890")[0];
        for (Permission permission : permissionsList) {
            //如果响应Uri与权限信息path相同,放行
            if (requestURI.equals(permission.getPath())){
                return true;
            }
        }
        return false;
    }

}
