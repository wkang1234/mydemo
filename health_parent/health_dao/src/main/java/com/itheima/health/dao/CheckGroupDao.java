package com.itheima.health.dao;

import com.github.pagehelper.Page;
import com.itheima.health.pojo.CheckGroup;

import java.util.List;
import java.util.Map;

public interface CheckGroupDao {

    void add(CheckGroup checkGroup);

    void addCheckGroupAndCheckItem(Map<String, Object> map);

    Page<CheckGroup> findPage(String queryString);

    CheckGroup findById(Integer id);

    List<Integer> findCheckitemIdsByCheckGroupId(Integer id);

    void edit(CheckGroup checkGroup);

    void deleteCheckGroupAndCheckItemByCheckGroupId(Integer id);

    List<CheckGroup> findAll();

    //void addCheckGroupAndCheckItem(@Param(value = "checkGroup_Id") Integer checkGroupId, @Param(value = "checkItem_Id") Integer checkItemId);

    List<CheckGroup> findCheckGroupListBySetmealId(Integer id);
}
