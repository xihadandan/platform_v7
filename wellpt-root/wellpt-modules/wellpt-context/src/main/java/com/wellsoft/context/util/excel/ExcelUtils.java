/*
 * @(#)2014-6-6 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.excel;

import com.alibaba.excel.EasyExcel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-6-6.1	wubin		2014-6-6		Create
 * </pre>
 * @date 2014-6-6
 */
public class ExcelUtils {

    private static Logger LOG = LoggerFactory.getLogger(ExcelUtils.class);

    public static File generateExcelFile(List<String[]> dataList, String[] titleArray,
                                         String fileName) {
        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = generateExcelBook(dataList, titleArray);

        // 第六步，将文件存到指定位置
        try {
            FileOutputStream fout = new FileOutputStream("E:/" + fileName + ".xls");
            wb.write(fout);
            fout.close();
            return new File("E:/" + fileName + ".xls");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }

    }

    public static HSSFWorkbook generateExcelBook(List<String[]> dataList, String[] titleArray) {
        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();

        // 没有任何效果，估计是无用的代码，屏蔽掉， zyguo
        // WritableFont font = new WritableFont(WritableFont.TIMES, 20,
        // WritableFont.NO_BOLD);
        // try {
        // font.setColour(jxl.format.Colour.RED);
        // WritableCellFormat formatTitle = new WritableCellFormat(font);
        // formatTitle.setWrap(true);
        // } catch (WriteException e1) {
        // LOG.error(e1.getMessage(), e1);
        // }

        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("Sheet1");
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        HSSFRow titleRow = sheet.createRow((short) 0);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式

        HSSFCell cell = titleRow.createCell((short) 0);
        for (int i = 0; i < titleArray.length; i++) {
            cell.setCellValue(titleArray[i]);
            cell.setCellStyle(style);
            cell = titleRow.createCell((short) ((short) i + 1));
        }
        // 第五步，按行写数据到表格
        for (int i = 0; i < dataList.size(); i++) {
            HSSFRow row = sheet.createRow(i + 1);
            String[] dataArray = dataList.get(i);
            // 第四步，创建单元格，并设置值
            for (int j = 0; j < dataArray.length; j++) {
                row.createCell(j).setCellValue(dataArray[j]);
            }
        }
        return wb;
    }

    public static Sheet getSheetFromInputStream(InputStream is, String sheetName) {
        // 因为 excel 有多个版本，所以需要多次尝试，但是 inputStream 只能读取一次，所以这里讲inputStream需要缓存起来
        try {
            byte[] body = IOUtils.toByteArray(is);
            Workbook workbook = null;
            try {
                workbook = new XSSFWorkbook(new ByteArrayInputStream(body));
            } catch (Exception ex) {
                try {
                    workbook = new HSSFWorkbook(new ByteArrayInputStream(body));
                } catch (IOException e) {

                }
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }

            if (workbook != null) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    sheet = workbook.getSheetAt(0);
                }
                return sheet;
            }

        } catch (IOException e) {
            LOG.error("缓存inputStream异常", e);
        }

        return null;
    }


    /**
     * 通过ExcelDo对象，简单写excel
     *
     * @param filePathName excel文件路径
     * @param datas        数据列表
     * @param sheetName
     * @param <T>
     */
    public static <T extends ExcelDo> void simpleWritePojo(String filePathName, List<T> datas,
                                                           String sheetName) {
        if (CollectionUtils.isNotEmpty(datas))
            EasyExcel.write(filePathName, datas.get(0).getClass()).sheet(
                    StringUtils.defaultIfBlank(sheetName, "sheet")).doWrite(datas);
    }


    /**
     * 通过List构造head/data，简单写excel
     *
     * @param filePathName
     * @param heads
     * @param datas
     * @param sheetName
     */
    public static void simpleWriteList(String filePathName, List<List<String>> heads,
                                       List<List<Object>> datas,
                                       String sheetName) {
        if (CollectionUtils.isNotEmpty(heads) && CollectionUtils.isNotEmpty(datas)) {
            EasyExcel.write(filePathName).head(heads).sheet(
                    StringUtils.defaultIfBlank(sheetName, "sheet")).doWrite(datas);
        }
    }
}
