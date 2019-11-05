package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderSettingService;
import com.itheima.health.utils.POIUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CheckItemController
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2019/10/23 15:58
 * @Version V1.0
 */
@RestController
@RequestMapping(value = "/ordersetting")
public class OrderSettingController {

    @Reference
    OrderSettingService orderSettingService;

    // 批量导入（批量添加），从excel中读取数据，批量导入到数据库
    @RequestMapping(value = "/upload")
    public Result upload(@RequestParam(value = "excelFile")MultipartFile excelFile){
        try {
            // 1：从excel中读取数据，组织成List<String[]>
            List<String[]> excelList = POIUtils.readExcel(excelFile);
            // 2：组织保存的数据List<String[]> --> List<OrderSetting>
            List<OrderSetting> list = new ArrayList<>();
            if(excelList!=null && excelList.size()>0){
                for (String[] strings : excelList) {
                    OrderSetting orderSetting = new OrderSetting(new Date(strings[0]),Integer.parseInt(strings[1]));
                    list.add(orderSetting);
                }
            }
            // 3：批量保存
            orderSettingService.addList(list);
            return new Result(true, MessageConstant.IMPORT_ORDERSETTING_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.IMPORT_ORDERSETTING_FAIL);
        }
    }


    // 使用当前月，查询当前月所对应的OrderSetting中的数据
    @RequestMapping(value = "/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(String date){
        try {
            List<Map> list = orderSettingService.getOrderSettingByMonth(date); // date的格式2019-10
            return new Result(true, MessageConstant.GET_ORDERSETTING_SUCCESS,list);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_ORDERSETTING_FAIL);
        }
    }

    // 根据当前日期，更新可预约人数
    @RequestMapping(value = "/updateNumberByOrderDate")
    public Result updateNumberByOrderDate(@RequestBody OrderSetting orderSetting){
        try {
            orderSettingService.updateNumberByOrderDate(orderSetting);
            return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ORDERSETTING_FAIL);
        }
    }
}
