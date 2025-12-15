/*
 * @(#)Nov 9, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support.export;

import com.wellsoft.pt.basicdata.datastore.support.DataStoreConfiguration;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreConfigurationBuilder;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.TextExtractingVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Nov 9, 2017.1	zhulh		Nov 9, 2017		Create
 * </pre>
 * @date Nov 9, 2017
 */
public abstract class AbstractDataStoreExcelExport implements DataStoreExport {
    protected final static Logger logger = LoggerFactory.getLogger(
            AbstractDataStoreExcelExport.class);

    protected static final String CONTENT_TYPE = "application/x-xls; charset=UTF-8";
    protected static final String SUFFIX = ".xls";
    protected static final String SUFFIX_XLSX = ".xlsx";

    public final static String html2Text(String inputString) {
        if (StringUtils.isBlank(inputString)) {
            return StringUtils.EMPTY;
        }
        String textStr = inputString;
        if (isHtml(inputString)) {
            Parser parser;
            try {
                parser = new Parser(inputString);
                parser.setEncoding("UTF-8");
                // 创建StringFindingVisitor对象
                TextExtractingVisitor visitor = new TextExtractingVisitor();
                // 去除网页中的所有标签,提出纯文本内容
                parser.visitAllNodesWith(visitor);
                textStr = visitor.getExtractedText();
            } catch (ParserException ex) {
                // logger.error(ex.getMessage(), ex);
            }
        }
        return textStr;
    }

    /**
     * @param string
     * @return
     */
    private static boolean isHtml(String string) {
        int length = string.length();
        boolean html = false;
        char ch;
        for (int i = 0; i < length; i++) {
            ch = string.charAt(i);
            if (!Character.isWhitespace(ch)) {
                if ('<' == ch) {
                    html = true;
                }
                break;
            }
        }
        return html;
    }

    public abstract void createExcelConent(ExportParams params, Workbook wb,
                                           DataStoreConfiguration dataStoreConfiguration);

    /**
     * @return
     */
    public String getContentType() {
        return CONTENT_TYPE;
    }

    /**
     * @return
     */
    public String getSuffix() {
        return SUFFIX;
    }

    /**
     * @param dataStoreConfiguration
     * @return
     */
    protected String getSheetName(DataStoreConfiguration dataStoreConfiguration) {
        String sheet1 = dataStoreConfiguration != null ? dataStoreConfiguration.getName() : null;
        return StringUtils.isBlank(sheet1) ? "Sheet1" : sheet1;
    }

    protected CellStyle getTitleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
        Font font = wb.createFont();
        font.setBold(true);// 粗体显示
        style.setFont(font);
        return style;
    }

    protected CellStyle getContentStyle(Workbook wb) {
        return getContentStyle(wb, HorizontalAlignment.CENTER);// 创建一个居中格式
    }

    protected CellStyle getContentStyle(Workbook wb, HorizontalAlignment alignment) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(alignment); // 创建一个居中格式
        return style;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.export.DataStoreExport#export(com.wellsoft.pt.basicdata.datastore.support.export.ExportParams)
     */
    @Override
    public DataSource export(ExportParams params) {
        DataStoreConfiguration dataStoreConfiguration = StringUtils.isNotBlank(params.getParams().getDataStoreId()) ? DataStoreConfigurationBuilder.buildFromDataStoreId(
                params.getParams().getDataStoreId()) : null;
        HSSFWorkbook wb = new HSSFWorkbook();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        WritableFont font = new WritableFont(WritableFont.TIMES, 20, WritableFont.NO_BOLD);
        try {
            font.setColour(jxl.format.Colour.RED);
            WritableCellFormat formatTitle = new WritableCellFormat(font);
            formatTitle.setWrap(true);
            createExcelConent(params, wb, dataStoreConfiguration);
            wb.write(os);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
            throw new RuntimeException(e);
        }
        return new ExportDataSource(new ByteArrayInputStream(os.toByteArray()), CONTENT_TYPE,
                params.getFileName() + SUFFIX);
    }

}
