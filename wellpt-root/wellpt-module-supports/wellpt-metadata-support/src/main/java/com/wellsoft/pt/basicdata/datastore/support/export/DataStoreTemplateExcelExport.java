package com.wellsoft.pt.basicdata.datastore.support.export;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreConfiguration;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreConfigurationBuilder;
import com.wellsoft.pt.jpa.template.TemplateEngine;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 按导出模板进行数据导出，导出模板支持解析freemarker表达式
 *
 * @author chenq
 * @date 2018/10/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/10/10    chenq		2018/10/10		Create
 * </pre>
 */
@Component
@Order(1)
public class DataStoreTemplateExcelExport extends AbstractDataStoreExcelExport {

    @Autowired
    MongoFileService mogMongoFileService;

    @Override
    public DataSource export(ExportParams params) {
        DataStoreConfiguration dataStoreConfiguration = DataStoreConfigurationBuilder.buildFromDataStoreId(
                params.getParams().getDataStoreId());
        try {
            Workbook workbook = WorkbookFactory.create(
                    mogMongoFileService.getFile(params.getExportTemplateId()).getInputstream());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            createExcelConent(params, workbook, dataStoreConfiguration);
            workbook.write(os);
            String suffix = SUFFIX;
            String contentType = CONTENT_TYPE;
            if (workbook.getSpreadsheetVersion().equals(SpreadsheetVersion.EXCEL2007)) {
                suffix = ".xlsx";
                contentType = ((XSSFWorkbook) workbook).getPackagePart().getContentType();
            }
            return new ExportDataSource(new ByteArrayInputStream(os.toByteArray()), contentType,
                    params.getFileName() + suffix);
        } catch (Exception e) {
            logger.error("按导出模板文件导出excel异常：{}", Throwables.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createExcelConent(ExportParams params, Workbook workbook,
                                  DataStoreConfiguration dataStoreConfiguration) {
        try {

            List<Map<String, Object>> dataList = params.getData().getData();//数据集
            Map<String, Object> root = TemplateEngineFactory.getExplainRootModel();
            TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();

            root.put("search", params.getSearchSnapshot());//查询条件的参数
            root.put("extras", params.getExtras());

            Sheet sheet = workbook.getSheetAt(0);
            //开始解析非导出数据行
            Iterator<Row> rowIterator = sheet.rowIterator();
            Integer dataBeginRow = null;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cells = row.cellIterator();
                while (cells.hasNext()) {
                    Cell cell = cells.next();
                    String value = getCellValue(cell);
                    if (StringUtils.isNotBlank(value) && value.indexOf("${row.") != -1) {
                        //解析到了数据行
                        dataBeginRow = row.getRowNum();
                        break;
                    }
                    if (StringUtils.isNotBlank(value) && value.indexOf("${") != -1) {
                        cell.setCellValue(explainFreemarkString(templateEngine, value, root));
                    }
                }
                if (dataBeginRow != null) {
                    break;
                }
            }

            //开始解析导出数据行
            if (dataBeginRow != null) {
                Map<Integer, String> cellExpressionMap = Maps.newHashMap();
                Map<Integer, CellStyle> cellStyleMap = Maps.newHashMap();
                Row beginRow = sheet.getRow(dataBeginRow);
                Iterator<Cell> dataCells = beginRow.cellIterator();
                while (dataCells.hasNext()) {
                    Cell cell = dataCells.next();
                    String value = getCellValue(cell);
                    cellExpressionMap.put(cell.getColumnIndex(), value);//单元格表达式
                    cellStyleMap.put(cell.getColumnIndex(), cell.getCellStyle());//单元格样式
                }

                sheet.removeRow(beginRow);//移除数据模板行

                for (int i = 0, len = dataList.size(); i < len; i++) {
                    Map<String, Object> data = dataList.get(i);
                    Row dataRow = sheet.createRow(dataBeginRow++);
                    data.put("rowid", i + 1);//序号
                    root.put("row", data);//行数据
                    Set<Integer> cellKeys = cellExpressionMap.keySet();
                    for (Integer cellIndex : cellKeys) {//创建单元格
                        Cell newCell = dataRow.createCell(cellIndex.shortValue());
                        newCell.setCellValue(explainFreemarkString(templateEngine,
                                cellExpressionMap.get(cellIndex), root));
                        newCell.setCellStyle(cellStyleMap.get(cellIndex));
                    }
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String explainFreemarkString(TemplateEngine templateEngine, String value,
                                         Map<String, Object> root) {
        try {
            return templateEngine.process(value, root);
        } catch (Exception e) {
            logger.error("解析freemark字符串异常：", e);
        }
        return "";
    }

    private String getCellValue(Cell cell) {

        if (CellType.NUMERIC == (cell.getCellType())) {
            return cell.getNumericCellValue() + "";
        }

        if (CellType.STRING == (cell.getCellType())) {
            return cell.getStringCellValue();
        }


        return "";
    }


    @Override
    public String getType() {
        return "templateExcel";
    }

    @Override
    public String getName() {
        return "按导出模板导出Excel";
    }
}
