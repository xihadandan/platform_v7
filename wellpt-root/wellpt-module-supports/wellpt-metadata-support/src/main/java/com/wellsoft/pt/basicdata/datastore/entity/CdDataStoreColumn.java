/*
 * @(#)2016年10月19日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月19日.1	xiem		2016年10月19日		Create
 * </pre>
 * @date 2016年10月19日
 */
@Entity
@Table(name = "cd_data_store_column")
@DynamicUpdate
@DynamicInsert
public class CdDataStoreColumn extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5026296420550665121L;

    private String columnName;

    private String columnIndex;

    private String title;

    private int dataType;

    private boolean isExport;
    @UnCloneable
    private CdDataStoreDefinition dataStoreDefinition;

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
     * @return the dataType
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * @param dataType 要设置的dataType
     */
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the dataSource
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DATA_SOURCE_UUID", nullable = false)
    public CdDataStoreDefinition getDataStoreDefinition() {
        return dataStoreDefinition;
    }

    /**
     * @param dataSourceDefinition 要设置的dataSourceDefinition
     */
    public void setDataStoreDefinition(CdDataStoreDefinition dataStoreDefinition) {
        this.dataStoreDefinition = dataStoreDefinition;
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
