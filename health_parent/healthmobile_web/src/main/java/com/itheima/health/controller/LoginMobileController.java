package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Member;
import com.itheima.health.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * @ClassName SetmealMobileController
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2019/10/29 15:46
 * @Version V1.0
 */
@RestController
@RequestMapping(value = "/login")
public class LoginMobileController {

    @Autowired
    JedisPool jedisPool;

    @Reference
    MemberService memberService;

    // 提交预约的保存
    @RequestMapping(value = "/check")
    public Result sumbit(@RequestBody Map map, HttpServletResponse response){
        //一：手机验证码比对
        // 1：获取页面传递的手机号和验证码
        String telephone = (String)map.get("telephone");
        String validateCode = (String)map.get("validateCode");
        // 2：从Redis中，通过手机号获取redis中存储的验证码
        String redisValidateCode = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);
        // 3：页面输入的验证码和redis中的验证码进行比对，如果比对不成功，提示：“验证码输入有误”
        if(redisValidateCode==null || !redisValidateCode.equals(validateCode)){
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        // 二：如果校验通过
        // 判断是否是会员，如果不是会员，注册会员，如果是就登录成功
        Member member = memberService.findMemberByTelephone(telephone);
        if(member == null){
            // 注册会员
            member = new Member();
            member.setPhoneNumber(telephone);
            member.setRegTime(new Date());
            memberService.addMember(member);
        }
        // 三：移动端单点登录（分布式系统的权限控制），使用Cookie存放用户信息，用于对服务做权限
        Cookie cookie = new Cookie("login_member_telephone",telephone);
        cookie.setPath("/");  // 有效路径
        cookie.setMaxAge(30*24*60*60);   // 有效时间
        response.addCookie(cookie);
        return new Result(true, MessageConstant.LOGIN_SUCCESS);
    }

}
