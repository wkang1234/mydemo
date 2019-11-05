package com.itheima.health.controller;

import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

/**
 * @ClassName SetmealMobileController
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2019/10/29 15:46
 * @Version V1.0
 */
@RestController
@RequestMapping(value = "/validateCode")
public class ValidateCodeMobileController {

    @Autowired
    JedisPool jedisPool;

    // 在体检预约中完成发送验证码
    @RequestMapping(value = "/send4Order")
    public Result send4Order(String telephone){
        // 1：获取手机号
        // 2：随机生成4位验证码
        Integer code4 = ValidateCodeUtils.generateValidateCode(4);
        // 3：使用SMSUtils发送短信，手机接收到4位验证码（1234）
        try {
            // SMSUtils.sendShortMessage(telephone,code4.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        System.out.println("发送的验证码是："+code4);
        // 4：将生成的4位验证码（value），存放到Redis数据库中，和手机号绑定（key）
        jedisPool.getResource().setex(telephone+ RedisMessageConstant.SENDTYPE_ORDER,5*60,code4.toString());
        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }

    // 在手机快速登录中完成发送验证码
    @RequestMapping(value = "/send4Login")
    public Result send4Login(String telephone){
        // 1：获取手机号
        // 2：随机生成4位验证码
        Integer code4 = ValidateCodeUtils.generateValidateCode(4);
        // 3：使用SMSUtils发送短信，手机接收到4位验证码（1234）
        try {
            // SMSUtils.sendShortMessage(telephone,code4.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        System.out.println("发送的验证码是："+code4);
        // 4：将生成的4位验证码（value），存放到Redis数据库中，和手机号绑定（key）
        jedisPool.getResource().setex(telephone+ RedisMessageConstant.SENDTYPE_LOGIN,5*60,code4.toString());
        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }
}
