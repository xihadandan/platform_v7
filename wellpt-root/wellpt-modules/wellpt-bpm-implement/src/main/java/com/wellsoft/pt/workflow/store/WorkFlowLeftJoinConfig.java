/*
 * @(#)5/14/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.store;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreColumn;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

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
 * 5/14/24.1	zhulh		5/14/24		Create
 * </pre>
 * @date 5/14/24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkFlowLeftJoinConfig extends BaseObject {
    private static final long serialVersionUID = 5092573487167184873L;

    // 是否启用
    private boolean enabled;

    // 表名
    private String tableName;

    // 表别名
    private String tableAlias;

    // 表查询SQL
    private String tableSql;

    // 查询列
    private List<String> selection;

    // 查询信息
    private List<DataStoreColumn> selectionColumns;

    // 关联条件
    private String onConditionSql;

    // 查询参数
    private Map<String, Object> sqlParameter;

    /**
     * @return the enabled
     */
    public boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled 要设置的enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    /**
     * @return the tableAlias
     */
    public String getTableAlias() {
        return tableAlias;
    }

    /**
     * @param tableAlias 要设置的tableAlias
     */
    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    /**
     * @return the tableSql
     */
    public String getTableSql() {
        return tableSql;
    }

    /**
     * @param tableSql 要设置的tableSql
     */
    public void setTableSql(String tableSql) {
        this.tableSql = tableSql;
    }

    /**
     * @return the selection
     */
    public List<String> getSelection() {
        return selection;
    }

    /**
     * @param selection 要设置的selection
     */
    public void setSelection(List<String> selection) {
        this.selection = selection;
    }

    /**
     * @return the selectionColumns
     */
    public List<DataStoreColumn> getSelectionColumns() {
        return selectionColumns;
    }

    /**
     * @param selectionColumns 要设置的selectionColumns
     */
    public void setSelectionColumns(List<DataStoreColumn> selectionColumns) {
        this.selectionColumns = selectionColumns;
    }

    /**
     * @return the onConditionSql
     */
    public String getOnConditionSql() {
        return onConditionSql;
    }

    /**
     * @param onConditionSql 要设置的onConditionSql
     */
    public void setOnConditionSql(String onConditionSql) {
        this.onConditionSql = onConditionSql;
    }

    /**
     * @return the sqlParameter
     */
    public Map<String, Object> getSqlParameter() {
        return sqlParameter;
    }

    /**
     * @param sqlParameter 要设置的sqlParameter
     */
    public void setSqlParameter(Map<String, Object> sqlParameter) {
        this.sqlParameter = sqlParameter;
    }
}
