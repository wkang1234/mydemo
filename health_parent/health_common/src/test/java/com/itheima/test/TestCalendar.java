package com.itheima.test;

import com.itheima.health.utils.DateUtils;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @ClassName TestPOI
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2019/10/28 14:55
 * @Version V1.0
 */
public class TestCalendar {

    // 测试日期
    @Test
    public void textCalendar() throws IOException {
        List<String> months = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,-12); // 根据当前月计算，将Calendar向前推12个月（此时Calendar对象变成2018-12）
        for (int i = 0; i < 12; i++) {
            calendar.add(Calendar.MONTH,1);// 此时Calendar对象变成2018-12  2019-01 2019-02 2019-03
            months.add(new SimpleDateFormat("YYYY-MM").format(calendar.getTime()));
        }
        System.out.println(months);
    }

    @Test
    public void testReport() throws Exception {
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
    }

}
