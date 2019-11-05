package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.service.MemberService;
import com.itheima.health.service.ReportService;
import com.itheima.health.service.SetmealService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName CheckItemController
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2019/10/23 15:58
 * @Version V1.0
 */
@RestController
@RequestMapping(value = "/report")
public class ReportController {

    @Reference
    MemberService memberService;

    @Reference
    SetmealService setmealService;

    @Reference
    ReportService reportService;


    // 会员按照月份注册数量折线图统计
    @RequestMapping(value = "/getMemberReport")
    public Result getMemberReport(){
        // 组织数据
        Map<String,Object> map = new HashMap<>();
        try {
            // 一：得到当前月计算前12个月（格式：YYYY-MM）
            List<String> months = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH,-12); // 根据当前月计算，将Calendar向前推12个月（此时Calendar对象变成2018-12）
            for (int i = 0; i < 12; i++) {
                calendar.add(Calendar.MONTH,1);// 此时Calendar对象变成2018-12  2019-01 2019-02 2019-03
                months.add(new SimpleDateFormat("YYYY-MM").format(calendar.getTime()));
            }
            // 二：得到每个月注册会员的总数量
            List<Integer> memberCounts = memberService.findMemberCountsByRegTime(months);
            map.put("months",months);
            map.put("memberCount",memberCounts);
            return new Result(true,MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_MEMBER_NUMBER_REPORT_FAIL);
        }

    }
    // 套餐预约占比饼形图统计
    @RequestMapping(value = "/getSetmealReport")
    public Result getSetmealReport(){
        // 组织数据
        Map<String,Object> map = new HashMap<>();
        try {
            List<String> setmealNames = new ArrayList<>();
            // 一：从数据库查询，组织每个套餐的预约数量
            List<Map> setmealCount = setmealService.findSetmealOrderCount();
            // 二：获取每个套餐，封装到setmealNames
            for (Map sMap : setmealCount) {
                String name = (String)sMap.get("name");
                setmealNames.add(name);
            }
            map.put("setmealNames",setmealNames);
            map.put("setmealCount",setmealCount);
            return new Result(true,MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_SETMEAL_COUNT_REPORT_FAIL);
        }

    }

    // 运营数据统计
    @RequestMapping(value = "/getBusinessReportData")
    public Result getBusinessReportData(){
        try {
            Map<String,Object> result = reportService.getBusinessReportData();
            return new Result(true,MessageConstant.GET_BUSINESS_REPORT_SUCCESS,result);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }

    // 运营数据统计，导出excel报表
    @RequestMapping(value = "/exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest request, HttpServletResponse response){
        try {
            // 一：查询excel中对应的数据
            Map<String,Object> result = reportService.getBusinessReportData();
            String reportDate = (String)result.get("reportDate"); // 字符串
            Integer todayNewMember = (Integer)result.get("todayNewMember"); // 数字
            Integer totalMember = (Integer)result.get("totalMember");
            Integer thisWeekNewMember = (Integer)result.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer)result.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer)result.get("todayOrderNumber");
            Integer todayVisitsNumber = (Integer)result.get("todayVisitsNumber");
            Integer thisWeekOrderNumber = (Integer)result.get("thisWeekOrderNumber");
            Integer thisWeekVisitsNumber = (Integer)result.get("thisWeekVisitsNumber");
            Integer thisMonthOrderNumber = (Integer)result.get("thisMonthOrderNumber");
            Integer thisMonthVisitsNumber = (Integer)result.get("thisMonthVisitsNumber");
            List<Map> hotSetmeal = (List<Map>)result.get("hotSetmeal"); // List<Map>
            // 二：读取项目中的template/report_template.xlsx
            // String path = request.getSession().getServletContext().getRealPath("/template/report_template.xlsx");
            String path = request.getSession().getServletContext().getRealPath("/template")+ File.separator+"report_template.xlsx";
            // 三：将从数据库查询的数据，填写到对应excel模板中的单元格中
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(path)));
            // 获取第1个工作表
            XSSFSheet sheet = workbook.getSheetAt(0);
            // 设置日期
            XSSFRow row = sheet.getRow(2);
            row.getCell(5).setCellValue(reportDate);
            // 设置新增会员数和总会员数
            row = sheet.getRow(4);
            row.getCell(5).setCellValue(todayNewMember);
            row.getCell(7).setCellValue(totalMember);

            row = sheet.getRow(5);
            row.getCell(5).setCellValue(thisWeekNewMember);//本周新增会员数
            row.getCell(7).setCellValue(thisMonthNewMember);//本月新增会员数

            row = sheet.getRow(7);
            row.getCell(5).setCellValue(todayOrderNumber);//今日预约数
            row.getCell(7).setCellValue(todayVisitsNumber);//今日到诊数

            row = sheet.getRow(8);
            row.getCell(5).setCellValue(thisWeekOrderNumber);//本周预约数
            row.getCell(7).setCellValue(thisWeekVisitsNumber);//本周到诊数

            row = sheet.getRow(9);
            row.getCell(5).setCellValue(thisMonthOrderNumber);//本月预约数
            row.getCell(7).setCellValue(thisMonthVisitsNumber);//本月到诊数

            int rowNum = 12;
            for(Map map : hotSetmeal){//热门套餐
                String name = (String) map.get("name");
                Long setmeal_count = (Long) map.get("setmeal_count");
                BigDecimal proportion = (BigDecimal) map.get("proportion");
                row = sheet.getRow(rowNum ++);
                row.getCell(4).setCellValue(name);//套餐名称
                row.getCell(5).setCellValue(setmeal_count);//预约数量
                row.getCell(6).setCellValue(proportion.doubleValue());//占比
            }

            // 四：使用输出流，将excel文件输出到缓存区，用于下载
            ServletOutputStream out = response.getOutputStream();
            // 设置文件下载的类型
            response.setContentType("application/vnd.ms-excel");
            // 设置文件下载的形式（头）（2种形式，1种内连(inline)，另1种附件）
            response.setHeader("Content-Disposition","attachment;filename=reportHealth76.xlsx");

            workbook.write(out);
            out.flush();
            out.close();
            workbook.close();
            return null; // null表示不需要响应任何数据，用在下载，导出（IO流）
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }
}
