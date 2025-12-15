package com.wellsoft.pt.basicdata.excelexporttemplate.support;

import com.wellsoft.context.enums.Separator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author FashionSUN
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-19.1	FashionSUN		2014-10-19		Create
 * </pre>
 * @date 2014-10-19
 */
public class Json2CelEngine {
    public static final String FORM_UUID = "formUuid";
    public static final String FORM_DATAS = "formDatas";
    public static final String DELETED_FORM_DATAS = "deletedFormDatas";
    private static final String UFSTART = "uf_";
    private static final String SEQNO = "seqNO";
    private static final String UFEND = "uf_end";
    private static final String appDataDir = System.getProperty("java.io.tmpdir") + File.separator;
    protected Logger logger = LoggerFactory.getLogger(Json2CelEngine.class);
    private Map<Class<?>, ClazzProcessor> clazzProcess = new HashMap<Class<?>, ClazzProcessor>();
    private Map<String, NameProcessor> nameProcess = new HashMap<String, NameProcessor>();
    private Map<String, String> refName = new HashMap<String, String>();// 初始化refName
    private Workbook hssfWorkbook = null;

    // private JsonConfig config = null;

    public Json2CelEngine(InputStream inputStream) {
        try {
            // default Office2007
            this.hssfWorkbook = new XSSFWorkbook(inputStream); // 解析excel
        } catch (IOException ex) {
            logger.error("ExcelParse Constructor Exception:" + ex.getMessage(), ex);
            throw new RuntimeException("ExcelParse Constructor Exception:" + ex.getMessage(), ex);
        }
    }

    public Json2CelEngine(Workbook hssfWorkbook) {
        this.hssfWorkbook = hssfWorkbook;
    }

    public static String convertNumToColString(int col) {
        int excelColNum = col + 1;
        String colRef = "";
        int colRemain = excelColNum;
        while (colRemain > 0) {
            int thisPart = colRemain % 26;
            if (thisPart == 0) {
                thisPart = 26;
            }
            colRemain = (colRemain - thisPart) / 26;

            char colChar = (char) (thisPart + 64);
            colRef = colChar + colRef;
        }
        return colRef;
    }

    public static int convertColStringToIndex(String ref) {
        int pos = 0;
        int retval = 0;
        for (int k = ref.length() - 1; k >= 0; --k) {
            char thechar = ref.charAt(k);
            if (thechar == '$') {
                if (k == 0) {
                    break;
                }
                throw new IllegalArgumentException("Bad col ref format '" + ref + "'");
            }
            int shift = (int) Math.pow(26.0D, pos);
            retval += (Character.getNumericValue(thechar) - 9) * shift;
            ++pos;
        }
        return (retval - 1);
    }

    public static int convertRowStringToIndex(String ref) {
        if (!StringUtils.isNumeric(ref)) {
            throw new IllegalArgumentException("Bad row ref format '" + ref + "'");
        }
        return Integer.valueOf(ref);
    }

    /**
     * 获取单元格，不存在则创建
     *
     * @param hssfSheet
     * @param rowNum    (注意rowNum的计数)
     * @param colNum
     * @return
     */
    public static Cell getHSSFCell(Sheet hssfSheet, int rowNum, int colNum) {
        Assert.notNull(hssfSheet, "parameter[hssfSheet] is null");
        Row hssfRow = hssfSheet.getRow(rowNum);
        if (hssfRow == null) {
            hssfRow = hssfSheet.createRow(rowNum);
        }
        Cell hssfCell = hssfRow.getCell(colNum);
        if (hssfCell == null) {
            hssfCell = hssfRow.createCell(colNum);
        }
        return hssfCell;
    }

    public static Row getColHeader(Sheet hssfSheet, int row) {
        Row headerRow = hssfSheet.getRow(row/* + 1*/);
        if (headerRow == null) {
            return hssfSheet.createRow(row/* + 1*/);
        }
        return headerRow;
    }

