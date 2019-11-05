package com.itheima.test;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @ClassName TestPOI
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2019/10/28 14:55
 * @Version V1.0
 */
public class TestPOI {

    // 从Excel中读取数据
    @Test
    public void readExcel() throws IOException {
        // 1：创建工作簿对象
        XSSFWorkbook workbook = new XSSFWorkbook("d:/hello.xlsx");
        // 2：获取第1个工作表
        XSSFSheet sheet = workbook.getSheetAt(0);
        // 遍历工作表对象，获取行
        for (Row row : sheet) {
            // 遍历行的对象，获取单元格
            for (Cell cell : row) {
                // 获取数据
                System.out.println(cell.getStringCellValue());
            }
        }
        // 关闭
        workbook.close();
    }

    /**
     * 还有一种方式就是获取工作表最后一个行号，从而根据行号获得行对象，通过行获取最后一个单元格索引，从而根据单元格索引获取每行的一个单元格对象
     */
    @Test
    public void readExcel2() throws IOException {
        // 1：创建工作簿对象
        XSSFWorkbook workbook = new XSSFWorkbook("d:/hello1.xlsx");
        // 2：获取第1个工作表
        XSSFSheet sheet = workbook.getSheetAt(0);
        // 遍历工作表对象，获取行
        int rows = sheet.getLastRowNum();
        System.out.println("rows:"+rows);
        for(int i=0;i<=rows;i++){
            XSSFRow row = sheet.getRow(i);
            short cells = row.getLastCellNum();
            for(int j=0;j<cells;j++) {
                // 遍历行的对象，获取单元格
                XSSFCell cell = row.getCell(j);
                System.out.println(cell.getStringCellValue());
            }

        }
        // 关闭
        workbook.close();
    }

    /**
     * 2.2.2. 向Excel文件写入数据
     【需求】
     使用POI可以在内存中创建一个Excel文件并将数据写入到这个文件，最后通过输出流将内存中的Excel文件下载到磁盘
     */
    @Test
    public void writeExcel() throws IOException {
        // 1.创建工作簿对象
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 2.创建工作表对象
        XSSFSheet sheet = workbook.createSheet("用户报表");
        // 3.创建行对象（第一行）
        XSSFRow row = sheet.createRow(0);
        // 4.创建列(单元格)对象, 设置内容
        row.createCell(0).setCellValue("姓名");
        row.createCell(1).setCellValue("年龄");
        row.createCell(2).setCellValue("地址");

        // 3.创建行对象（第二行）
        XSSFRow row1 = sheet.createRow(1);
        // 4.创建列(单元格)对象, 设置内容
        row1.createCell(0).setCellValue("张三");
        row1.createCell(1).setCellValue("22");
        row1.createCell(2).setCellValue("北京");

        // 3.创建行对象（第三行）
        XSSFRow row2 = sheet.createRow(2);
        // 4.创建列(单元格)对象, 设置内容
        row2.createCell(0).setCellValue("李四");
        row2.createCell(1).setCellValue("20");
        row2.createCell(2).setCellValue("上海");

        // 3.创建行对象（第四行）
        XSSFRow row3 = sheet.createRow(3);
        // 4.创建列(单元格)对象, 设置内容
        row3.createCell(0).setCellValue("王五");
        row3.createCell(1).setCellValue("22");
        row3.createCell(2).setCellValue("深圳");
        // 5.通过输出流将workbook对象下载到磁盘
        FileOutputStream out = new FileOutputStream("d:/report76.xlsx");
        workbook.write(out);
        out.flush();// 刷新缓冲区，将数据从缓冲区读取
        out.close();
        workbook.close();
    }
}
