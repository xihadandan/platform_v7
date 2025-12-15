package com.wellsoft.context.util.excel;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

/**
 * Description: excel样式封装类
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
public class ExcelCellStyleUtils {
    /**
     * 标题样式
     */
    public CellStyle titleStyle;

    /**
     * 时间样式
     */
    public CellStyle dataStyle;
    /**
     * 单元格样式
     */
    public CellStyle nameStyle;

    /**
     * 超链接样式
     */
    public CellStyle linkStyle;
    /**
     * 超链接字体样式
     */
    public Font font;

    /**
     * 创建一个ExcelCellStyleUtils
     *
     * @param ExcelWorkBook work
     */
    public ExcelCellStyleUtils(ExcelWorkBook work) {
        titleStyle = titleStyle(work.getWorkbook());
        dataStyle = dataStyle(work.getWorkbook());
        nameStyle = nameStyle(work.getWorkbook());
        linkStyle = linkStyle(work.getWorkbook());
        font = font(work.getWorkbook());
    }

    /**
     * 创建一个ExcelCellStyleUtils
     *
     * @param Workbook workbook
     */
    public ExcelCellStyleUtils(Workbook workbook) {
        titleStyle = titleStyle(workbook);
        dataStyle = dataStyle(workbook);
        nameStyle = nameStyle(workbook);
        linkStyle = linkStyle(workbook);
        font = font(workbook);
    }

    /**
     * 标题样式
     *
     * @return CellStyle
     */
    private CellStyle titleStyle(Workbook work) {
        CellStyle titleStyle = work.createCellStyle();
        Font font = work.createFont();
        font.setBold(true);
        font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        titleStyle.setFont(font);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setBorderBottom(BorderStyle.DOUBLE);
        titleStyle.setBorderLeft(BorderStyle.THIN);
        titleStyle.setBorderRight(BorderStyle.THIN);
        titleStyle.setBorderBottom(BorderStyle.DOUBLE);
        titleStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return titleStyle;
    }

    /**
     * 时间样式
     *
     * @return CellStyle
     */
    private CellStyle dataStyle(Workbook work) {
        CellStyle dataStyle = work.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_GREEN.getIndex());
        dataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return dataStyle;
    }

    /**
     * 单元格样式
     *
     * @return CellStyle
     */
    private CellStyle nameStyle(Workbook work) {
        CellStyle nameStyle = work.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        nameStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_CORNFLOWER_BLUE.getIndex());
        nameStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return nameStyle;
    }

    /**
     * 超链接样式
     *
     * @return CellStyle
     */
    private CellStyle linkStyle(Workbook work) {
        CellStyle linkStyle = work.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        linkStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.SKY_BLUE.getIndex());
        linkStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = work.createFont();
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setUnderline((byte) 1);
        font.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        linkStyle.setFont(font);
        return linkStyle;
    }

    /**
     * 字体
     *
     * @return Font
     */
    private Font font(Workbook work) {
        font = work.createFont();
        // font.setItalic(true);// 斜体
        font.setBold(true);
        font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        return font;
    }
}