    /**
     * insert row into the target sheet, the style of cell is the same as
     * startRow
     *
     * @param wb
     * @param sheet
     * @param starRow - the row to start shifting
     * @param rows
     */
    public static List<Row> insertRows(Sheet sheet, int startRow, int rows) {
        List<Row> targetRows = new ArrayList<Row>();
        // modify by wujx 2016-01-27 begin
        sheet.shiftRows(startRow + 1, sheet.getLastRowNum() + 1, rows, true, false);
        // modify by wujx 2016-01-27 begin
        // Parameters:
        // startRow - the row to start shifting
        // endRow - the row to end shifting
        // n - the number of rows to shift
        // copyRowHeight - whether to copy the row height during the shift
        // resetOriginalRowHeight - whether to set the original row's height to
        // the default

        for (int i = 0; i < rows; i++) {
            Row sourceRow = null;
            Row targetRow = null;
            sourceRow = sheet.getRow(startRow);
            targetRow = sheet.createRow(++startRow);
            Util.copyRow(sheet, sourceRow, targetRow);
            targetRows.add(targetRow);
        }
        return targetRows;
    }

    /**
     * 从startRow开始插入1行
     *
     * @param hssfSheet
     * @param starRow
     * @return
     */
    public static Row insertRow(Sheet hssfSheet, int starRow) {
        return insertRows(hssfSheet, starRow, 1).get(0);
    }

    /**
     * 判断hssfRow是否最后一行
     *
     * @param hssfRow
     * @param refName
     * @return
     */
    public static boolean endRow(Row hssfRow, Map<String, String> refName) {
        if (hssfRow == null) {
            return true;
        }
        int firstColNum = hssfRow.getFirstCellNum();
        int lastColNum = hssfRow.getLastCellNum();
        if (lastColNum < 0 || firstColNum >= lastColNum) {
            return true;
        }
        for (int jCol = firstColNum; jCol < lastColNum; jCol++) {
            Cell hssfCell = hssfRow.getCell(jCol);
            String key = new CellReference(hssfCell).formatAsString(false);
            if (refName.get(key) != null /*&& refName.get(key).startsWith(UFSTART)*/) {
                return true;// 行单元格存在字段名则结束
            }
        }
        return false;
    }

    public static Object getCellValue(Cell hssfCell) {
        switch (hssfCell.getCellType().getCode()) {
            case 3:
                return "";
            case 4:
                return hssfCell.getBooleanCellValue();
            case 5:
                return hssfCell.getErrorCellValue();
            case 2:
                return hssfCell.getCellFormula();
            case 0:
                if (DateUtil.isCellDateFormatted(hssfCell)) {
                    return hssfCell.getDateCellValue();
                }
                return hssfCell.getNumericCellValue();
            case 1:
                return hssfCell.getStringCellValue();
        }
        return "Unknown Cell Type: " + hssfCell.getCellType();
    }

    /**
     * 根据单元格格式要求设置单元格的值
     *
     * @param hssfCell
     * @param value
     */
    public static void setValue(HSSFCell hssfCell, Object paramValue) {
        Assert.notNull(hssfCell, "parameter[hssfCell] is null");
        Object value = paramValue;
        if (JSONUtils.isNull(value)) {
            hssfCell.setCellValue("");
        } else if (JSONUtils.isBoolean(value)) {
            hssfCell.setCellValue((java.lang.Boolean) value);
        } else if (isDate(value)) {
            hssfCell.setCellValue((java.util.Date) value);
        } else if (JSONUtils.isNumber(value)) {
            Number localValue = (Number) value;
            hssfCell.setCellValue((java.lang.Double) localValue);
        } else {
            hssfCell.setCellValue(objectString(value));
        }
    }

    public static String getFormUuid(JSONObject jsonObject) {
        return jsonObject.getString(FORM_UUID);// 自己会抛异常
    }

    public static JSONObject getFormDatas(JSONObject jsonObject) {
        return jsonObject.getJSONObject(FORM_DATAS);
    }

    public static JSONArray getMainFormDatas(JSONObject jsonObject) {
        return getFormDatas(jsonObject).getJSONArray(getFormUuid(jsonObject));
    }

    public static JSONObject getMainFormData(JSONObject jsonObject) {
        return getMainFormDatas(jsonObject).getJSONObject(0);// 主表第一条数据
    }

    /**
     * 不会返回空
     *
     * @param jsonObject
     * @return
     */
    public static List<String> getSubFormKeys(JSONObject jsonObject) {
        List<String> subFormKeys = new ArrayList<String>();
        for (Object keySet : getFormDatas(jsonObject).keySet()) {
            String key = String.valueOf(keySet);
            if (key.equalsIgnoreCase(getFormUuid(jsonObject))) {
                continue;
            }
            subFormKeys.add(key);
        }
        return subFormKeys;
    }

    public static JSONArray getSubFormDatas(JSONObject jsonObject, String key) {
        return getFormDatas(jsonObject).getJSONArray(key);
    }

