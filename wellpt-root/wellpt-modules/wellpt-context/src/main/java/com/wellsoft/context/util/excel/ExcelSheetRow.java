package com.wellsoft.context.util.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Description: excel 行封装类
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
public class ExcelSheetRow {
    public ExcelSheetRow() {
    }

    /**
     * 创建当前标题行
     *
     * @param work 工作簿
     * @return 当前标题行Row对象
     */
    public static Row createCurrSheetTitle(ExcelWorkBook work) {
        Row row = null;
        Sheet sheet = work.getSheet();
        row = sheet.createRow(0);
        return row;
    }

    /**
     * 创建当前excel记录内容
     *
     * @param work 工作簿
     * @param i    第几行
     * @return 第几行Row对象
     */
    public static Row createCurrSheetRecord(ExcelWorkBook work, int i) {
        Row row = null;
        Sheet sheet = work.getSheet();
        row = sheet.createRow(i + 1);
        return row;
    }
}
