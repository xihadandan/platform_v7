/*
 * @(#)2017年4月27日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support;

import com.wellsoft.context.base.BaseObject;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年4月27日.1	zhulh		2017年4月27日		Create
 * </pre>
 * @date 2017年4月27日
 */
public class DataStoreColumn extends BaseObject {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1702545258983840878L;

    private String columnName;

    private String columnIndex;

    private String title;

    private String dataType;

    private String columnType;

    private Boolean isExport;

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
     * @return the columnIndex
     */
    public String getColumnIndex() {
        return columnIndex;
    }

    /**
     * @param columnIndex 要设置的columnIndex
     */
    public void setColumnIndex(String columnIndex) {
        this.columnIndex = columnIndex;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param dataType 要设置的dataType
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the columnType
     */
    public String getColumnType() {
        return columnType;
    }

    /**
     * @param columnType 要设置的columnType
     */
    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    /**
     * @return the isExport
     * TODO Boolean字段命名去掉is
     */
//	public Boolean getIsExport() {
//		return isExport;
//	}

    /**
     * @return the isExport
     */
    public Boolean isIsExport() {
        return isExport;
    }

    /**
     * @param isExport 要设置的isExport
     */
    public void setIsExport(Boolean isExport) {
        this.isExport = isExport;
    }

}
