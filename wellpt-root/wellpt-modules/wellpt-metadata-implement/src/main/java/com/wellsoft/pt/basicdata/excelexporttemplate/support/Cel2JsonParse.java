package com.wellsoft.pt.basicdata.excelexporttemplate.support;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;

public class Cel2JsonParse {
    private static final String UFSTART_UF = "uf_";
    private static final String UFSTART_USERFORM = "userform_";
    private static final String UFEND = "uf_end";
    private static final Map<Integer, Class<?>> cellTypeName = new HashMap<Integer, Class<?>>();

    static {
        cellTypeName.put(3, String.class);
        cellTypeName.put(1, String.class);
        cellTypeName.put(4, Boolean.class);
        cellTypeName.put(5, Byte.class);
        cellTypeName.put(0, Double.class);// 0有可能是数字也有可能是日期(日期->-数字)
        cellTypeName.put(2, String.class);
    }

    protected Logger logger = LoggerFactory.getLogger(Cel2JsonParse.class);
    private Workbook hssfWorkbook = null;
    private JsonConfig config = null;
    private Map<String, List<Map<String, Object>>> formDatas = new HashMap<String, List<Map<String, Object>>>();
    private Map<String, List<String>> deletedFormDatas = new HashMap<String, List<String>>();
    private String formUuid;

    public Cel2JsonParse(InputStream inputStream) {
        this(inputStream, new JsonConfig());
    }

    public Cel2JsonParse(InputStream inputStream, JsonConfig jsonConfig) {
        try {
            // default Office2007
            this.hssfWorkbook = new XSSFWorkbook(inputStream); // 解析excel
        } catch (IOException ex) {
            logger.error("ExcelParse Constructor Exception:" + ex.getMessage(), ex);
            throw new RuntimeException("ExcelParse Constructor Exception:" + ex.getMessage(), ex);
        }
        this.config = jsonConfig;
    }

    public Cel2JsonParse(Workbook hssfWorkbook) {
        this(hssfWorkbook, new JsonConfig());
    }

    public Cel2JsonParse(Workbook hssfWorkbook, JsonConfig jsonConfig) {
        this.hssfWorkbook = hssfWorkbook;
        this.config = jsonConfig;
    }

    /**
     * 获取单元格的值
     *
     * @param hssfCell
     * @return
     */
    public static Object getValue(Cell hssfCell) {
        switch (hssfCell.getCellType().getCode()) {
            case 3:
                return null;
            case 4:
                return hssfCell.getBooleanCellValue();
            case 5:
                return hssfCell.getErrorCellValue();
            case 2:
                return hssfCell.getCellFormula();
            case 0:
                if (PoiDateUtil.isCellDateFormatted(hssfCell)) { // 取Date
                    return hssfCell.getDateCellValue();
                }
                // modify by wujx 2016-01-27 begin
                DecimalFormat df = new DecimalFormat("##########.##########"); // 取Number
                return df.format(hssfCell.getNumericCellValue());
            // modify by wujx 2016-01-27 end
            case 1:
                return hssfCell.getStringCellValue();
        }
        return "Unknown Cell Type: " + hssfCell.getCellType();
    }

