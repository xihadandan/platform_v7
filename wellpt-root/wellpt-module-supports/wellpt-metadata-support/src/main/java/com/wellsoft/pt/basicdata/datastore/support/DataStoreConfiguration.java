/*
 * @(#)2017年4月27日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.jpa.criteria.DataType;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.HashMap;
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
 * 2017年4月27日.1	zhulh		2017年4月27日		Create
 * </pre>
 * @date 2017年4月27日
 */
public class DataStoreConfiguration extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5416407257215423690L;

    private String name;

    private String id;

    private String code;

    private String type;

    private String tableName;

    private String viewName;

    private String entityName;

    private String sqlStatement;

    private String dataInterfaceName;

    private String dataInterfaceParam;

    private String sqlName;

    private String defaultCondition;

    private String defaultOrder;

    private String columnsDefinition;

    private Long dbLinkConfUuid;

    private Map<String, DataStoreColumn> columnMap = new HashMap<String, DataStoreColumn>();

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
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
     * @return the viewName
     */
    public String getViewName() {
        return viewName;
    }

    /**
     * @param viewName 要设置的viewName
     */
    public void setViewName(String viewName) {
        this.viewName = viewName;
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
     * @return the sqlStatement
     */
    public String getSqlStatement() {
        return sqlStatement;
    }

    /**
     * @param sqlStatement 要设置的sqlStatement
     */
    public void setSqlStatement(String sqlStatement) {
        this.sqlStatement = sqlStatement;
    }

    /**
     * @return the dataInterfaceName
     */
    public String getDataInterfaceName() {
        return dataInterfaceName;
    }

    /**
     * @param dataInterfaceName 要设置的dataInterfaceName
     */
    public void setDataInterfaceName(String dataInterfaceName) {
        this.dataInterfaceName = dataInterfaceName;
    }

    /**
     * @return the sqlName
     */
    public String getSqlName() {
        return sqlName;
    }

    /**
     * @param sqlName 要设置的sqlName
     */
    public void setSqlName(String sqlName) {
        this.sqlName = sqlName;
    }

    /**
     * @return the defaultCondition
     */
    public String getDefaultCondition() {
        return defaultCondition;
    }

    /**
     * @param defaultCondition 要设置的defaultCondition
     */
    public void setDefaultCondition(String defaultCondition) {
        this.defaultCondition = defaultCondition;
    }

    /**
     * @return the defaultOrder
     */
    public String getDefaultOrder() {
        return defaultOrder;
    }

    /**
     * @param defaultOrder 要设置的defaultOrder
     */
    public void setDefaultOrder(String defaultOrder) {
        this.defaultOrder = defaultOrder;
    }

    /**
     * @return the columnsDefinition
     */
    public String getColumnsDefinition() {
        return columnsDefinition;
    }

    /**
     * @param columnsDefinition 要设置的columnsDefinition
     */
    public void setColumnsDefinition(String columnsDefinition) {
        this.columnsDefinition = columnsDefinition;
    }

    @SuppressWarnings("unchecked")
    public Map<String, DataStoreColumn> getColumnMap() {
        if (!columnMap.isEmpty()) {
            return columnMap;
        }
        Collection<DataStoreColumn> dataStoreColumns = JsonUtils.toCollection(columnsDefinition, DataStoreColumn.class, true);
//        List<DataStoreColumn> dataStoreColumns = JSONArray.toList(JSONArray.fromObject(columnsDefinition),
//                DataStoreColumn.class);
        for (DataStoreColumn dataStoreColumn : dataStoreColumns) {
            columnMap.put(dataStoreColumn.getColumnIndex(), dataStoreColumn);
        }
        return columnMap;
    }

    /**
     * @param columnMap 要设置的columnMap
     */
    public void setColumnMap(Map<String, DataStoreColumn> columnMap) {
        this.columnMap = columnMap;
    }

    /**
     * @param columnIndex
     * @return
     */
    public DataType getColumnDataType(String columnIndex) {
        String key = columnIndex;
        if (StringUtils.endsWith(key, "RenderValue")) {
            key = StringUtils.substring(key, 0, key.length() - "RenderValue".length());
        }
        DataStoreColumn dataStoreColumn = getColumnMap().get(key);
        return DataType.getByType(dataStoreColumn.getDataType());
    }

    /**
     * @return the dataInterfaceParam
     */
    public String getDataInterfaceParam() {
        return dataInterfaceParam;
    }

    /**
     * @param dataInterfaceParam 要设置的dataInterfaceParam
     */
    public void setDataInterfaceParam(String dataInterfaceParam) {
        this.dataInterfaceParam = dataInterfaceParam;
    }

    public Long getDbLinkConfUuid() {
        return dbLinkConfUuid;
    }

    public void setDbLinkConfUuid(Long dbLinkConfUuid) {
        this.dbLinkConfUuid = dbLinkConfUuid;
    }
}
