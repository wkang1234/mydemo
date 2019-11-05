package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName CheckItemController
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2019/10/23 15:58
 * @Version V1.0
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Reference
    UserService userService;

    // 获取用户信息（用户名）
    @RequestMapping(value = "/getUsername")
    public Result getUsername(){
        // 获取用户信息（从SpringSecurity）
        try {
            User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = user.getUsername();
            return new Result(true,MessageConstant.GET_USERNAME_SUCCESS,username);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_USERNAME_FAIL);
        }

    }

}