    private static List<Map<String, Object>> getListField(Sheet hssfSheet, Map<String, String> refName, int startRow,
                                                          String headKey) {
        List<Map<String, Object>> listFields = new ArrayList<Map<String, Object>>();
        Row headerRow = hssfSheet.getRow(startRow);
        short firstColNum = headerRow.getFirstCellNum();
        short lastColNum = headerRow.getLastCellNum();
        // List<String> header = new ArrayList<String>();
        Map<Integer, String> header = new HashMap<Integer, String>();
        for (int jCol = firstColNum; jCol < lastColNum; jCol++) { // 获取表头信息
            Cell hssfCell = headerRow.getCell(jCol);
            if (hssfCell == null) {
                continue;
            }
            String value = hssfCell.getStringCellValue();
            // if (value == null || value.equals("")) {
            // continue;
            // }
            String key = new CellReference(hssfCell).formatAsString(false);
            value = refName.get(key) == null ? value : refName.get(key);// 如果有头名称则key为名称,否则为头单元格值
            // 处理重名字段,去掉表头,xm_uf_xx -> xm
            if (StringUtils.isBlank(value)) {
                continue;
            } else if (value.endsWith(headKey)) {
                value = value.replace("_" + headKey, "");// value.substring(0,
                // value.indexOf("_"
                // + headKey));//
                // 从表处理
            }
            // header.add(value);
            header.put(hssfCell.getColumnIndex(), value);
        }
        boolean flag = true; // break end
        int firstRowNum = startRow + 1;
        int lastRowNum = hssfSheet.getLastRowNum();// 要注意Row计数
        for (int iRow = firstRowNum; iRow <= lastRowNum && flag; iRow++) { // 收集数据,遇到单元格字段名不为空则结束
            Row hssfRow = hssfSheet.getRow(iRow);
            if (hssfRow == null) {
                continue;
            }
            Map<String, Object> rowMap = new HashMap<String, Object>();
            for (int jCol : header.keySet()) {
                Cell hssfCell = hssfRow.getCell(jCol);
                if (hssfCell == null) {
                    continue;
                }
                String key = new CellReference(hssfCell).formatAsString(false);
                Object value = null;
                if (refName.get(key) != null/*
                 * &&
                 * (refName.get(key).startsWith(UFSTART
                 * ) ||
                 * refName.get(key).startsWith(UFEND
                 * ))
                 */) {
                    flag = false;
                    break; // 单元格存在字段名称则从表收集结束
                } else if ((value = getValue(hssfCell)) != null) {
                    // String lkey = header.get(hssfCell.getColumnIndex());
                    rowMap.put(header.get(jCol), value);
                }
            }

            // for (int jCol = firstColNum; jCol < header.size(); jCol++) {
            // HSSFCell hssfCell = hssfRow.getCell(jCol);
            // if (hssfCell == null) {
            // continue;
            // }
            // String key = new CellReference(hssfCell).formatAsString(false);
            // Object value = getValue(hssfCell);
            // if (refName.get(key) != null
            // && (refName.get(key).startsWith(UFSTART) ||
            // refName.get(key).startsWith(UFEND))) {
            // flag = false;
            // } else {
            // //String lkey = header.get(hssfCell.getColumnIndex());
            // rowMap.put(header.get(jCol), value);
            // }
            // }
            if (flag && rowMap.size() > 0) {/*
             * //for (String key :
             * rowMap.keySet()) { //if
             * (!objectString
             * (rowMap.get(key)).equals("")) {
             * rowMap
             * .put(EnumSystemField.uuid.getName
             * (), randomUuid());
             * listFields.add(rowMap); //break;
             * //} //}
             */
            }
        }
        return listFields;
    }

    public static boolean isUserform(String refName) {
        return !refName.startsWith(UFEND) && (refName.startsWith(UFSTART_UF) || refName.startsWith(UFSTART_USERFORM));
    }

    public static boolean endRow(Row hssfRow) {

        return false;
    }

    public static String objectString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    public static String randomUuid() {
        String oUuid = UUID.randomUUID().toString();
        return oUuid.replaceAll("-", "");
    }

    public void setHssfWorkbook(Workbook hssfWorkbook) {
        this.hssfWorkbook = hssfWorkbook;
    }

    public void setConfig(JsonConfig config) {
        this.config = config;
    }

    public Map<String, List<Map<String, Object>>> getFormDatas() {
        return formDatas;
    }

    public void setFormDatas(Map<String, List<Map<String, Object>>> formDatas) {
        this.formDatas = formDatas;
    }

    public Map<String, List<String>> getDeletedFormDatas() {
        return deletedFormDatas;
    }

    public void setDeletedFormDatas(Map<String, List<String>> deletedFormDatas) {
        this.deletedFormDatas = deletedFormDatas;
    }

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public JSONObject parse() {
        // 默认第一个工作表
        Sheet hssfSheet = hssfWorkbook.getSheetAt(0);
        // 获取工作表名称
        String shellName = hssfSheet.getSheetName();
        return parse(shellName);
    }

