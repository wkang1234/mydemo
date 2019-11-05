package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    OrderSettingDao orderSettingDao;

    @Override
    public void addList(List<OrderSetting> list) {
        if(list!=null && list.size()>0){
            for (OrderSetting orderSetting : list) {
                // 1：判断当前时间在数据库中是否存记录
                long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                // 2：如果count==0，执行保存
                if(count==0){
                    // 保存预约设置
                    orderSettingDao.add(orderSetting);
                }
                // 3：如果count>0，执行更新，使用日期更新最多可预约的数量
                else{
                    // 更新预约设置
                    orderSettingDao.update(orderSetting);
                }

            }
        }
    }

    @Override
    public List<Map> getOrderSettingByMonth(String date) {
        // 当前月的开始时间
        String begin = date+"-01";
        // 当前月的结束时间
        String end = date+"-31";
        // 使用Map集合作为参数
        Map<String,String> paramsMap = new HashMap<>();
        paramsMap.put("begin",begin);
        paramsMap.put("end",end);
        // 使用条件完成对预约设置的查询
        List<OrderSetting> list = orderSettingDao.getOrderSettingByMonthBetween(paramsMap);
        // 组织返回的数据结构
        List<Map> mapList = new ArrayList<>();
        if(list!=null && list.size()>0){
            for (OrderSetting orderSetting : list) {
                Map map = new HashMap();
                map.put("date",orderSetting.getOrderDate().getDate()); // 日
                map.put("number",orderSetting.getNumber()); // 可预约人数
                map.put("reservations",orderSetting.getReservations()); // 已经预约的人数
                mapList.add(map);
            }
        }
        return mapList;
    }

    @Override
    public void updateNumberByOrderDate(OrderSetting orderSetting) {
        // 1：判断当前时间在数据库中是否存记录
        long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        // 2：如果count==0，执行保存
        if(count==0){
            // 保存预约设置
            orderSettingDao.add(orderSetting);
        }
        // 3：如果count>0，执行更新，使用日期更新最多可预约的数量
        else{
            // 更新预约设置
            orderSettingDao.update(orderSetting);
        }
    }
}
