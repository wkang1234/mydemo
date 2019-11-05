package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.dao.OrderDao;
import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Member;
import com.itheima.health.pojo.Order;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderService;
import com.itheima.health.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {

    // 体检预约的订单
    @Autowired
    OrderDao orderDao;

    // 预约设置
    @Autowired
    OrderSettingDao orderSettingDao;

    // 会员
    @Autowired
    MemberDao memberDao;

    @Override
    public Result submitOrder(Map map) throws RuntimeException {
        try {
            String orderDate = (String)map.get("orderDate");
            // 预约时间
            Date date = DateUtils.parseString2Date(orderDate);
            String telephone = (String) map.get("telephone");
            // 1：使用体检日期，查询预约设置表，判断当前时间在预约设置表中是否存在数据，返回OrderSetting，如果没有数据，提示：“当前时间不可以进行预约”
            OrderSetting orderSetting = orderSettingDao.findOrderSettingByOrderDate(date);
            if(orderSetting==null){
                return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
            }
            // 2：从OrderSetting中获取number（预约最大人数），获取reservations（已经预约的人数），如果reservations>=number，提示：“预约已满”
            int number = orderSetting.getNumber();
            int reservations = orderSetting.getReservations();
            if(reservations>=number){
                return new Result(false, MessageConstant.ORDER_FULL);
            }
            // 3：获取手机号，使用手机号作为查询条件，查询会员表，判断当前预约人是否是会员
            Member member = memberDao.findMemberByTelephone(telephone);
            //如果是会员，使用会员id+预约时间+套餐id作为查询条件，查询体检预约订单表，判断当前时间是否重复预约，如果在预约订单表中存在数据，提示：“不能重复预约”
            if(member!=null){
                // 封装查询条件
                Order orderParams = new Order(member.getId(),date,null,null, Integer.parseInt((String) map.get("setmealId")));
                // 使用查询条件查询对应条件的数据（通用的方法）
                List<Order> list = orderDao.findOrderListByCondition(orderParams);
                // 判断是否重复预约
                if(list!=null && list.size()>0){
                    return new Result(false, MessageConstant.HAS_ORDERED);
                }
            }
            //如果不是会员，注册会员
            else{
                member = new Member();
                member.setName((String) map.get("name")); // 姓名
                member.setSex((String) map.get("sex")); // 性别
                member.setIdCard((String) map.get("idCard")); // 身份证号
                member.setPhoneNumber(telephone); // 手机号
                member.setRegTime(new Date()); // 注册时间
                memberDao.add(member);
            }
            // 4：向预约订单表中添加数据，表示预约完成
            Order order = new Order(member.getId(),date,(String)map.get("orderType"),Order.ORDERSTATUS_NO,Integer.parseInt((String) map.get("setmealId")));
            orderDao.add(order);
            // 5：根据预约时间，更新预约设置表，使得reservations字段+1
            orderSettingDao.updateReservationsAdd1ByOrderDate(date);
            return new Result(true, MessageConstant.ORDER_SUCCESS,order);
        } catch (Exception e) {
            e.printStackTrace();
            //return new Result(false, MessageConstant.ORDER_FAIL);
            throw new RuntimeException("提交预约出错");// spring的声明式事务处理，捕获运行时异常
        }

    }

    @Override
    public Map findById(Integer id) {
        Map map = orderDao.findById(id);
        if(map!=null){
            Date date = (Date)map.get("orderDate");
            // 转成年月日的形式
            try {
                String sDate = DateUtils.parseDate2String(date);
                // 封装到Map中
                map.put("orderDate",sDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