    public JSONObject parse(int sheetIndex) {
        if (sheetIndex >= hssfWorkbook.getNumberOfSheets()) {
            throw new IllegalArgumentException("parameter[sheetIndex] out of Array");
        }
        // 默认第一个工作表
        Sheet hssfSheet = hssfWorkbook.getSheetAt(sheetIndex);
        // 获取工作表名称
        String shellName = hssfSheet.getSheetName();
        return parse(shellName);
    }

    public JSONObject parse(String sheetName) {
        Assert.notNull(sheetName, "parameter[sheetName] is null");
        setFormUuid(sheetName);
        List<Map<String, Object>> mainFormDatasList = new ArrayList<Map<String, Object>>();
        Map<String, String> refName = new HashMap<String, String>();// refName:单元格地址的字段名称
        Map<String, String> nameRef = new HashMap<String, String>();// nameRef:定义从表对应的单元格地址,uf_开头识别为从表
        for (int i = 0; i < hssfWorkbook.getNumberOfNames(); i++) {// 收集sheetName中字段定义及从表定义（Name在Workbook中唯一）
            Name hssfName = hssfWorkbook.getNameAt(i);
            if (hssfName == null) {
                continue;
            }
            String name = hssfName.getRefersToFormula(); // 获取名称的单元格全局地址
            String[] refs = name.split("\\$");// SheetName!$ColName$RowName :
            // Sheet1!$F$2
            String localSheetName = refs[0].substring(0, refs[0].length() - 1);
            if (sheetName.equalsIgnoreCase(localSheetName)) {
                refName.put(refs[1] + refs[2], hssfName.getNameName());
                if (isUserform(hssfName.getNameName())/*
                 * hssfName.getNameName().
                 * startsWith(UFSTART_UF)
                 */) {
                    nameRef.put(hssfName.getNameName(), refs[1] + refs[2]);
                }
            }
        }
        // 第一个工作表
        Sheet hssfSheet = hssfWorkbook.getSheet(sheetName);
        if (hssfSheet == null) {
            hssfSheet = hssfWorkbook.createSheet(sheetName);
        }
        int firstRowNum = hssfSheet.getFirstRowNum();
        int lastRowNum = hssfSheet.getLastRowNum() + 1;// 要注意Row计数
        Map<String, Object> refValue = new HashMap<String, Object>();
        for (int iRow = firstRowNum; iRow <= lastRowNum; iRow++) { // refValue:收集单元格数据
            Row hssfRow = hssfSheet.getRow(iRow);
            if (hssfRow == null) {
                continue;
            }
            short firstColNum = hssfRow.getFirstCellNum();
            short lastColNum = hssfRow.getLastCellNum();
            for (int jCol = firstColNum; jCol < lastColNum; jCol++) {
                Cell hssfCell = hssfRow.getCell(jCol);
                if (hssfCell == null) {
                    continue;
                }
                String key = new CellReference(hssfCell).formatAsString(false);
                /*
                 * refName.get(key).startsWith(UFSTART_UF) &&
                 * !refName.get(key).startsWith(UFEND)
                 */
                if (refName.get(key) != null && isUserform(refName.get(key))) { // 获取从表数据
                    formDatas.put(refName.get(key), getListField(hssfSheet, refName, iRow + 1, refName.get(key)));
                    deletedFormDatas.put(refName.get(key), new ArrayList<String>());
                }
                Object value = getValue(hssfCell);// 不会返回空
                if (refName.get(key) != null && value != null) {// 过滤空值，提高效率（这里多一个判断,后面少一些存储和迭代）
                    refValue.put(key, value);// 有名称的都记录,没名称的不为空则记录
                }
            }
        }
        Map<String, Object> nameValue = new HashMap<String, Object>();
        for (String key : refValue.keySet()) { // 把单元格地址替换为名称
            // if (refName.get(key) != null) {
            nameValue.put(refName.get(key), refValue.get(key));
            // } else {
            // nameValue.put(key, refValue.get(key));// 是否收集其他未命名信息
            // }
        }
        // nameValue.put(EnumSystemField.uuid.getName(), randomUuid());
        mainFormDatasList.add(nameValue);
        formDatas.put(sheetName, mainFormDatasList);
        return config == null ? JSONObject.fromObject(this) : JSONObject.fromObject(this, config);
    }

}
