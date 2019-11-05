package com.itheima.health.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.pojo.Permission;
import com.itheima.health.pojo.Role;
import com.itheima.health.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2019/11/1 17:20
 * @Version V1.0
 */
@Component
public class SpringSecurityUserService implements UserDetailsService {

    @Reference
    UserService userService;



    // 使用登录名username作为条件，查询当前登录名具有的用户，并返回用户信息封装到UserDetails的对象中
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 使用username从数据库查询User对象
        com.itheima.health.pojo.User user = userService.findUserByUsername(username);
        // 表示当前登录名查询不到用户信息，表示登录名输入有误，return null（表示登录名输入有误：抛出异常org.springframework.security.authentication.InternalAuthenticationServiceException）；
        if(user==null){
            return null;
        }

        // 获取密码（使用BCryptPasswordEncoder）
        String password = user.getPassword();
        // 封装当前用户具有的角色和权限
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        Set<Role> roles = user.getRoles();
        if(roles!=null && roles.size()>0){
            for (Role role : roles) {
                list.add(new SimpleGrantedAuthority(role.getKeyword()));// 具有ROLE_ADMIN的角色
                Set<Permission> permissions = role.getPermissions();
                if(permissions!=null && permissions.size()>0){
                    for (Permission permission : permissions) {
                        list.add(new SimpleGrantedAuthority(permission.getKeyword()));// 具有add_checkitem的权限
                    }
                }
            }
        }
        // 返回值
        /**
         * User(String username, String password, Collection<? extends GrantedAuthority> authorities)
         * 参数一：用户名
         * 参数二：密码（使用数据查询的密码{noop}admin和页面输入的密码进行比对，比对成功，可以登录；如果比对不成功，此时登录不成功，抛出异常org.springframework.security.authentication.BadCredentialsException: Bad credentials）
         * 参数三：集合，存放当前用户具有的角色和权限
         */
        UserDetails userDetails = new User(user.getUsername(),password,list);
        return userDetails;
    }
}
