/*
 * @(#)2016年7月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年7月1日.1	zhulh		2016年7月1日		Create
 * </pre>
 * @date 2016年7月1日
 */
public class DataDependencySyncInfo {

    private String dataType;

    private String tableName;

    /**
     * 如何描述该构造方法
     *
     * @param dataType
     * @param tableName
     */
    public DataDependencySyncInfo(String dataType, String tableName) {
        super();
        this.dataType = dataType;
        this.tableName = tableName;
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

}
