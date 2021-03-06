package com.nice.web.controller;

import com.nice.common.Constants;
import com.nice.common.ServletUtils;
import com.nice.model.*;
import com.nice.service.IMenuService;
import com.nice.service.IRoleService;
import com.nice.service.IUserService;
import com.nice.service.impl.CustomTokenService;
import com.sun.corba.se.impl.ior.OldJIDLObjectKeyTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@RestController
public class UserController {
    @Autowired
    private IMenuService menuService;
    @Autowired
    private IRoleService roleService;

    @Autowired
    private IUserService iuserService;

    @Autowired
    CustomTokenService customTokenService;
    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("/getrouters")
    public AjaxResult getRouters()
    {
        Long uid = ((LoginUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getUserId();
        //Long uid = ((SysUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(uid);
        return AjaxResult.success(menuService.buildMenus(menus));
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getinfo")
    public AjaxResult getInfo()
    {
        LoginUser loginUser = customTokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        System.out.print("------user-----------"+ user);
        // 角色集合
        Set<String> roles = roleService.getRolesByUid(user.getUserId());
        System.out.print("------roles-----------"+ roles);
        // 权限集合
        Set<String> menus = menuService.getMenusByUid(user.getUserId());
        HashMap<String,Object> hashMap = new HashMap<String,Object>();
        hashMap.put("user", user);
        hashMap.put("roles", roles);
        hashMap.put("menus", menus);
        AjaxResult ajax = AjaxResult.success(hashMap);
        return ajax;
    }

    @PostMapping("/dosigin")
    public AjaxResult doSigin(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = iuserService.doSigin(loginBody.getUsername(), loginBody.getPassword());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    @RequestMapping("/gethellomodule")
    public String getHelloModule()
    {
        return "---------------gethellomodule--------------------";
    }
}
