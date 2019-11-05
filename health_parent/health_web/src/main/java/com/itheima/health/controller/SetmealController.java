package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisConstant;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import com.itheima.health.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.util.UUID;

/**
 * @ClassName CheckItemController
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2019/10/23 15:58
 * @Version V1.0
 */
@RestController
@RequestMapping(value = "/setmeal")
public class SetmealController {

    @Reference
    SetmealService setmealService;

    @Autowired
    JedisPool jedisPool;

    // 套餐图片的上传(springmvc的文件上传）
    @RequestMapping(value = "/upload")
    public Result upload(MultipartFile imgFile){
        try {
            // 之前的文件名
            String fileName = imgFile.getOriginalFilename();
            // 使用UUID的形式（时间戳），保证文件名唯一
            fileName = UUID.randomUUID().toString()+fileName.substring(fileName.lastIndexOf("."));
            // 七牛云上上传
            QiniuUtils.upload2Qiniu(imgFile.getBytes(),fileName);
            // 同时向Redis中的集合的key=setmealPicResource，存放数据，数据存放文件名
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);
            // 响应文件名
            return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS,fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
    }

    // 新增套餐
    @RequestMapping(value = "/add")
    public Result add(@RequestBody Setmeal setmeal, @RequestParam(value = "checkgroupIds") Integer [] checkgroupIds){
        try {
            setmealService.add(setmeal,checkgroupIds);
            return new Result(true, MessageConstant.ADD_SETMEAL_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ADD_SETMEAL_FAIL);
        }
    }

    // 套餐的分页
    @RequestMapping(value = "/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = setmealService.pageQuery(queryPageBean.getCurrentPage(),
                queryPageBean.getPageSize(),
                queryPageBean.getQueryString());
        return pageResult;
    }

}
