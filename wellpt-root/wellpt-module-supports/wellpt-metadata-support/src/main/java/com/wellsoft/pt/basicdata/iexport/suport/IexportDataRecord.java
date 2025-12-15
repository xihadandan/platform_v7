/*
 * @(#)2019年4月7日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.reflection.ConvertUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年4月7日.1	zhulh		2019年4月7日		Create
 * </pre>
 * @date 2019年4月7日
 */
public class IexportDataRecord extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -769632033340875452L;

    // 表名
    private String tableName;

    private String primaryKeyName;

    // private String primaryKeyValue;

    // 记录的数据字段
    private List<IexportDataColumn> dataColumns = Lists.newArrayList();

    // 记录的数据字段MAP
    private transient Map<String, IexportDataColumn> dataColumnMap = null;

    // 记录的数据
    private transient Map<String, Object> data = null;

    // 关联的数据，<表名，表数据记录>
    private Map<String, List<IexportDataRecord>> relationalDataMap = Maps.newHashMap();

    // 元数据
    private IexportMetaData metaData;

    // 表关系
    private TableRelation tableRelation;

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName 要设置的tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the primaryKeyName
     */
    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    /**
     * @param primaryKeyName 要设置的primaryKeyName
     */
    public void setPrimaryKeyName(String primaryKeyName) {
        this.primaryKeyName = primaryKeyName;
    }

    /**
     * @return the primaryKeyValue
     */
    public String getPrimaryKeyValue() {
        StringBuilder sb = new StringBuilder();
        List<String> primaryKeyColumnNames = Arrays.asList(StringUtils.split(primaryKeyName,
                Separator.SEMICOLON.getValue()));
        Iterator<String> it = primaryKeyColumnNames.iterator();
        while (it.hasNext()) {
            String columnName = (String) it.next();
            IexportDataColumn dataColumn = getDataColumn(columnName);
            sb.append(ObjectUtils.toString(dataColumn.getValue(), StringUtils.EMPTY));
            if (it.hasNext()) {
                sb.append(Separator.SEMICOLON.getValue());
            }
        }
        return sb.toString();
    }

    //	/**
    //	 * @param primaryKeyValue 要设置的primaryKeyValue
    //	 */
    //	public void setPrimaryKeyValue(String primaryKeyValue) {
    //		this.primaryKeyValue = primaryKeyValue;
    //	}

    /**
     * @return the dataColumns
     */
    public List<IexportDataColumn> getDataColumns() {
        return dataColumns;
    }

    /**
     * @param dataColumns 要设置的dataColumns
     */
    public void setDataColumns(List<IexportDataColumn> dataColumns) {
        this.dataColumns = dataColumns;
    }

    /**
     * @return the data
     */
    public Map<String, Object> getData() {
        if (data == null) {
            data = Maps.newHashMap();
            for (IexportDataColumn dataColumn : dataColumns) {
                data.put(dataColumn.getName(), dataColumn.getValue());
            }
        }
        return data;
    }

    /**
     * @param data 要设置的data
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /**
     * @return the relationalDataMap
     */
    public Map<String, List<IexportDataRecord>> getRelationalDataMap() {
        return relationalDataMap;
    }

    /**
     * @param relationalDataMap 要设置的relationalDataMap
     */
    public void setRelationalDataMap(Map<String, List<IexportDataRecord>> relationalDataMap) {
        this.relationalDataMap = relationalDataMap;
    }

    /**
     * @return the metaData
     */
    public IexportMetaData getMetaData() {
        return metaData;
    }

    /**
     * @param metaData 要设置的metaData
     */
    public void setMetaData(IexportMetaData metaData) {
        this.metaData = metaData;
    }

    /**
     * @return the tableRelation
     */
    public TableRelation getTableRelation() {
        return tableRelation;
    }

    /**
     * @param tableRelation 要设置的tableRelation
     */
    public void setTableRelation(TableRelation tableRelation) {
        this.tableRelation = tableRelation;
    }

    /**
     * @param columnName
     * @return
     */
    public String getString(String columnName) {
        return ObjectUtils.toString(getDataColumn(columnName).getValue());
    }

    /**
     * @param columnName
     * @return
     */
    public Integer getInt(String columnName) {
        Object object = getDataColumn(columnName).getValue();
        if (object instanceof BigDecimal) {
            return ((BigDecimal) object).intValue();
        }
        return (Integer) object;
    }

    /**
     * @param columnName
     * @return
     */
    public Long getLong(String columnName) {
        Object object = getDataColumn(columnName).getValue();
        if (object instanceof BigDecimal) {
            return ((BigDecimal) object).longValue();
        }
        return (Long) object;
    }

    /**
     * @param columnName
     * @return
     */
    public IexportDataColumn getDataColumn(String columnName) {
        if (dataColumnMap == null) {
            dataColumnMap = ConvertUtils.convertElementToMap(dataColumns, "name");
        }
        return dataColumnMap.get(StringUtils.lowerCase(columnName));
    }

}
