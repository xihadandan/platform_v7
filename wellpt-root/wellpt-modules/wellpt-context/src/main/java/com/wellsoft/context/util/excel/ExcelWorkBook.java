package com.wellsoft.context.util.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.*;

/**
 * Description: excel工作薄封装类
 *
 * @author Asus
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年12月24日.1	Asus		2015年12月24日		Create
 * </pre>
 * @date 2015年12月24日
 */
public class ExcelWorkBook {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelWorkBook.class);

    public Workbook workbook = null;

    // 设置当前workbookName
    private String workbookName = null;

    private Sheet sheet = null;

    /**
     * 创建excel工作薄封装类
     */
    public ExcelWorkBook() {
        workbook = new HSSFWorkbook();
    }

    /**
     * 创建excel工作薄封装类
     *
     * @param workbookName 获取excel名称
     */
    public ExcelWorkBook(String workbookName) {
        workbook = new HSSFWorkbook();
        setWorkbookName(workbookName);
    }

    /**
     * 创建excel工作薄封装类
     *
     * @param workbook see@Workbook
     */
    public ExcelWorkBook(Workbook workbook) {
        Assert.notNull(workbook, "workbook is not null");
        this.workbook = workbook;
    }

    /**
     * 创建excel工作薄封装类
     *
     * @param workbook     see@Workbook
     * @param workbookName 获取excel名称
     */
    public ExcelWorkBook(Workbook workbook, String workbookName) {
        Assert.notNull(workbook, "workbook is not null");
        Assert.notNull(workbookName, "workbookName is not null");
        this.workbook = workbook;
        this.workbookName = workbookName;
    }

    /**
     * 获取excel名称
     *
     * @return excel名称
     */
    public String getWorkbookName() {
        return workbookName;
    }

    /**
     * 设置excel名称
     *
     * @param workbookName excel名称
     */
    public void setWorkbookName(String workbookName) {
        this.workbookName = workbookName;
    }

    /**
     * 返回sheet
     *
     * @return Sheet
     */
    public Sheet getSheet() {
        if (sheet == null) {
            sheet = workbook.createSheet();
            // 防止中文乱码
            workbook.setSheetName(0, workbookName);
        }
        return sheet;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    /**
     * 写到指定目录
     *
     * @param destDir   目录地址
     * @param excelName excel名
     */
    public void write(String destDir, String excelName) {
        File file = new File(destDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        writerFileStream(destDir + File.separator + excelName);
    }

    /**
     * 输入当前WorkBook为下载临时文件记录
     *
     * @param excelName
     */
    public void writerFileStream(String excelName) {
        OutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(excelName);
            workbook.write(fileOut);
            fileOut.flush();
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                if (fileOut != null) {
                    fileOut.close();
                }
                if (workbook != null) {
                    workbook = null;
                }
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
