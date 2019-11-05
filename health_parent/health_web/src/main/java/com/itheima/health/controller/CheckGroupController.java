package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.service.CheckGroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName CheckItemController
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2019/10/23 15:58
 * @Version V1.0
 */
@RestController
@RequestMapping(value = "/checkgroup")
public class CheckGroupController {

    @Reference
    CheckGroupService checkGroupService;

    // 新增检查组
    @RequestMapping(value = "/add")
    public Result add(@RequestBody CheckGroup checkGroup,@RequestParam(value = "checkitemIds") Integer [] checkitemIds){
        try {
            checkGroupService.add(checkGroup,checkitemIds);
            return new Result(true, MessageConstant.ADD_CHECKGROUP_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ADD_CHECKGROUP_FAIL);
        }
    }

    // 检查组的分页
    @RequestMapping(value = "/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = checkGroupService.pageQuery(queryPageBean.getCurrentPage(),
                queryPageBean.getPageSize(),
                queryPageBean.getQueryString());
        return pageResult;
    }

    // 使用id，查询检查组
    @RequestMapping(value = "/findById")
    public Result findById(Integer id){
        CheckGroup checkGroup = checkGroupService.findById(id);
        if(checkGroup!=null){
            return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkGroup);
        }else{
            return new Result(false, MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
    }

    // 使用检查组id，查询检查项的id集合，返回List<Integer>
    @RequestMapping(value = "/findCheckitemIds")
    public List<Integer> findCheckitemIds(Integer id){
        List<Integer> list = checkGroupService.findCheckitemIds(id);
        return list;
    }

    // 编辑检查组
    @RequestMapping(value = "/edit")
    public Result edit(@RequestBody CheckGroup checkGroup,@RequestParam(value = "checkitemIds") Integer [] checkitemIds){
        try {
            checkGroupService.edit(checkGroup,checkitemIds);
            return new Result(true, MessageConstant.EDIT_CHECKGROUP_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.EDIT_CHECKGROUP_FAIL);
        }
    }

    // 查询所有检查组集合
    @RequestMapping(value = "/findCheckGroupList")
    public Result findCheckGroupList(){
        List<CheckGroup> list = checkGroupService.findAll();
        if(list!=null && list.size()>0){
            return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,list);
        }else{
            return new Result(false, MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
    }
}
