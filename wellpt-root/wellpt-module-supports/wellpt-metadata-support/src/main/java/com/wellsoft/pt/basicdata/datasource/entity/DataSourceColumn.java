/*
 * @(#)2014-7-31 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Description: 数据源的列
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-7-31.1	wubin		2014-7-31		Create
 * </pre>
 * @date 2014-7-31
 */
@Entity
@Table(name = "data_source_column")
@DynamicUpdate
@DynamicInsert
public class DataSourceColumn extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3515682359295085633L;
    //显示标题
    private String titleName;
    //数据列名
    private String columnName;
    //列数据类型
    private String columnDataType;
    //列所属的实体类
    private String entityName;
    //数据列的字段名(存数据库的名字)
    private String fieldName;
    //列别名
    private String columnAliase;
    //是否输出列
    private String isExport;
    //默认排序
    private String defaultSort;
    //所属的数据源
    @UnCloneable
    private DataSourceDefinition dataSourceDefinition;

    public Object getColumnDataType(String data) {
        if (columnDataType == null || "".equals(columnDataType)) {
            return data;
        }
        Object value = null;
        if (columnDataType.equals("STRING")) {
            value = data;
        } else if (columnDataType.equals("CLOB")) {
            value = data;
        } else if (columnDataType.equals("INTEGER")) {
            value = Integer.valueOf(data);
        } else if (columnDataType.equals("LONG")) {
            value = Long.valueOf(data);
        } else if (columnDataType.equals("DOUBLE")) {
            value = Double.valueOf(data);
        } else if (columnDataType.equals("DATE")) {
            SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                value = sim.parse(data);
            } catch (ParseException e) {
                return value = "0000-00-00 00:00:00";
            }
        } else if (columnDataType.equals("BOOLEAN")) {
            value = Boolean.valueOf(data);
        }
        return value;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName 要设置的fieldName
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return the titleName
     */
    public String getTitleName() {
        return titleName;
    }

    /**
     * @param titleName 要设置的titleName
     */
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    /**
     * @return the columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @param columnName 要设置的columnName
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @return the columnDataType
     */
    public String getColumnDataType() {
        return columnDataType;
    }

    /**
     * @param columnDataType 要设置的columnDataType
     */
    public void setColumnDataType(String columnDataType) {
        this.columnDataType = columnDataType;
    }

    /**
     * @return the isExport
     */
    public String getIsExport() {
        return isExport;
    }

    /**
     * @param isExport 要设置的isExport
     */
    public void setIsExport(String isExport) {
        this.isExport = isExport;
    }

    /**
     * @return the defaultSort
     */
    public String getDefaultSort() {
        return defaultSort;
    }

    /**
     * @param defaultSort 要设置的defaultSort
     */
    public void setDefaultSort(String defaultSort) {
        this.defaultSort = defaultSort;
    }

    /**
     * @return the entityName
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @param entityName 要设置的entityName
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * @return the columnAliase
     */
    public String getColumnAliase() {
        return columnAliase;
    }

    /**
     * @param columnAliase 要设置的columnAliase
     */
    public void setColumnAliase(String columnAliase) {
        this.columnAliase = columnAliase;
    }

    /**
     * @return the dataSourceDefinition
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_def_uuid", nullable = false)
    public DataSourceDefinition getDataSourceDefinition() {
        return dataSourceDefinition;
    }

    /**
     * @param dataSourceDefinition 要设置的dataSourceDefinition
     */
    public void setDataSourceDefinition(DataSourceDefinition dataSourceDefinition) {
        this.dataSourceDefinition = dataSourceDefinition;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
        return result;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        DataSourceColumn other = (DataSourceColumn) obj;
        if (fieldName == null) {
            if (other.fieldName != null)
                return false;
        } else if (!fieldName.equals(other.fieldName))
            return false;
        return true;
    }

}
