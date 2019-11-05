package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.service.CheckItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping(value = "/checkitem")
public class CheckItemController {

    @Reference
    CheckItemService checkItemService;

    // 新增检查项
    @RequestMapping(value = "/add")
    @PreAuthorize(value = "hasAuthority('CHECKITEM_ADD')")
    public Result add(@RequestBody CheckItem checkItem){
        try {
            checkItemService.add(checkItem);
            return new Result(true, MessageConstant.ADD_CHECKITEM_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ADD_CHECKITEM_FAIL);
        }
    }

    // 分页查询（根据查询条件：传递查询条件，按照条件查询；没有查询条件，查询所有）
    @RequestMapping(value = "/findPage")
    @PreAuthorize(value = "hasAuthority('CHECKITEM_QUERY')")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = checkItemService.pageQuery(queryPageBean.getCurrentPage(),
                queryPageBean.getPageSize(),
                queryPageBean.getQueryString());
        return pageResult;
    }

    // 删除检查项
    @RequestMapping(value = "/delete")
    @PreAuthorize(value = "hasAuthority('CHECKITEM_DELETE')") // 没有权限 403
    public Result delete(Integer id){
        try {
            checkItemService.deleteById(id);
            return new Result(true, MessageConstant.DELETE_CHECKITEM_SUCCESS);
        }catch(RuntimeException e){
            return new Result(false, e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.DELETE_CHECKITEM_FAIL);
        }
    }

    // 使用ID查询检查项
    @RequestMapping(value = "/findById")
    public Result findById(Integer id){
        CheckItem checkItem = checkItemService.findById(id);
        if(checkItem!=null){
            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItem);
        }else{
            return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
        }
    }

    // 编辑检查项
    @RequestMapping(value = "/edit")
    @PreAuthorize(value = "hasAuthority('CHECKITEM_EDIT')")
    public Result edit(@RequestBody CheckItem checkItem){
        try {
            checkItemService.edit(checkItem);
            return new Result(true, MessageConstant.EDIT_CHECKITEM_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.EDIT_CHECKITEM_FAIL);
        }
    }

    // 查询所有检查项
    @RequestMapping(value = "/findCheckItemList")
    public Result findCheckItemList(){
        List<CheckItem> list = checkItemService.findAll();
        if(list!=null && list.size()>0){
            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,list);
        }else{
            return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
        }
    }
}
