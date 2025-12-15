/*
 * @(#)2016年10月20日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月20日.1	xiem		2016年10月20日		Create
 * </pre>
 * @date 2016年10月20日
 */
@ApiModel("数据仓库列实体")
public class CdDataStoreColumnBean {

    @ApiModelProperty("列名")
    private String columnName;

    @ApiModelProperty("列索引")
    private String columnIndex;
    @ApiModelProperty("标题")
    private String title;
    @ApiModelProperty("数据类型")
    private String dataType;

    @ApiModelProperty("列类型")
    private String columnType;

    @ApiModelProperty("是否导出")
    private Boolean isExport;

    private Boolean hidden;

    /**
     *
     */
    public CdDataStoreColumnBean() {
        super();
    }

    public CdDataStoreColumnBean(String title, String columnIndex, String columnName, String dataType, String columnType) {
        this.title = title;
        this.columnIndex = columnIndex;
        this.columnName = columnName;
        this.dataType = dataType;
        this.columnType = columnType;
        this.isExport = true;
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

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }
}
