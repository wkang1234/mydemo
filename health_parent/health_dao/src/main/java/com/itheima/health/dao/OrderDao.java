package com.itheima.health.dao;

import com.itheima.health.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrderDao {


    List<Order> findOrderListByCondition(Order orderParams);

    void add(Order order);

    Map findById(Integer id);

    Integer findTodayOrderNumber(String today);

    Integer findTodayVisitsNumber(String today);

    Integer findThisOrderNumber(Map<String, String> params);

    Integer findThisVisitsNumber(@Param(value = "begin") String a, @Param(value = "end") String b);

    List<Map> findHotSetmeal();
}