    public static List<JSONArray> getSubForms(JSONObject jsonObject) {
        List<JSONArray> subForms = new ArrayList<JSONArray>();
        for (String key : getSubFormKeys(jsonObject)) {
            subForms.add(getSubFormDatas(jsonObject, key));
        }
        return subForms;
    }

    public static String objectString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    public static boolean isDate(Object obj) {
        if (((obj != null) && (obj instanceof java.util.Date)) || ((obj != null) && (obj instanceof java.sql.Date))
                || ((obj != null) && (obj instanceof java.sql.Time))
                || ((obj != null) && (obj instanceof java.sql.Timestamp))) {
            return true;
        }

        return false;
    }

    /**
     * 简单判断键值类的JSON
     *
     * @param obj
     * @return
     */
    public static boolean isJsonStr(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof String)) {
            return false;
        }
        return obj.toString().startsWith("\"{") && obj.toString().endsWith("}\"") && obj.toString().indexOf(":") > -1;
    }

    /**
     * 获取JSON值(可以不要，json{key,value}在前端通过collectFormDisplayData已处理)
     *
     * @param key
     * @param jsonObject
     * @return
     */
    public static Object getKeyValue(String key, JSONObject jsonObject) {
        String value = jsonObject.getString(key);
        if (isJsonStr(value)) {
            return StringUtils.join(jsonObject.getJSONObject(key).values(), Separator.COMMA.getValue());//String.valueOf(value).split("\"")[3];// xb : {"1":"男"}//值在第2个
        }
        return value;
    }

    public static String trimLeft(String string) {
        if (string == null) {
            return string;
        }
        return string.substring(0, string.length() - 1);
    }

    public static String trimRight(String string) {
        if (string == null) {
            return string;
        }
        return string.substring(1);
    }

    public static String trim(String string) {
        if (string == null) {
            return string;
        }
        return trimRight(trimLeft(string));
    }

    public static void main(String[] args) {
        Object obj = null;
        obj = null;
        // System.out.println(isJsonStr(obj));
        obj = new Date();
        // System.out.println(isJsonStr(obj));
        obj = "{:}";
        // System.out.println(isJsonStr(obj));
    }

    /**
     * 注册值类型处理类
     *
     * @param clazz     要处理的类型,Date
     * @param processor
     * @return
     */
    public ClazzProcessor registerClazzProcessor(Class<?> clazz, ClazzProcessor processor) {
        return clazzProcess.put(clazz, processor);
    }

    /**
     * 注销值类型处理类
     *
     * @param clazz 要处理的类型,Date
     * @return
     */
    public ClazzProcessor unregisterClazzProcessor(Class<?> clazz) {
        return clazzProcess.remove(clazz);
    }

    /**
     * 注册字段名的值处理类
     *
     * @param name
     * @param processor
     * @return
     */
    public NameProcessor registerNameProcessor(String name, NameProcessor processor) {
        return nameProcess.put(name, processor);
    }

    /**
     * 注销字段名的值处理类
     *
     * @param name
     * @return
     */
    public NameProcessor unregisterNameProcessor(String name) {
        return nameProcess.remove(name);
    }

    public File process(JSONObject jsonObject) {
        // 默认第一个工作表,获取工作表名称
        String sheetName = hssfWorkbook.getSheetAt(0).getSheetName();
        return process(sheetName, jsonObject);
    }

    public File process(int sheetIndex, JSONObject jsonObject) {
        if (sheetIndex >= hssfWorkbook.getNumberOfSheets() || sheetIndex < 0) {
            throw new IllegalArgumentException("parameter[sheetIndex] out of Array");
        }
        // 获取工作表名称
        String sheetName = hssfWorkbook.getSheetAt(sheetIndex).getSheetName();
        return process(sheetName, jsonObject);
    }

    public File process(String sheetName, JSONObject jsonObject) {
        Assert.notNull(sheetName, "parameter[sheetName] is null");
        Assert.notNull(jsonObject, "parameter[jsonObject] is null");
        Sheet hssfSheet = hssfWorkbook.getSheet(sheetName);
        if (hssfSheet == null) {
            throw new IllegalArgumentException("parameter[sheetName] not exist");
        }
        if (jsonObject == null || !jsonObject.has(FORM_UUID) || !jsonObject.has(FORM_DATAS)) {
            throw new IllegalArgumentException("parameter[jsonObject] not exist key[formUuid] or key[formDatas]");
        }
        JSONObject mainFormData = getMainFormData(jsonObject);// 根据formUuid获取主表数据
        for (Object iKey : mainFormData.keySet()) {
            String key = String.valueOf(iKey);
            Name hssfName = hssfWorkbook.getName(key);// 根据名称，获取HSSFName
            if (hssfName == null) {
                continue;// name对应的HSSFName不存在
            }
            // Object value = mainFormData.get(key);
            // Object value = nameProcess.get(key) == null ?
            // mainFormData.get(key) :
            // nameProcess.get(key).process(mainFormData.get(key));
            Object value = nameProcess.get(key) == null ? getKeyValue(key, mainFormData) : nameProcess.get(key)
                    .process(getKeyValue(key, mainFormData));// 名字处理器,(可以不要，json{key,value}在前端通过collectFormDisplayData已处理)
            if (StringUtils.isBlank((String) value)/*objectString(value).equals("")*/) {
                continue;// key对应没有值,则不设置
            }
            String name = hssfName.getRefersToFormula();
            String[] refs = name.split("\\$");// SheetName!$ColName$RowName :  Sheet1!$F$2
            String localSheetName = refs[0].substring(0, refs[0].length() - 1);
            if (!localSheetName.equalsIgnoreCase(sheetName)) {// 不在同一个表空间名称,忽略
                continue;
            }
            int colNum = convertColStringToIndex(refs[1]);
            int rowNum = convertRowStringToIndex(refs[2]);
            Cell hssfCell = getHSSFCell(hssfSheet, rowNum - 1, colNum);
            if (hssfCell == null) {// 不存在名称单元格则继续
                continue;
            }
            setCellValue(hssfCell, value);
        }

        for (int i = 0; i < hssfWorkbook.getNumberOfNames(); i++) {// HSSFName是HSSFWorkbook级的唯一
            Name hssfName = hssfWorkbook.getNameAt(i);
            if (hssfName == null) {
                continue;
            }
            String name = hssfName.getRefersToFormula();
            String[] refs = name.split("\\$");// SheetName!$ColName$RowName : Sheet1!$F$2
            String localSheetName = refs[0].substring(0, refs[0].length() - 1);
            if (sheetName.equalsIgnoreCase(localSheetName)) {
                refName.put(refs[1] + refs[2], hssfName.getNameName());
            }
        }

        for (String key : getSubFormKeys(jsonObject)) {
            JSONArray subFormLists = getSubFormDatas(jsonObject, key);
            if (subFormLists == null || subFormLists.size() == 0) {// List没有值
                continue;
            }
            Name hssfName = hssfWorkbook.getName(key);// 获取从表的单元格地址,根据名称，获取HSSFName
            if (hssfName == null) {
                continue;// name对应的HSSFName不存在
            }
            String name = hssfName.getRefersToFormula();
            String[] refs = name.split("\\$");// SheetName!$ColName$RowName : Sheet1!$F$2
            String localSheetName = refs[0].substring(0, refs[0].length() - 1);
            if (!localSheetName.equalsIgnoreCase(sheetName)) {// 不在同一个表空间名称,忽略
                continue;
            }
            int rowNum = convertRowStringToIndex(refs[2]);
            // int colNum = convertColStringToIndex(refs[1]);
            // Cell hssfCell = getHSSFCell(hssfSheet, rowNum - 1, colNum);// (标题单元格)一般都存在()
            Row headerRow = getColHeader(hssfSheet, rowNum);// 标题跳一行为表头
            for (int i = 0; i < subFormLists.size(); i++) {
                Row addRow = addHSSFRow(hssfSheet, refName, (rowNum + 1) + i);// 标题跳2行为BODY
                JSONObject subJSONObject = subFormLists.getJSONObject(i);
                setRowValue(headerRow, addRow, refName, subJSONObject, key);
            }
        }
        File file = new File(appDataDir + sheetName + (hssfWorkbook instanceof XSSFWorkbook ? ".xlsx" : ".xls"));
        try {
            if (file.exists() && file.delete()) {
            }
            file.createNewFile();
            // file.deleteOnExit(); //退出时删除
            this.hssfWorkbook.write(new FileOutputStream(file));
        } catch (IOException ex) {
            logger.error("hssfWorkbook write Exception : " + ex.getMessage(), ex);
        }
        return file;
    }

    private void updateRefName(String sheetName) {
        refName.clear();// 清空之前的RefName
        for (int i = 0; i < hssfWorkbook.getNumberOfNames(); i++) {// HSSFName是HSSFWorkbook级的唯一
            Name hssfName = hssfWorkbook.getNameAt(i);
            if (hssfName == null) {
                continue;
            }
            String name = hssfName.getRefersToFormula();
            String[] refs = name.split("\\$");// SheetName!$ColName$RowName :
            // Sheet1!$F$2
            String localSheetName = refs[0].substring(0, refs[0].length() - 1);
            if (sheetName.equalsIgnoreCase(localSheetName)) {// 主表已处理，只更新从表的refName
                refName.put(refs[1] + refs[2], hssfName.getNameName());
            }
        }
    }

    /**
     * 创建一行从hssfCell的rowIndex开始,如果表格Row不够,会自动创建
     *
     * @param hssfSheet
     * @param hssfCell  (第一次传头行,后面是插入行的上一行,建议除表头外，留一行空数据作为示例行)
     * @param refName
     * @return
     */
    public Row addHSSFRow(Sheet hssfSheet, Map<String, String> refName, int startRow) {
        Row hssfRow = hssfSheet.getRow(startRow /*+ 2*/);// 表格第一行()
        if (endRow(hssfRow, refName)) {// 判断是否结束行（当前行的任意一单元格存在字段名则为结束）
            hssfRow = insertRow(hssfSheet, startRow - 1/*+ 1*/);
            updateRefName(hssfSheet.getSheetName());// 插入了一行，refName就会改变，需要重新更新
        }
        return hssfRow;
    }

    private void setRowValue(Row headerRow, Row addRow, Map<String, String> refName, JSONObject jsonObject,
                             String headKey) {
        short firstColNum = headerRow.getFirstCellNum();
        short lastColNum = headerRow.getLastCellNum();
        for (int iCol = firstColNum; iCol < lastColNum; iCol++) {
            Cell headerCell = headerRow.getCell(iCol);
            String key = new CellReference(headerCell).formatAsString(false);
            // int rowIndex = headerCell.getRowIndex();
            // String colString = convertNumToColString(iCol);
            // String key = colString + rowIndex;
            String objKey = refName.get(key) == null ? objectString(getCellValue(headerCell)) : refName.get(key);
            if (StringUtils.isBlank(objKey)/*objKey == null || objKey.equals("")*/) {
                continue;
            } else if (objKey.endsWith(headKey)) {
                objKey = objKey.replace("_" + headKey, "");//objKey.substring(0, objKey.indexOf());// 从表处理
            }
            if (objKey.equals("序号")) {
                objKey = SEQNO;
            }
            if (!jsonObject.containsKey(objKey)) {// key有可能不是迭代的Key了
                continue;
            }
            // Object value = nameProcess.get(objKey) == null ?
            // jsonObject.get(objKey) :
            // nameProcess.get(objKey).process(headerRow, jsonObject,
            // jsonObject.get(objKey));
            Object value = nameProcess.get(objKey) == null ? getKeyValue(objKey, jsonObject) : nameProcess.get(objKey)
                    .process(headerRow, jsonObject, getKeyValue(objKey, jsonObject));// 可以处理name重名,//
            // 名字处理器,(可以不要，json{key,value}在前端通过collectFormDisplayData已处理)
            if (value == null) {
                continue;
            }
            Cell valueCell = addRow.getCell(iCol);
            if (valueCell == null) {
                continue;
            }
            setCellValue(valueCell, value);
        }
    }

    /**
     * 根据单元格格式要求设置单元格的值
     *
     * @param hssfCell
     * @param value
     */
    private void setCellValue(Cell hssfCell, Object paramValue) {
        Assert.notNull(hssfCell, "parameter[hssfCell] is null");
        Object value = clazzProcess.get(paramValue.getClass()) == null ? paramValue : clazzProcess.get(
                paramValue.getClass()).process(paramValue);// 类处理器在这里实现(可以用来处理Date等)
        if (JSONUtils.isNull(value)) {
            hssfCell.setCellValue("");
        } else if (JSONUtils.isBoolean(value)) {
            hssfCell.setCellValue((java.lang.Boolean) value);
        } else if (isDate(value)) {
            hssfCell.setCellValue((java.util.Date) value);
        } else if (JSONUtils.isNumber(value)) {
            Number localValue = (Number) value;
            hssfCell.setCellValue((java.lang.Double) localValue);
        } else {
            hssfCell.setCellValue(objectString(value).replaceAll("<br>", "\n"));// 处理特殊字符<br>
        }
    }
}
