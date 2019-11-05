package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Order;
import com.itheima.health.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

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
@RequestMapping(value = "/order")
public class OrderMobileController {

    @Reference
    OrderService orderService;

    @Autowired
    JedisPool jedisPool;

    // 提交预约的保存
    @RequestMapping(value = "/submit")
    public Result sumbit(@RequestBody Map map){
        //一：手机验证码比对
        // 1：获取页面传递的手机号和验证码
        String telephone = (String)map.get("telephone");
        String validateCode = (String)map.get("validateCode");
        // 2：从Redis中，通过手机号获取redis中存储的验证码
        String redisValidateCode = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        // 3：页面输入的验证码和redis中的验证码进行比对，如果比对不成功，提示：“验证码输入有误”
        if(redisValidateCode==null || !redisValidateCode.equals(validateCode)){
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        // 二：完成体检预约
        Result result = null;
        try {
            map.put("orderType", Order.ORDERTYPE_WEIXIN);
            result = orderService.submitOrder(map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ORDER_FAIL);
        }
        return result;
    }

    // 使用订单id，查询订单信息
    @RequestMapping(value = "/findById")
    public Result findById(Integer id){
        try {
            Map map = orderService.findById(id);
            return new Result(true, MessageConstant.QUERY_ORDER_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_ORDER_FAIL);
        }
    }
}
