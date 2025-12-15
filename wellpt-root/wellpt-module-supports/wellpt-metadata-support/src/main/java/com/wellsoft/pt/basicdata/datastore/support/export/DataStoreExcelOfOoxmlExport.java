package com.wellsoft.pt.basicdata.datastore.support.export;

import com.wellsoft.pt.basicdata.datastore.support.DataStoreConfiguration;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreConfigurationBuilder;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Description: 导出Excel 2007
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年2月26日.1	zhulh		2020年2月26日		Create
 * </pre>
 * @date 2020年2月26日
 */
@Component
@Order(5)
public class DataStoreExcelOfOoxmlExport extends DataStoreExcelExport {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.export.AbstractDataStoreExcelExport#export(com.wellsoft.pt.basicdata.datastore.support.export.ExportParams)
     */
    @Override
    public DataSource export(ExportParams params) {
        DataStoreConfiguration dataStoreConfiguration = DataStoreConfigurationBuilder.buildFromDataStoreId(params
                .getParams().getDataStoreId());
        Workbook wb = new XSSFWorkbook();
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
        return new ExportDataSource(new ByteArrayInputStream(os.toByteArray()), CONTENT_TYPE, params.getFileName()
                + SUFFIX_XLSX);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.export.DataStoreExcelExport#getRichTextString(java.lang.String)
     */
    @Override
    protected RichTextString getRichTextString(String string) {
        return new XSSFRichTextString(string);
    }

    @Override
    public String getType() {
        return "excel_ooxml";
    }

    @Override
    public String getName() {
        return "按导出列定义的字段导出Excel(2007)";
    }

}
