package com.wellsoft.pt.jpa.criteria;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class CriteriaMetadata {
    private String[] columnIndexs = ArrayUtils.EMPTY_STRING_ARRAY;
    private String[] comments = ArrayUtils.EMPTY_STRING_ARRAY;
    private DataType[] dataTypes = new DataType[0];
    private String[] columnTypes = ArrayUtils.EMPTY_STRING_ARRAY;
    private Map<String, String> columnMap = new HashMap<String, String>();
    private int length;

    private CriteriaMetadata() {

    }

    public static CriteriaMetadata createMetadata() {
        return new CriteriaMetadata();
    }

    public String getMapColumnIndex(String columnIndex) {
        return columnMap.containsKey(columnIndex) ? columnMap.get(columnIndex) : columnIndex;
    }

    /**
     * 如何描述该方法
     *
     * @param columnIndex 列索引
     * @param comment
     * @param dataType
     */
    public void add(String columnIndex, String comment, DataType dataType) {
        Assert.hasText(columnIndex, "列索引不允许为空");
        Assert.notNull(dataType, "必须明确数据类型");
        if (ArrayUtils.contains(columnIndexs, columnIndex)) {
            throw new RuntimeException("列索引必须唯一");
        }
        length++;
        columnIndexs = (String[]) ArrayUtils.add(columnIndexs, columnIndex);
        comments = (String[]) ArrayUtils.add(comments, comment);
        dataTypes = (DataType[]) ArrayUtils.add(dataTypes, dataType);
    }

    /**
     * 当列名和列索引不同时，传入真实列名
     *
     * @param columnIndex 列索引
     * @param columnName  真实列名
     * @param comment     备注
     * @param dataType    数据类型
     */
    public void add(String columnIndex, String columnName, String comment, DataType dataType) {
        this.add(columnIndex, comment, dataType);
        columnMap.put(columnIndex, columnName);
    }

    public void add(String columnIndex, String columnName, String comment, DataType dataType, String columnType) {
        this.add(columnIndex, columnName, comment, dataType);
        columnTypes = (String[]) ArrayUtils.add(columnTypes, columnType);
    }

    public void add(String columnIndex, String comment, DataType dataType, String columnType) {
        this.add(columnIndex, comment, dataType);
        columnTypes = (String[]) ArrayUtils.add(columnTypes, columnType);
    }

    public void add(String columnIndex, String columnName, String comment, String dataType, String columnType) {
        this.add(columnIndex, columnName, comment, DataType.getCovert4DB(dataType), columnType);
    }

    public void add(String columnIndex, String columnName, String comment, String dataType) {
        this.add(columnIndex, columnName, comment, DataType.getCovert4DB(dataType), dataType);
    }

    public void add(String columnIndex, String columnName, String comment, Class<?> dataType) {
        this.add(columnIndex, columnName, comment, DataType.getCovert4JavaType(dataType));
    }

    public void add(String columnIndex, String comment, String dataType) {
        this.add(columnIndex, comment, DataType.getCovert4DB(dataType), dataType);
    }

    public void add(String columnIndex, String comment, Class<?> dataType) {
        this.add(columnIndex, comment, DataType.getCovert4JavaType(dataType));
    }

    public String[] getColumnIndexs() {
        return columnIndexs;
    }

    public String[] getComments() {
        return comments;
    }

    public DataType[] getDataTypes() {
        return dataTypes;
    }

    public String getColumnIndex(int index) {
        return columnIndexs[index];
    }

    public String getComment(int index) {
        return comments[index];
    }

    public DataType getDataType(int index) {
        return dataTypes[index];
    }

    public String getColumnType(int index) {
        if (columnTypes.length > index) {
            return columnTypes[index];
        }
        return dataTypes[index].getType();
    }

    public int length() {
        return length;
    }

    public boolean containsColumnIndex(String key) {
        return columnMap.containsKey(key);
    }
}
