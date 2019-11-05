package com.itheima.health.job;

import com.itheima.health.constant.RedisConstant;
import com.itheima.health.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.Iterator;
import java.util.Set;

/**
 * @ClassName ClearImgJob
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2019/10/26 18:12
 * @Version V1.0
 */
// 任务类
public class ClearImgJob {

    @Autowired
    JedisPool jedisPool;

    // 任务类执行的方法
    public void executeJob(){
        //计算setmealPicResources集合与setmealPicDbResources集合的差值，清理图片
        Set<String> set = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        Iterator<String> iterator = set.iterator();
        while(iterator.hasNext()){
            String picName = iterator.next();
            System.out.println("删除的图片名称："+picName);
            // 1：删除七牛云的数据
            QiniuUtils.deleteFileFromQiniu(picName);
            // 2:：删除key值为ssetmealPicResources 的redis的数据
            jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES,picName);

        }

    }
}
