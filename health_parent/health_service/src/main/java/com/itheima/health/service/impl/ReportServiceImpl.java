package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.dao.OrderDao;
import com.itheima.health.service.ReportService;
import com.itheima.health.utils.DateUtils;
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
@Service(interfaceClass = ReportService.class)
@Transactional
public class ReportServiceImpl implements ReportService {

    // 会员
    @Autowired
    MemberDao memberDao;

    // 订单
    @Autowired
    OrderDao orderDao;

    // 获取运营业务数据报表
    /**
     * -- 今天新增会员数
     SELECT COUNT(*) FROM t_member WHERE regTime = '2019-11-04'
     -- 总会员数
     SELECT COUNT(*) FROM t_member
     -- 本周新增会员数(>=本周的周一的日期)（本周的周一，11月4号）
     SELECT COUNT(*) FROM t_member WHERE regTime >= '2019-11-04'
     -- 本月新增会员数(>=本月的第一天的日期)（本月的1号，11月1号）
     SELECT COUNT(*) FROM t_member WHERE regTime >= '2019-11-01'
     -------------------------------------------------------------------------------
     -- 今日预约数
     SELECT COUNT(*) FROM t_order WHERE orderDate = '2019-11-04'
     -- 今日到诊数
     SELECT COUNT(*) FROM t_order WHERE orderDate = '2019-11-04' AND orderStatus = '已到诊'
     -- 本周预约数(>=本周的周一的日期 <=本周的周日的日期)  （本周的周一，11月4号；本周的周日，11月10号）
     SELECT COUNT(*) FROM t_order WHERE orderDate BETWEEN '2019-11-04' AND '2019-11-10'
     -- 本周到诊数
     SELECT COUNT(*) FROM t_order WHERE orderDate BETWEEN '2019-11-04' AND '2019-11-10' AND orderStatus = '已到诊'
     -- 本月预约数(>=每月的第一天的日期 <=每月的最后一天的日期)（本月的1号，11月1号，本月的最后1号，11月30号）
     SELECT COUNT(*) FROM t_order WHERE orderDate BETWEEN '2019-11-01' AND '2019-11-30'
     -- 本月到诊数
     SELECT COUNT(*) FROM t_order WHERE orderDate BETWEEN '2019-11-01' AND '2019-11-30' AND orderStatus = '已到诊'
     ----------------------------------------------------------------------------------
     -- 热门套餐
     SELECT s.name,COUNT(o.id) setmeal_count, COUNT(o.id)/(SELECT COUNT(*) FROM t_order) proportion FROM t_order o,t_setmeal s WHERE o.setmeal_id = s.id
     GROUP BY s.name
     ORDER BY setmeal_count DESC
     LIMIT 0,4

     */
    @Override
    public Map<String,Object> getBusinessReportData() {
        try {
            // 当前时间
            String today = DateUtils.parseDate2String(DateUtils.getToday());
            // 当前时间计算本周的周一
            String monday = DateUtils.parseDate2String(DateUtils.getThisWeekMonday());
            // 当前时间计算本周的周日
            String sunday = DateUtils.parseDate2String(DateUtils.getSundayOfThisWeek());
            // 当前时间计算本月的1号
            String firstMonth = DateUtils.parseDate2String(DateUtils.getFirstDay4ThisMonth());
            // 当前时间计算本月的最后1号
            String lastMonth = DateUtils.parseDate2String(DateUtils.getLastDay4ThisMonth());
            // 封装Map结构
            Map<String,Object> map = new HashMap<>();
            // 今日新增会员数
            Integer todayNewMember = memberDao.findTodayNewMember(today);
            // 总会员数
            Integer totalMember = memberDao.findTotalMember();
            // 本周新增会员数
            Integer thisWeekNewMember = memberDao.findThisNewMember(monday);
            // 本月新增会员数
            Integer thisMonthNewMember = memberDao.findThisNewMember(firstMonth);
            // 今日预约数
            Integer todayOrderNumber = orderDao.findTodayOrderNumber(today);
            // 今日到诊数
            Integer todayVisitsNumber = orderDao.findTodayVisitsNumber(today);
            // 本周预约数
            Map<String,String> weekOrderParam = new HashMap<>();
            weekOrderParam.put("begin",monday);
            weekOrderParam.put("end",sunday);
            Integer thisWeekOrderNumber = orderDao.findThisOrderNumber(weekOrderParam);
            // 本周到诊数
//            Map<String,String> weekVisitsParam = new HashMap<>();
//            weekVisitsParam.put("begin",monday);
//            weekVisitsParam.put("end",sunday);
            Integer thisWeekVisitsNumber = orderDao.findThisVisitsNumber(monday,sunday);
            // 本月预约数
            Map<String,String> monthOrderParam = new HashMap<>();
            monthOrderParam.put("begin",firstMonth);
            monthOrderParam.put("end",lastMonth);
            Integer thisMonthOrderNumber = orderDao.findThisOrderNumber(monthOrderParam);
            // 本月到诊数
//            Map<String,String> monthVisitsParam = new HashMap<>();
//            monthVisitsParam.put("begin",firstMonth);
//            monthVisitsParam.put("end",lastMonth);
            Integer thisMonthVisitsNumber = orderDao.findThisVisitsNumber(firstMonth,lastMonth);
            // 热门套餐
            List<Map> hotSetmeal = orderDao.findHotSetmeal();

            map.put("reportDate",today); // 字符串
            map.put("todayNewMember",todayNewMember); // 数字
            map.put("totalMember",totalMember);
            map.put("thisWeekNewMember",thisWeekNewMember);
            map.put("thisMonthNewMember",thisMonthNewMember);
            map.put("todayOrderNumber",todayOrderNumber);
            map.put("todayVisitsNumber",todayVisitsNumber);
            map.put("thisWeekOrderNumber",thisWeekOrderNumber);
            map.put("thisWeekVisitsNumber",thisWeekVisitsNumber);
            map.put("thisMonthOrderNumber",thisMonthOrderNumber);
            map.put("thisMonthVisitsNumber",thisMonthVisitsNumber);
            map.put("hotSetmeal",hotSetmeal); // List<Map>
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("未知异常");
        }
    }
}
