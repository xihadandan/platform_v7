/*
 * @(#)2013-4-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.dyview.provider;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-17.1	wubin		2013-4-17		Create
 * </pre>
 * @date 2013-4-17
 */
public class ViewColumn {

    private String attributeName;//属性名

    private String columnAlias;//列查询别名

    private String columnName;//列显示名

    private ViewColumnType columnType;//列类型

    private boolean isQueryField = true;

    public Object getColumnValue(String data) {
        if (columnType == null) {
            return data;
        }
        Object value = null;
        switch (columnType) {
            case STRING:
                value = data;
                break;

            case INTEGER:
                value = Integer.valueOf(data);
                break;
            case LONG:
                value = Long.valueOf(data);
                break;
            case DOUBLE:
                value = Double.valueOf(data);
                break;
            case DATE:
                SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                try {
                    value = sim.parse(data);
                } catch (ParseException e) {
                    return value = "0000-00-00 00:00:00";
                }
                break;
            case BOOLEAN:
                value = Boolean.valueOf(data);
                break;
        }

        return value;
    }

    /**
     * @return the columnType
     */
    public ViewColumnType getColumnType() {
        return columnType;
    }

    /**
     * @param columnType 要设置的columnType
     */
    public void setColumnType(ViewColumnType columnType) {
        this.columnType = columnType;
    }

    /**
     * @return the attributeName
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * @param attributeName 要设置的attributeName
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * @return the columnAlias
     */
    public String getColumnAlias() {
        return columnAlias;
    }

    /**
     * @param columnAlias 要设置的columnAlias
     */
    public void setColumnAlias(String columnAlias) {
        this.columnAlias = columnAlias;
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
     * @return the isQueryField
     */
    public boolean isQueryField() {
        return isQueryField;
    }

    /**
     * @param isQueryField 要设置的isQueryField
     */
    public void setQueryField(boolean isQueryField) {
        this.isQueryField = isQueryField;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((columnAlias == null) ? 0 : columnAlias.hashCode());
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ViewColumn other = (ViewColumn) obj;
        if (columnAlias == null) {
            if (other.columnAlias != null)
                return false;
        } else if (!columnAlias.equals(other.columnAlias))
            return false;
        return true;
    }

}
