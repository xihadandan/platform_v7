/*
 * @(#)2019年4月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.basicdata.iexport.suport.IexportMetaData.ColumnMetaData;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年4月12日.1	zhulh		2019年4月12日		Create
 * </pre>
 * @date 2019年4月12日
 */
public class IexportTableMetaData extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4328602259652869336L;

    private String tableName;
    private List<ColumnMetaData> columnMetaDatas;

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
     * @return the columnMetaDatas
     */
    public List<ColumnMetaData> getColumnMetaDatas() {
        return columnMetaDatas;
    }

    /**
     * @param columnMetaDatas 要设置的columnMetaDatas
     */
    public void setColumnMetaDatas(List<ColumnMetaData> columnMetaDatas) {
        this.columnMetaDatas = columnMetaDatas;
    }

    /**
     * @param columnName
     * @return
     */
    public boolean isPrimaryKey(String columnName) {
        return StringUtils.equalsIgnoreCase(TableMetaData.getPk(tableName), columnName);
    }

}
