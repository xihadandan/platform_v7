package com.wellsoft.pt.basicdata.datastore.support.export;

import com.wellsoft.pt.basicdata.datastore.support.DataStoreConfiguration;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Order(4)
public class DataStoreExcelExport extends AbstractDataStoreExcelExport {
    public void createExcelConent(ExportParams params, Workbook wb, DataStoreConfiguration dataStoreConfiguration) {
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        Sheet sheet = wb.createSheet(getSheetName(dataStoreConfiguration));
        sheet.setDefaultColumnWidth((short) 30);
        int rowNum = 0;
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        Row row = sheet.createRow((int) rowNum++);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        // 标题
        CellStyle titleStyle = getTitleStyle(wb);
        int titleRow = 0;
        for (ExportColumn column : params.getColumns()) {
            Cell cell = row.createCell((short) ((short) titleRow++));
            cell.setCellValue(getRichTextString(column.getTitle()));
            cell.setCellStyle(titleStyle);
        }
        ConversionService conversionService = new DefaultConversionService();
        // 第五步，写入实体数据 实际应用中这些数据从数据库得到，
        List<Map<String, Object>> dataList = params.getData().getData();
        CellStyle leftStyle = getContentStyle(wb, HorizontalAlignment.LEFT);
        CellStyle centerStyle = getContentStyle(wb);
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> data = dataList.get(i);
            row = sheet.createRow((short) rowNum++);
            int j = 0;
            for (ExportColumn column : params.getColumns()) {
                String columnIndex = column.getColumnIndex();
                Cell cell = row.createCell((short) j++);
                Object value = data.get(columnIndex + "RenderValue") == null ? data.get(columnIndex) : data
                        .get(columnIndex + "RenderValue");
                cell.setCellValue(getRichTextString(html2Text(conversionService.convert(value, String.class))));
                if (value != null && value.getClass().isAssignableFrom(String.class)) {
                    cell.setCellStyle(leftStyle);
                } else {
                    cell.setCellStyle(centerStyle);
                }

            }
        }

    }

    /**
     * @param string
     * @return
     */
    protected RichTextString getRichTextString(String string) {
        return new HSSFRichTextString(string);
    }

    @Override
    public String getType() {
        return "excel";
    }

    @Override
    public String getName() {
        return "按导出列定义的字段导出Excel(97-2003)";
    }

}
