package com.wellsoft.context.util.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Description: Excel工作表单封装类
 *
 * @author Asus
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年12月25日.1	Asus		2015年12月25日		Create
 * </pre>
 * @date 2015年12月25日
 */
public class ExcelSheetCell {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelSheetCell.class);

    /**
     * 用于产生当前excel标题
     *
     * @param sheetRow      [当前工作表单]
     * @param work          [当前工作薄]
     * @param firstRowValue [标题数组]
     * @param style         [当前单元格风格]
     */
    public static void createCurrRowTitle(ExcelWorkBook work, String[] firstRowValue, CellStyle style) {
        Row row = ExcelSheetRow.createCurrSheetTitle(work);
        for (int i = 0; i < firstRowValue.length; i++) {
            Cell cell = row.createCell((short) i);
            cell.setCellStyle(style);
            cell.setCellValue(firstRowValue[i]);
        }
    }

    /**
     * 用于生成excel标题
     *
     * @param sheetRow      [当前工作表单]
     * @param work          [当前工作薄]
     * @param firstRowValue 标题列内容
     */
    public static void createCurrRowTitle(ExcelWorkBook work, String[] firstRowValue) {
        createCurrRowTitle(work, firstRowValue, work.getWorkbook().createCellStyle());
    }

    /**
     * 用于生成excel当前记录内容,标题除外
     *
     * @param sheetRow [当前工作表单]
     * @param work     [当前工作薄]
     * @param beanList [当前数据列表,i=Object[]]
     * @param style    [当前单元格风格]
     */
    public static void createCurrRowRecord(ExcelWorkBook work, List<Object[]> beanList, CellStyle style) {
        Object[] obj = null;
        for (int i = 0; i < beanList.size(); i++) {
            Row row = ExcelSheetRow.createCurrSheetRecord(work, i);
            obj = (Object[]) beanList.get(i);
            if (obj != null) {
                createExcelCell(row, obj, style);
            }
        }
    }

    /**
     * 用于生成excel当前记录内容,标题除外
     *
     * @param sheetRow
     * @param work
     * @param beanList
     */
    public static void createCurrRowRecord(ExcelWorkBook work, List<Object[]> beanList) {
        createCurrRowRecord(work, beanList, work.getWorkbook().createCellStyle());
    }

    /**
     * 需要以数组的方式提供当前每条记录 通过数组自动判断有多少列,生成当前行
     *
     * @param row   excel行实体
     * @param obj   记录内容
     * @param style 样式
     */
    private static void createExcelCell(Row row, Object[] obj, CellStyle style) {
        if (obj == null || obj.length <= 0) {
            return;
        }
        try {
            for (int i = 0; i < obj.length; i++) {
                Cell cell = row.createCell((short) i);
                if (obj[i] != null && cell != null) {
                    cell.setCellStyle(style);
                    ExcelSheetCell.setCellValue(cell, obj[i]);
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private static void setCellValue(Cell cell, Object obj) {
        if (obj instanceof String) {
            cell.setCellValue((String) obj);
        } else if (obj instanceof RichTextString) {
            cell.setCellValue((RichTextString) obj);
        } else if (obj instanceof Date) {
            cell.setCellValue((Date) obj);
        } else if (obj instanceof Calendar) {
            cell.setCellValue((Calendar) obj);
        } else if (obj instanceof Double) {
            cell.setCellValue((Double) obj);
        } else if (obj instanceof Boolean) {
            cell.setCellValue((Boolean) obj);
        } else {
            cell.setCellValue(obj.toString());
        }
    }
}
