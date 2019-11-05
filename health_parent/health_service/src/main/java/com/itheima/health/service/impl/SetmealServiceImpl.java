package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.constant.RedisConstant;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.dao.CheckItemDao;
import com.itheima.health.dao.SetmealDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CheckItemServiceImpl
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2019/10/23 15:57
 * @Version V1.0
 */
@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    SetmealDao setmealDao;

    @Autowired
    JedisPool jedisPool;

    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        // 1：新增套餐，向套餐表中添加1条数据
        setmealDao.add(setmeal);
        // 2：新增套餐和检查组的中间表，想套餐和检查组的中间表中插入多条数据
        if(checkgroupIds!=null && checkgroupIds.length>0){
            setSetmealAndCheckGroup(setmeal.getId(),checkgroupIds);
        }
        // 3：向Redis中集合的key值为setmealPicDbResources下保存数据，数据为图片的名称
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());
    }

    @Override
    public PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString) {
        // 初始化分页参数
        PageHelper.startPage(currentPage,pageSize);
        // 查询
        Page<Setmeal> page = setmealDao.findPage(queryString);
        // 组织响应数据
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Setmeal> findAll() {
        return setmealDao.findAll();
    }


    // 使用<resultMap>和<collectioin>
//    @Override
//    public Setmeal findById(Integer id) {
//        Setmeal setmeal = setmealDao.findById(id);
//        return setmeal;
//    }

    // 不使用<resultMap>和<collectioin>，使用业务代码
    @Autowired
    CheckGroupDao checkGroupDao;

    @Autowired
    CheckItemDao checkItemDao;

    @Override
    public Setmeal findById(Integer id) {
        Setmeal setmeal = setmealDao.findById(id);
        // 业务代码完成封装
        List<CheckGroup> checkgroups = checkGroupDao.findCheckGroupListBySetmealId(id);
        if(checkgroups!=null && checkgroups.size()>0){
            for (CheckGroup checkgroup : checkgroups) {
                List<CheckItem> checkitems = checkItemDao.findCheckItemListByCheckGroupId(checkgroup.getId());
                checkgroup.setCheckItems(checkitems);
            }
        }
        setmeal.setCheckGroups(checkgroups);
        return setmeal;
    }

    @Override
    public List<Map> findSetmealOrderCount() {
        return setmealDao.findSetmealOrderCount();
    }

    private void setSetmealAndCheckGroup(Integer setmealId, Integer[] checkGroupIds) {
        for (Integer checkGroupId : checkGroupIds) {
            Map<String,Integer> map = new HashMap<>();
            map.put("setmealId",setmealId);
            map.put("checkGroupId",checkGroupId);
            setmealDao.addSetmealAndCheckGroup(map);
        }
    }
}
