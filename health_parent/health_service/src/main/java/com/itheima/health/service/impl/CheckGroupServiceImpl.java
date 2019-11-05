package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {

    @Autowired
    CheckGroupDao checkGroupDao;

    @Override
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        // 1：组织检查组的表单数据，保存检查组
        checkGroupDao.add(checkGroup);
        // 2：遍历页面传递过来的checkitemIds数组，向检查组和检查项的中间表中插入数据
        setCheckGroupAndCheckItem(checkGroup.getId(),checkitemIds);
    }

    @Override
    public PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString) {
        // PageHelper插件
        // 1：初始化
        PageHelper.startPage(currentPage,pageSize);
        // 2：查询
        Page<CheckGroup> page = checkGroupDao.findPage(queryString);
        // 3：封装结果
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }

    @Override
    public List<Integer> findCheckitemIds(Integer id) {
        return checkGroupDao.findCheckitemIdsByCheckGroupId(id);
    }

    @Override
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        // 1：组织检查组的表单数据，使用id，更新检查组
        checkGroupDao.edit(checkGroup);
        // 2：使用检查组的id，删除检查组和检查项中间表的数据
        checkGroupDao.deleteCheckGroupAndCheckItemByCheckGroupId(checkGroup.getId());
        // 3：遍历页面传递过来的checkitemIds数组，向检查组和检查项的中间表中插入数据
        this.setCheckGroupAndCheckItem(checkGroup.getId(),checkitemIds);
    }

    @Override
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }

    // 遍历页面传递过来的checkitemIds数组，向检查组和检查项的中间表中插入数据
    private void setCheckGroupAndCheckItem(Integer checkGroupId, Integer[] checkitemIds) {
        if(checkitemIds!=null && checkitemIds.length>0){
            for (Integer checkItemId : checkitemIds) {
                // 方案一：在CheckGroupDao中@Param
                // checkGroupDao.addCheckGroupAndCheckItem(checkGroupId,checkItemId);
                // 方案二：使用Map集合
                Map<String,Object> map = new HashMap<>();
                map.put("checkGroup_Id",checkGroupId);
                map.put("checkItem_Id",checkItemId);
                checkGroupDao.addCheckGroupAndCheckItem(map);
            }
        }
    }
}
