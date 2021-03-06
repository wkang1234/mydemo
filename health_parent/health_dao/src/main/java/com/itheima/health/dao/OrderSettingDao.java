package com.itheima.health.dao;

import com.itheima.health.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderSettingDao {

    void add(OrderSetting orderSetting);

    long findCountByOrderDate(Date orderDate);

    void update(OrderSetting orderSetting);

    List<OrderSetting> getOrderSettingByMonthBetween(Map<String, String> paramsMap);

    OrderSetting findOrderSettingByOrderDate(Date date);

    void updateReservationsAdd1ByOrderDate(Date date);
}
