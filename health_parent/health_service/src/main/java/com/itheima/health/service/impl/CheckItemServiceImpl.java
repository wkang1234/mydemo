package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.dao.CheckItemDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName CheckItemServiceImpl
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2019/10/23 15:57
 * @Version V1.0
 */
@Service(interfaceClass = CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    CheckItemDao checkItemDao;

    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    @Override
    public PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString) {
        // 友情回忆：不使用Mybatis分页插件PageHelper，应该怎么办？
        // 总记录数：sql：select count(*) from t_checkitem WHERE CODE = '传智身高' OR NAME = '传智身高'，返回Long total
        // 当前页的数据集合：SELECT * FROM t_checkitem WHERE CODE = '传智身高' OR NAME = '传智身高' LIMIT 0,10，返回List
        // limit：参数一：当前页从第几条开始检索，默认是0（0表示第1条），计算方法：（currentPage-1）*pageSize
        // limit：参数二：每页最多显示的记录数，计算方法：pageSize

        /**采用mybatis的分页插件，方案一*/
//        // 1：完成对分页初始化工作
//        PageHelper.startPage(currentPage,pageSize);
//        // 2：查询
//        List<CheckItem> list = checkItemDao.findPage(queryString);
//        // 3：后处理，PageHelper会根据查询的结果再封装成PageHealper对应的实体类
//        PageInfo<CheckItem> pageInfo = new PageInfo<>(list);
//        // 组织PageResult
//        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        /**采用mybatis的分页插件，方案二*/
        // 1：完成对分页初始化工作
        PageHelper.startPage(currentPage,pageSize);
        // 2：查询
        Page<CheckItem> page = checkItemDao.findPage(queryString);
        // 组织PageResult
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void deleteById(Integer id) {
        // 使用检查项id，查询检查项
        Integer count = checkItemDao.findCheckGroupAndCheckItemCountByCheckItemId(id);
        // 存在数据
        if(count>0){
            throw new RuntimeException("当前检查项和检查组之间存在关联关系，不能删除");
        }
        checkItemDao.deleteById(id);
    }

    @Override
    public CheckItem findById(Integer id) {
        return checkItemDao.findById(id);
    }

    @Override
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }

    @Override
    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }
}
