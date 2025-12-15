/*
 * @(#)2016年10月19日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.entity;

import com.google.gson.reflect.TypeToken;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.datastore.bean.CdDataStoreColumnBean;
import io.swagger.annotations.ApiModelProperty;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

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
@Table(name = "cd_data_store_definition")
@DynamicUpdate
@DynamicInsert
public class CdDataStoreDefinition extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5026296420550665121L;
    @NotBlank
    @ApiModelProperty("名称")
    private String name;
    @NotBlank
    @ApiModelProperty("ID")
    private String id;
    @NotBlank
    @ApiModelProperty("编号")
    private String code;
    @NotBlank
    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("数据库表名称")
    private String tableName;
    @ApiModelProperty("数据库视图名称")
    private String viewName;
    @ApiModelProperty("实体名")
    private String entityName;
    @Length
    @ApiModelProperty("SQL语句")
    private String sqlStatement;

    @ApiModelProperty("数据接口名称")
    private String dataInterfaceName;

    @ApiModelProperty("数据接口参数")
    private String dataInterfaceParam;

    @ApiModelProperty("SQL名称")
    private String sqlName;

    // 使用驼峰风格列索引，对SQL命名查询、SQL语句、数据库表、数据库视图有效
    @ApiModelProperty("列索引")
    private Boolean camelColumnIndex;
    @Length
    @ApiModelProperty("默认条件")
    private String defaultCondition;
    @ApiModelProperty("默认排序")
    private String defaultOrder;
    @ApiModelProperty("列定义")
    private String columnsDefinition;
    @ApiModelProperty("模块Id")
    private String moduleId;

    private DataSourceType dbSourceType;
    private Long dbLinkConfUuid;

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
     * @return the camelColumnIndex
     */
    public Boolean getCamelColumnIndex() {
        return camelColumnIndex;
    }

    /**
     * @param camelColumnIndex 要设置的camelColumnIndex
     */
    public void setCamelColumnIndex(Boolean camelColumnIndex) {
        this.camelColumnIndex = camelColumnIndex;
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

    /**
     * @return the columnsDefinition
     */
    @SuppressWarnings("unchecked")
    @Transient
    public List<CdDataStoreColumnBean> getColumnDefinitionBeans() {

        if (StringUtils.isBlank(columnsDefinition)) {
            return new ArrayList<CdDataStoreColumnBean>();
        }
        return JsonUtils.gson2List(columnsDefinition, new TypeToken<List<CdDataStoreColumnBean>>() {
        }.getType());
//        return JSONArray.toList(JSONArray.fromObject(columnsDefinition),
//                CdDataStoreColumnBean.class);
    }

    @Transient
    public void setColumnsDefinitionBean(List<CdDataStoreColumnBean> beans) {
        columnsDefinition = JSONArray.fromObject(beans).toString();
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

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public DataSourceType getDbSourceType() {
        return dbSourceType;
    }

    public void setDbSourceType(DataSourceType dbSourceType) {
        this.dbSourceType = dbSourceType;
    }

    public Long getDbLinkConfUuid() {
        return dbLinkConfUuid;
    }

    public void setDbLinkConfUuid(Long dbLinkConfUuid) {
        this.dbLinkConfUuid = dbLinkConfUuid;
    }

    public static enum DataSourceType {
        INNER_DS, EXTERNAL_DS;
    }


}
